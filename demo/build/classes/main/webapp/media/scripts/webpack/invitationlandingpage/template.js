/* eslint-disable indent */
/* eslint-disable react/require-default-props */
import {
	WMPaper,
	WMRaisedButton,
	WMSelectField,
	WMValidatingTextField,
	WMValidatingForm,
	WMLink,
	WMMenuItem,
	WMMessageBanner,
	WMHeading
} from '@workmarket/front-end-components';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import WMCompanyPagesSPA from 'wm-company-pages-spa';
import Styles from './styles';
import Application from '../core';

const USER_TYPE = {
	INDIVIDUAL: 1,
	COMPANY: 2
};

const getFormData = object => Object.keys(object).reduce((formData, key) => {
	formData.append(key, object[key]);
	return formData;
}, new FormData());

class LandingPage extends Component {
	constructor (props) {
		super(props);

		this.state = {
			showForm: false,
			showError: false,
			errorMessage: '',
			firstName: '',
			lastName: '',
			email: '',
			password: '',
			companyName: '',
			industryId: '',
			userType: ''
		};
	}

	componentDidMount () {
		this.fetchIndustries();
	}

	onSubmit (form) {
		if (this.isCompany() && !this.props.isInvitation) {
			this.submitCompanyForm(form);
		} else {
			const REGISTER_URL = this.props.isInvitation ?
				`/register/invitation/${this.props.encryptedId}` :
				`/register/campaign/${this.props.encryptedId}`;
		fetch(REGISTER_URL, {
			method: 'POST',
			credentials: 'same-origin',
			body: getFormData(form),
			headers: {
				'X-CSRF-Token': Application.CSRFToken
			}
		})
		.then(response => response.json())
		.then((response) => {
			if (response && response.successful) {
				this.redirectToSuccessPage();
				} else {
					this.unexpectedException(response.results || response);
				}
		}).catch(() => {
			this.unexpectedException();
		});
		}
	}

	submitCompanyForm (form) {
		const COMPANY_REGISTER_URL = `/v2/employer/create-account/${this.props.encryptedId}`;
		fetch(COMPANY_REGISTER_URL, {
			method: 'POST',
			credentials: 'same-origin',
			body: JSON.stringify({ ...form, userEmail: form.email }),
			headers: {
				'X-CSRF-Token': Application.CSRFToken
			}
		})
			.then(response => response.json())
            .then((response) => {
				if (response && response.meta.code === 200) {
					this.redirectToSuccessPage();
				} else {
					this.unexpectedException(response.results || response);
				}
			}).catch(() => {
				this.unexpectedException();
			});
	}

	fetchIndustries = () => {
		return fetch('/api/v1/constants/industries', { credentials: 'same-origin' })
			.then((res) => {
				return res.ok ? res.json() : [];
			})
			.then((res) => {
				const industriesList = res.response.map(industry => (
					<WMMenuItem
						key={ industry.id }
						value={ industry.id }
						primaryText={ industry.name }
					/>
				));
				this.setState({ industries: industriesList });
			});
	}

	redirectToSuccessPage () {
			window.location = this.props.isInvitation ?
				`/register/thankyou/?i=${this.props.encryptedId}` :
				`/register/thankyou/?c=${this.props.encryptedId}`;
	}

	unexpectedException (unidentfiedError) {
		let errorMessage;
		if (Array.isArray(unidentfiedError)) {
			errorMessage = unidentfiedError.map(error => error.message).join(' ');
		} else {
			errorMessage = unidentfiedError.messages[0];
		}
        this.setState({
            showError: true,
            errorMessage: errorMessage || 'An unexpected error occurred.'
        });
    }

	toggleForm (userType) {
		if (this.state.showForm && this.state.userType !== userType) {
		this.setState({ showForm: true, userType });
			} else {
				this.setState({ showForm: !this.state.showForm, userType
			});
		}
	}

	isCompany () {
		return this.state.userType === USER_TYPE.COMPANY;
	}

	handleChange = (name, value) => {
		const updated = {};
		updated[name] = value;
		this.setState(updated);
	}

	handleIndustryChange = (event, index, value) => {
		this.setState({ industryId: value });
	}

