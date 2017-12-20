import { Map } from 'immutable';
import PropTypes from 'prop-types';
import React from 'react';
import {
	commonStyles,
	WMFontIcon,
	WMRaisedButton,
	WMList,
	WMListItem,
	WMMenuItem,
	WMSelectField,
	WMPaper,
	WMFieldSet,
	WMRadioButtonGroup,
	WMRadioButton,
	WMFormRow
} from '@workmarket/front-end-components';
import * as requirementTypes from '../../constants/requirementTypes';
import AbandonedRateRequirement from '../RequirementComponents/AbandonedRateRequirement';
import AgreementRequirement from '../RequirementComponents/AgreementRequirement';
import AssessmentsRequirement from '../RequirementComponents/AssessmentsRequirement';
import AvailabilityRequirement from '../RequirementComponents/AvailabilityRequirement';
import BackgroundCheckRequirement from '../RequirementComponents/BackgroundCheckRequirement';
import CancelledRateRequirement from '../RequirementComponents/CancelledRateRequirement';
import CompanyTypeRequirement from '../RequirementComponents/CompanyTypeRequirement';
import CertificationRequirement from '../RequirementComponents/CertificationRequirement';
import CountryRequirement from '../RequirementComponents/CountryRequirement';
import DeliverableOnTimeRequirement from '../RequirementComponents/DeliverableOnTimeRequirement';
import DocumentRequirement from '../RequirementComponents/DocumentRequirement';
import DrugTestRequirement from '../RequirementComponents/DrugTestRequirement';
import IndustryRequirement from '../RequirementComponents/IndustryRequirement';
import InsuranceRequirement from '../RequirementComponents/InsuranceRequirement';
import LicenseRequirement from '../RequirementComponents/LicenseRequirement';
import SignatureRequirement from '../RequirementComponents/SignatureRequirement';
import OnTimeRequirement from '../RequirementComponents/OnTimeRequirement';
import ProfileVideoRequirement from '../RequirementComponents/ProfileVideoRequirement';
import SatisfactionRatingRequirement from '../RequirementComponents/SatisfactionRatingRequirement';
import TalentPoolMembershipsRequirement from '../RequirementComponents/TalentPoolMembershipsRequirement';
import TravelDistanceRequirement from '../RequirementComponents/TravelDistanceRequirement';
import WorkerTypeRequirement from '../RequirementComponents/WorkerTypeRequirement';

