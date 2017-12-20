import $ from 'jquery';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSelectField, WMStateProvince, WMMenuItem, WMCheckbox } from '@workmarket/front-end-components';
import * as actions from '../../actions';

class LicenseRequirement extends React.Component {
	constructor (props) {
		super(props);
		this.state = {
			licenseList: []
		};
		this.handleStateChange = this.handleStateChange.bind(this);
	}

	handleStateChange (value) {
		this.setState({ stateSelected: value });
		this.fetchStateLicenses(value);
	}

	fetchStateLicenses (value, urlRoot = '') {
		// TODO: @artivilla investigate why fetch isn't working
		$.getJSON(`${urlRoot}/profile-edit/licenselist?state=${value}`, (res) => {
			this.setState({ licenseList: res });
		});
	}

	render () {
		const {
			handleChange,
			applyRequirement,
			license } = this.props;
		const {
			licenseList,
			stateSelected } = this.state;
		const { licenseId, notifyOnExpiration, removeMembershipOnExpiration } = license;
		const renderLicences = licenseList.map((licenseMap, index) =>
			<WMMenuItem
				key={ index }
				value={ licenseMap.id }
				primaryText={ licenseMap.name }
			/>
		);

		return (
			<div>
				<WMFormRow
					data-component-identifier="requirements_stateRow"
					id="requirements-state"
					baseStyle={ { margin: '1em 0' } }
				>
					<WMStateProvince
						data-component-identifier="requirements_stateSelect"
						locale="us"
						hintText="Select State"
						hide="abbreviation"
						onChange={ (event, index, value) => this.handleStateChange(value) }
						value={ stateSelected }
					/>
				</WMFormRow>
				{ stateSelected &&
					<div>
						<WMFormRow
							data-component-identifier="requirements_licenseRow"
							id="requirements-license"
							baseStyle={ { margin: '1em 0' } }
						>
							<WMSelectField
								data-component-identifier="requirements_licenseSelect"
								onChange={ (event, index, value) => handleChange('licenseId', value) }
								fullWidth
								name="licenseId"
								hintText="Select License"
								value={ licenseId }
							>
								{ renderLicences }
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
									label="Notify me when license expires"
								/>
								<WMCheckbox
									name="removeMembershipOnExpiration"
									id="remove-membership-on-expiry"
									checked={ removeMembershipOnExpiration }
									onCheck={ (event, isInputChecked) => handleChange('removeMembershipOnExpiration', null, isInputChecked) }
									label="Deactivate membership when license expires"
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
							disabled={ !license.licenseId }
							onClick={ () => {
								const data = {
									$type: 'LicenseRequirement',
									$humanTypeName: 'License',
									notifyOnExpiry: notifyOnExpiration,
									removeMembershipOnExpiry: removeMembershipOnExpiration,
									requirable: {
										name: `${stateSelected} - ${licenseList.find(item => (item.id === license.licenseId)).name}`,
										id: licenseId
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
	license: requirementsData.toJS().license
});

const mapDispatchToProps = (dispatch) => {
	return {
		handleChange: (fieldName, value, isInputChecked) => {
			const newValue = value || isInputChecked;
			dispatch(actions.changeDeepRequirementField('license', fieldName, newValue));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data, true));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(LicenseRequirement);

LicenseRequirement.propTypes = {
	handleChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	license: PropTypes.shape({
		licenseId: PropTypes.number,
		notifyOnExpiration: PropTypes.bool,
		removeMembershipOnExpiration: PropTypes.bool
	})
};