	render () {
		return (
			<div>
				<WMPaper style={ Styles.landingPageHeader }>
					<div style={ Styles.landingPageText }>{ 'You\'re invited to join our network on Work Market.' }</div>
					<div style={ Styles.landingPageButtonContainer }>
						<div style={ Styles.buttonContainer } >
							<WMRaisedButton
								fullWidth
								onClick={ () => this.toggleForm(USER_TYPE.COMPANY) }
								label="Join as a company*"
								secondary
							/>
							<div style={ Styles.tinyText }>
								{ '* EIN required' }
							</div>
						</div>
						<div style={ Styles.buttonContainer }>
							<WMRaisedButton
								fullWidth
								onClick={ () => this.toggleForm(USER_TYPE.INDIVIDUAL) }
								label="Join as an individual"
								secondary
							/>
						</div>
						<div style={ Styles.tos }>
							By joining, you agree to our <WMLink href="/tos">Terms of Service</WMLink> and <WMLink href="/privacy">Privacy Policy</WMLink>.
						</div>
					</div>
				</WMPaper>
				<WMPaper
					transitionEnabled
					style={ this.state.showForm ? Styles.formContainer : Styles.hideForm }
				>
					<WMHeading level="3">Sign Up for Work Market</WMHeading>
					{ this.state.showError &&
						<WMMessageBanner
							hideDismiss
							status={ 'error' }
						>
							<p style={ Styles.landingPageError }>{ this.state.errorMessage }</p>
						</WMMessageBanner>
					}
					<WMValidatingForm
						onSubmit={ form => this.onSubmit(form) }
						submitText="Register"
					>
						<WMValidatingTextField
							required
							fullWidth
							floatingLabelText="First Name"
							id="firstname"
							name="firstName"
							errorName="first name"
							value={ this.state.firstName }
							onChange={ this.handleChange }
						/>
						<WMValidatingTextField
							required
							fullWidth
							floatingLabelText="Last Name"
							id="lastname"
							name="lastName"
							errorName="last name"
							value={ this.state.lastName }
							onChange={ this.handleChange }
						/>
						<WMValidatingTextField
							required
							fullWidth
							criteria="email"
							floatingLabelText={ `${(this.isCompany() ? 'Company' : '')} Email` }
							id="email"
							name="email"
							errorName={ `${(this.isCompany() ? 'company' : '')} email` }
							value={ this.state.email }
							onChange={ this.handleChange }
						/>
						<WMValidatingTextField
							required
							fullWidth
							floatingLabelText="Password"
							hintText="Min 8 characters, at least 1 number"
							min={ 8 }
							id="password"
							name="password"
							type="password"
							errorName="password"
							value={ this.state.password }
							onChange={ this.handleChange }
						/>
						{ this.isCompany() &&
							<div>
								<WMValidatingTextField
									required
									fullWidth
									floatingLabelText="Company Name"
									id="companyName"
									name="companyName"
									errorName="company name"
									value={ this.state.companyName }
									onChange={ this.handleChange }
								/>
								<WMSelectField
									required
									id="industryId"
									name="industryId"
									fullWidth
									floatingLabelText="Choose Industry"
									errorName="industry"
									value={ this.state.industryId }
									onChange={ this.handleIndustryChange }
								>
									{ this.state.industries }
								</WMSelectField>
							</div>
						}
					</WMValidatingForm>
				</WMPaper>
				{ this.props.isInvitation !== true &&
				<WMPaper style={ Styles.descriptionContainer }>
					<div
						dangerouslySetInnerHTML={ // eslint-disable-line react/no-danger
							{ __html: this.props.campaignText }
						}
					/> {
					}
				</WMPaper>
				}
				<WMCompanyPagesSPA
					compact
					isPublic
					companyNumber={ this.props.companyNumber }
					csrf={ this.props.csrf }
				/>
			</div>
		);
	}
}

LandingPage.propTypes = {
	encryptedId: PropTypes.string,
	isInvitation: PropTypes.bool,
	csrf: PropTypes.string,
	campaignText: PropTypes.string,
	companyNumber: PropTypes.string
};

export default LandingPage;