const TalentPoolRequirements = ({
	removeRequirement,
	toggleRequirementNotifyOnExpiry,
	toggleRequirementRemoveOnExpiry,
	setActiveRequirementType,
	onToggleActivateAutomaticEnforcement,
	saveRequirementSet,
	talentPoolData,
	hasESignatureEnabled
}) => {
	const requirementComponents = {
		AbandonedRateRequirement,
		AgreementRequirement,
		AssessmentsRequirement,
		AvailabilityRequirement,
		BackgroundCheckRequirement,
		CancelledRateRequirement,
		CertificationRequirement,
		CompanyTypeRequirement,
		CountryRequirement,
		DeliverableOnTimeRequirement,
		DocumentRequirement,
		DrugTestRequirement,
		IndustryRequirement,
		InsuranceRequirement,
		LicenseRequirement,
		OnTimeRequirement,
		ProfileVideoRequirement,
		SatisfactionRatingRequirement,
		SignatureRequirement,
		TalentPoolMembershipsRequirement,
		TravelDistanceRequirement,
		WorkerTypeRequirement
	};
	const requirementTypeList = Object.keys(requirementTypes)
	.filter(requirementTypeKey => (requirementTypeKey !== 'ESIGNATURES' || hasESignatureEnabled))
	.map((requirementTypeKey) => {
		const requirement = requirementTypes[requirementTypeKey];
		return (
			<WMMenuItem
				key={ requirementTypeKey }
				value={ requirement.componentName }
				primaryText={ requirement.name }
			/>);
	});
	const activeRequirements = [];
	const requirements = talentPoolData.get('requirements') ? talentPoolData.get('requirements').toJS() : [];
	const disabledIcon = (
		<WMFontIcon
			id="talent-pool-delete-requirement"
			className="material-icons"
			color={ commonStyles.colors.baseColors.grey }
		>
			check_box_outline_blank
		</WMFontIcon>
	);
	const enabledIcon = (
		<WMFontIcon
			id="talent-pool-delete-requirement"
			className="material-icons"
			color={ talentPoolData.get('readOnly') ? commonStyles.colors.baseColors.grey : commonStyles.colors.baseColors.orange }
		>
			check_box
		</WMFontIcon>
	);
	requirements.forEach((requirement, index) => {
		const trashIcon =
			(
				<a
					onClick={ () =>
						removeRequirement(requirement.$type, requirement.name || requirement.requirable.name)
					}
				>
					<WMFontIcon
						id="talent-pool-delete-requirement"
						className="material-icons"
						color={ commonStyles.colors.baseColors.lightGrey }
						hoverColor={ commonStyles.colors.baseColors.grey }
					>
						delete
					</WMFontIcon>
				</a>
			);
		const nestedItems = [];
		if (requirement.notifyOnExpiry !== undefined) {
			let notifyIcon = '';
			let removeIcon = '';
			if (talentPoolData.get('readOnly')) {
				notifyIcon = requirement.notifyOnExpiry === true ? enabledIcon : disabledIcon;
				removeIcon = requirement.removeMembershipOnExpiry === true ? enabledIcon : disabledIcon;
			} else {
				notifyIcon = (
					<a
						onClick={ () =>
							toggleRequirementNotifyOnExpiry(
								requirement.$type,
								requirement.name || requirement.requirable.name
							)
						}
					>
						{ requirement.notifyOnExpiry === true ? enabledIcon : disabledIcon }
					</a>);
				removeIcon = (
					<a
						onClick={ () =>
							toggleRequirementRemoveOnExpiry(
								requirement.$type,
								requirement.name || requirement.requirable.name
							)
						}
					>
						{ requirement.removeMembershipOnExpiry === true ? enabledIcon : disabledIcon }
					</a>);
			}
			nestedItems.push(
				<WMListItem
					leftIcon={ notifyIcon }
					primaryText={ `Notify me when ${requirement.$humanTypeName} expires` }
				/>
			);
			nestedItems.push(
				<WMListItem
					leftIcon={ removeIcon }
					primaryText={ `Deactivate membership when ${requirement.$humanTypeName} expires` }
				/>
			);
		}
		let requirementString = '';
		if (requirement.$humanTypeName) {
			requirementString = `${requirement.$humanTypeName}: `;
		}
		if (requirement.requirable) {
			requirementString += requirement.requirable.name;
		} else {
			requirementString += requirement.name;
		}
		activeRequirements.push(
			<WMListItem
				key={ index } // eslint-disable-line react/no-array-index-key
				rightIcon={ !talentPoolData.get('readOnly') ? trashIcon : null }
				primaryText={ requirementString }
				nestedItems={ nestedItems }
				primaryTogglesNestedList
			/>
		);
	});
	let ActiveRequirementComponent = null;
	if (talentPoolData.get('activeRequirementType') !== '') {
		ActiveRequirementComponent = requirementComponents[talentPoolData.get('activeRequirementType')];
	}

	const emptyState = '';

	const hasRequirementsChanged = (a, b) => {
		if (a === b) return true;
		if (a == null || b == null) return false;
		if (a.size !== b.size) return false;
		if (!a.equals(b)) return false;
		return true;
	};

	return (
		<WMPaper style={ { width: '100%', padding: '1em' } } >
			<WMFieldSet text="Talent Pool Requirements" >
				<div style={ { margin: '1em 0' } } >
					<WMFormRow
						labelText="Requirements"
						id="talent-pool-requirement-add"
					>
						{ !talentPoolData.get('readOnly') &&
							<WMSelectField
								fullWidth
								style={ { margin: '-1.5em 0 0 0' } }
								name="requirementAdd"
								value={ talentPoolData.get('activeRequirementType') }
								onChange={ (event, index, value) => setActiveRequirementType(value) }
								floatingLabelText="Choose Requirement Type to Add"
							>
								{ requirementTypeList }
							</WMSelectField>
						}
					</WMFormRow>
				</div>

				<div>
					{ (talentPoolData.get('activeRequirementType') !== '') ? <ActiveRequirementComponent /> : null }
				</div>

				<WMFormRow id="talent-pool-active-requirements">
					<WMList>
						{ activeRequirements.length > 0 ? activeRequirements : emptyState }
					</WMList>
				</WMFormRow>

				<div style={ { margin: '1em 0' } } >
					{ !talentPoolData.get('readOnly') &&
					<WMFormRow id="talent-pools-save-requirements" >
						<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
							<WMRaisedButton
								primary
								disabled={ hasRequirementsChanged(talentPoolData.get('requirements'), talentPoolData.get('requirementsLoad')) }
								style={ { margin: '1em 0 0 1em' } }
								label="Save Requirements"
								onClick={ () => saveRequirementSet(talentPoolData.get('requirements'), talentPoolData.get('requirementsModel'), talentPoolData.get('id')) }
							/>
						</div>
					</WMFormRow>
					}
					<WMFormRow
						baseStyle={ { margin: '1em 0', alignItems: 'baseline' } }
						id="talent-pool-auto-enforce"
						labelText="Remove members when requirements are not met"
					>
						<WMRadioButtonGroup
							name="autoEnforce"
							onChange={ (event, value) => onToggleActivateAutomaticEnforcement(talentPoolData.get('id'), value) }
							valueSelected={ talentPoolData.get('autoEnforce') }
						>
							<WMRadioButton
								name="autoEnforce"
								label="Yes"
								value="true"
								disabled={ talentPoolData.get('readOnly') }
							/>
							<WMRadioButton
								name="autoEnforce"
								label="No"
								value="false"
								disabled={ talentPoolData.get('readOnly') }
							/>
						</WMRadioButtonGroup>
					</WMFormRow>
				</div>
			</WMFieldSet>
		</WMPaper>

	);
};

export default TalentPoolRequirements;

TalentPoolRequirements.propTypes = {
	removeRequirement: PropTypes.func.isRequired,
	toggleRequirementNotifyOnExpiry: PropTypes.func.isRequired,
	toggleRequirementRemoveOnExpiry: PropTypes.func.isRequired,
	setActiveRequirementType: PropTypes.func.isRequired,
	onToggleActivateAutomaticEnforcement: PropTypes.func.isRequired,
	saveRequirementSet: PropTypes.func.isRequired,
	talentPoolData: PropTypes.instanceOf(Map), // eslint-disable-line react/require-default-props,
	hasESignatureEnabled: PropTypes.bool.isRequired
};
