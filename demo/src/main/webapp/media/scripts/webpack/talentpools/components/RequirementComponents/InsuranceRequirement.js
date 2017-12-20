import fetch from 'isomorphic-fetch';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSelectField, WMMenuItem, WMCheckbox, WMTextField } from '@workmarket/front-end-components';
import * as actions from '../../actions';

class InsuranceRequirement extends React.Component {
	constructor (props) {
		super(props);
		this.state = {
			industryList: [],
			insuranceList: []
		};
	}

	componentDidMount () {
		this.fetchIndustries();
	}

	fetchIndustries = (urlRoot = '') => {
		return fetch(`${urlRoot}/industries-list`, { credentials: 'same-origin' })
		.then(res => res.json())
		.then(res => this.setState({ industryList: res }));
	}

	handleIndustryChange = (value) => {
		this.setState({ industrySelected: value });
		return this.fetchInsuranceList(value)
			.then((res) => {
				this.setState({ insuranceList: res.list });
			});
	}

	fetchInsuranceList = (value, urlRoot = '') => {
		return fetch(`${urlRoot}/profile-edit/insurancelist?industry=${value}`,
			{ headers: { Accept: 'application/json, text/javascript, */*; q=0.01', 'X-Requested-With': 'XMLHttpRequest' },
				credentials: 'same-origin',
				method: 'get'
			})
			.then((res) => {
				return res.ok ? res.json() : { list: [] };
			});
	}

	render () {
		const {
			handleChange,
			applyRequirement,
			insurance } = this.props;
		const { insuranceId,
			notifyOnExpiration,
			removeMembershipOnExpiration,
			minimumCoverageEnabled,
			minimumCoverageAmount } = insurance;
		const { industryList, industrySelected, insuranceList } = this.state;
		const renderIndustries = industryList.map((industryMap, index) =>
			<WMMenuItem
				key={ index }
				value={ industryMap.id }
				primaryText={ industryMap.name }
			/>
		);

		const renderInsurance = insuranceList.map((insuranceMap, index) =>
			<WMMenuItem
				key={ index }
				value={ insuranceMap.id }
				primaryText={ insuranceMap.name }
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
							data-component-identifier="requirements_insuranceRow"
							id="requirements-insurance"
							baseStyle={ { margin: '1em 0' } }
						>
							<WMSelectField
								data-component-identifier="requirements_insuranceSelect"
								onChange={ (event, index, value) => handleChange('insuranceId', value) }
								fullWidth
								name="insuranceId"
								hintText="Select Insurance"
								value={ insuranceId }
							>
								{ renderInsurance }
							</WMSelectField>
						</WMFormRow>
						<WMFormRow
							data-component-identifier="requirements_checkboxRow"
							id="requirements-checkbox"
							baseStyle={ { margin: '1em 0' } }
						>
							<div>
								<WMCheckbox
									name="minimumCoverageEnabled"
									id="minimum-coverage-toggle"
									checked={ minimumCoverageEnabled }
									onCheck={ (event, isInputChecked) => handleChange('minimumCoverageEnabled', null, isInputChecked) }
									label="Enable Minmium Coverage Amount"
								/>
								{ minimumCoverageEnabled &&
									<WMFormRow
										id="minimum-coverage-row"
										labelText="$"
										name="minimum-coverage"
									>
										<WMTextField
											id="minimum-coverage"
											defaultValue={ minimumCoverageAmount }
											onChange={ (event, value) => handleChange('minimumCoverageAmount', value) }
										/>
									</WMFormRow>
								}
								<WMCheckbox
									name="notifyOnExpiration"
									id="notify-on-expiry"
									checked={ notifyOnExpiration }
									onCheck={ (event, isInputChecked) => handleChange('notifyOnExpiration', null, isInputChecked) }
									label="Notify me when insurance expires"
								/>
								<WMCheckbox
									name="removeMembershipOnExpiration"
									id="remove-membership-on-expiry"
									checked={ removeMembershipOnExpiration }
									onCheck={ (event, isInputChecked) => handleChange('removeMembershipOnExpiration', null, isInputChecked) }
									label="Deactivate membership when insurance expires"
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
							style={ { margin: '1em 0 0 1em' } }
							disabled={ !insurance.insuranceId }
							onClick={ () => {
								const name = insuranceList.find(item => (item.id === insurance.insuranceId)).name;
								const formattedName = (minimumCoverageAmount !== null) ? `${name} $(${minimumCoverageAmount})` : name;
								const data = {
									$type: 'InsuranceRequirement',
									$humanTypeName: 'Insurance',
									minimumCoverage: minimumCoverageEnabled ? minimumCoverageAmount : null,
									notifyOnExpiry: notifyOnExpiration,
									removeMembershipOnExpiry: removeMembershipOnExpiration,
									requirable: {
										name: formattedName,
										id: insuranceId
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
	insurance: requirementsData.toJS().insurance
});

const mapDispatchToProps = (dispatch) => {
	return {
		handleChange: (fieldName, value, isInputChecked) => {
			const newValue = value || isInputChecked;
			dispatch(actions.changeDeepRequirementField('insurance', fieldName, newValue));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data, true));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(InsuranceRequirement);

InsuranceRequirement.propTypes = {
	handleChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	insurance: PropTypes.shape({
		insuranceId: PropTypes.number,
		notifyOnExpiration: PropTypes.bool,
		removeMembershipOnExpiration: PropTypes.bool
	})
};
