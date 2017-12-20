import { connect } from 'react-redux';
import * as actions from '../../actions';
import template from './template';

const mapStateToProps = ({ rootData, requirementsData }) => ({
	talentPoolData: requirementsData,
	hasESignatureEnabled: rootData.get('hasESignatureEnabled')
});

const mapDispatchToProps = (dispatch) => {
	return {
		removeRequirement: (requirementKey, requirementValue) => {
			dispatch(actions.removeRequirement(requirementKey, requirementValue));
		},
		toggleRequirementNotifyOnExpiry: (requirementKey, requirementValue) => {
			dispatch(actions.toggleRequirementNotifyOnExpiry(requirementKey, requirementValue));
		},
		toggleRequirementRemoveOnExpiry: (requirementKey, requirementValue) => {
			dispatch(actions.toggleRequirementRemoveOnExpiry(requirementKey, requirementValue));
		},
		setActiveRequirementType: (type) => {
			dispatch(actions.setActiveRequirementType(type));
		},
		onToggleActivateAutomaticEnforcement: (group, enabled) => {
			dispatch(actions.toggleActivateAutomaticEnforcement(group, enabled));
		},
		saveRequirementSet: (requirementSet, requirementsModel, groupId) => {
			dispatch(
				actions.saveRequirementSet(requirementSet.toJS(), requirementsModel.toJS(), groupId)
			);
		}
	};
};

const TalentPoolRequirements = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default TalentPoolRequirements;
