import fetch from 'isomorphic-fetch';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSelectField, WMMenuItem, WMCheckbox } from '@workmarket/front-end-components';
import * as actions from '../../actions';

class CertificationRequirement extends React.Component {
	constructor (props) {
		super(props);
		this.state = {
			industryList: [],
			providerList: [],
			certificationList: []
		};
	}

	componentDidMount () {
		this.fetchIndustries();
	}

	fetchIndustries = (urlRoot = '') => {
		return fetch(`${urlRoot}/industries-list`, { credentials: 'same-origin' })
		.then((res) => {
			return res.ok ? res.json() : [];
		})
		.then(res => this.setState({ industryList: res }));
	}

	handleIndustryChange = (value) => {
		this.setState({ industrySelected: value });
		return this.fetchProvidersList(value)
			.then((res) => {
				this.setState({ providerList: res });
			});
	}

	handleProviderChange = (value) => {
		this.setState({ providerSelected: value });
		return this.fetchCertificationList(value, this.state.industrySelected)
			.then((res) => {
				this.setState({ certificationList: res.list });
			});
	}

	fetchProvidersList = (value, urlRoot = '') => {
		return fetch(`${urlRoot}/profile-edit/certificationslist?industry=${value}`,
			{ headers: { Accept: 'application/json, text/javascript, */*; q=0.01', 'X-Requested-With': 'XMLHttpRequest' },
				credentials: 'same-origin',
				method: 'get'
			})
			.then((res) => {
				return res.ok ? res.json() : [];
			});
	}

	fetchCertificationList = (value, industrySelected, urlRoot = '') => {
		return fetch(`${urlRoot}/profile-edit/certificationslist?industry=${industrySelected}&provider=${value}`,
			{ headers: { Accept: 'application/json, text/javascript, */*; q=0.01', 'X-Requested-With': 'XMLHttpRequest' },
				credentials: 'same-origin',
				method: 'get'
			})
			.then((res) => {
				return res.ok ? res.json() : [];
			});
	}

	render () {
		const {
			handleChange,
			applyRequirement,
			certification } = this.props;
		const {
			certificationId, notifyOnExpiration, removeMembershipOnExpiration
		} = certification;
		const { industryList,
						industrySelected,
						providerList,
						providerSelected,
						certificationList } = this.state;
		const renderIndustries = industryList.map((industryMap, index) =>
			<WMMenuItem
				key={ index }
				value={ industryMap.id }
				primaryText={ industryMap.name }
			/>
		);

		const renderProvider = providerList.map((providerMap, index) =>
			<WMMenuItem
				key={ index }
				value={ providerMap.id }
				primaryText={ providerMap.name }
			/>
		);

		const renderCertifications = certificationList.map((certificationMap, index) =>
			<WMMenuItem
				key={ index }
				value={ certificationMap.id }
				primaryText={ certificationMap.name }
			/>
		);

		return (
			<div>
				<WMFormRow
					data-component-identifier="requirements_industryRow"
					id="requirements-industry"
					baseStyle={ { margin: '1em 0' } }
				>
					<WMSelectField
						data-component-identifier="requirements_industrySelect"
						onChange={ (event, index, value) => this.handleIndustryChange(value) }
						fullWidth
						name="industry"
						hintText="Select Industry"
						value={ industrySelected }
					>
						{ renderIndustries }
					</WMSelectField>
				</WMFormRow>
				{ industrySelected &&
					<div>
						<WMFormRow
							data-component-identifier="requirements_providerRow"
							id="requirements-provider"
							baseStyle={ { margin: '1em 0' } }
						>
							<WMSelectField
								data-component-identifier="requirements_providerSelect"
								onChange={ (event, index, value) => this.handleProviderChange(value) }
								fullWidth
								name="providerId"
								hintText="Select Provider"
								value={ providerSelected }
							>
								{ renderProvider }
							</WMSelectField>
						</WMFormRow>
					</div>
				}
				{ industrySelected && providerSelected &&
					<div>
						<WMFormRow
							data-component-identifier="requirements_certificationRow"
							id="requirements-certificate"
							baseStyle={ { margin: '1em 0' } }
						>
							<WMSelectField
								data-component-identifier="requirements_certificationSelect"
								onChange={ (event, index, value) => handleChange('certificationId', value) }
								fullWidth
								name="certificationId"
								hintText="Select Certificate"
								value={ certificationId }
							>
								{ renderCertifications }
							</WMSelectField>
						</WMFormRow>
						<WMFormRow
							data-component-identifier="requirements_checkboxRow"
							id="requirements-checkbox"
							baseStyle={ { margin: '1em 0' } }
						>
							<div>
								<WMCheckbox
									name="notifyOnExpiration"
									id="notify-on-expiry"
									checked={ notifyOnExpiration }
									onCheck={ (event, isInputChecked) => handleChange('notifyOnExpiration', null, isInputChecked) }
									label="Notify me when certification expires"
								/>
								<WMCheckbox
									name="removeMembershipOnExpiration"
									id="remove-membership-on-expiry"
									checked={ removeMembershipOnExpiration }
									onCheck={ (event, isInputChecked) => handleChange('removeMembershipOnExpiration', null, isInputChecked) }
									label="Deactivate membership when certification expires"
								/>
							</div>
						</WMFormRow>
					</div>
				}
				<WMFormRow
					data-component-identifier="requirements_buttonRow"
					id="requirements-buttons"
					baseStyle={ { margin: '1em 0' } }
				>
					<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
						<WMRaisedButton
							secondary
							data-component-identifier="requirements_buttons"
							label="Add this requirement"
							disabled={ !certificationId }
							onClick={ () => {
								const data = {
									$type: 'CertificationRequirement',
									$humanTypeName: 'Certification',
									notifyOnExpiry: notifyOnExpiration,
									removeMembershipOnExpiry: removeMembershipOnExpiration,
									requirable: {
										name: `${providerList.find(item => (item.id === providerSelected)).name} - ${certificationList.find(item => (item.id === certificationId)).name}`,
										id: certificationId
									}
								};
								applyRequirement(data);
							} }
						/>
					</div>
				</WMFormRow>
			</div>
		);
	}
}

const mapStateToProps = ({ requirementsData }) => ({
	certification: requirementsData.toJS().certification
});

const mapDispatchToProps = (dispatch) => {
	return {
		handleChange: (fieldName, value, isInputChecked) => {
			const newValue = value || isInputChecked;
			dispatch(actions.changeDeepRequirementField('certification', fieldName, newValue));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data, true));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(CertificationRequirement);

CertificationRequirement.propTypes = {
	handleChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	certification: PropTypes.shape({
		certificationId: PropTypes.number,
		notifyOnExpiration: PropTypes.bool,
		removeMembershipOnExpiration: PropTypes.bool
	})
};
