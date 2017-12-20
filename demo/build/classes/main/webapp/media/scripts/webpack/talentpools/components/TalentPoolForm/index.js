import { connect } from 'react-redux';
import * as actions from '../../actions';
import template from './template';

const mapStateToProps = ({ formData }) => ({
	talentPoolFormData: formData,
	defaultOrgUnitUuid: formData.get('orgMode') && formData.get('orgMode').get('uuid')
});

const mapDispatchToProps = (dispatch) => {
	return {
		onFetchSkill: (value) => {
			dispatch(actions.fetchSuggestedSkills(value));
		},
		onChangeSkillsField: (value) => {
			dispatch(actions.onChangeSkillsField(value));
		},
		onRemoveSkillsField: (value) => {
			dispatch(actions.onRemoveSkillsField(value));
		},
		onRemoveOrgUnit: (orgUnitMeta) => {
			dispatch(actions.removeOrgUnit(orgUnitMeta));
		},
		onToggleActive: (id, name, isActive) => {
			dispatch(actions.toggleActive(id, name, isActive));
		},
		onChangeField: (name, value) => {
			dispatch(actions.changeField(name, value));
		},
		onSubmitTalentPoolForm: (formData) => {
			dispatch(actions.submitTalentPoolForm(formData.toObject()));
		},
		onCloseDrawer: () => {
			dispatch(actions.clearMessages());
			dispatch(actions.closeDrawer());
		},
		onDeleteTalentPool: () => {
			dispatch(actions.deleteTalentPool());
		},
		onDeleteTalentPoolConfirm: (id) => {
			dispatch(actions.deleteTalentPoolConfirm(id));
		},
		onDeleteTalentPoolCancel: () => {
			dispatch(actions.deleteTalentPoolCancel());
		}
	};
};

const TalentPoolForm = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default connect()(TalentPoolForm);
