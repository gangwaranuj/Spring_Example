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
		onChangeField: (name, value) => {
			dispatch(actions.changeField(name, value));
		},
		onSubmitTalentPoolForm: (formData) => {
			dispatch(actions.submitTalentPoolCreateForm(formData));
		},
		onCloseDrawer: () => {
			dispatch(actions.closeDrawer());
			dispatch(actions.routeTalentPools('/groups'));
		}
	};
};

const TalentPoolCreateForm = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default connect()(TalentPoolCreateForm);
