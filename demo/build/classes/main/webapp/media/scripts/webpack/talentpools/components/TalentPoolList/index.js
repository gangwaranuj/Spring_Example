import { connect } from 'react-redux';
import * as actions from '../../actions';
import template from './template';

const mapStateToProps = ({ rootData }) => ({
	talentPoolData: rootData
});

const mapDispatchToProps = (dispatch) => {
	return {
		onToggleActive: (id, name, isActive) => {
			dispatch(actions.toggleActive(id, name, isActive));
		},
		onOpenProfileModal: (userNumber) => {
			dispatch(actions.openProfileModal(userNumber));
		},
		onManageGroup: (group, memberTabMode) => {
			dispatch(actions.manageGroup(group.toJS(), memberTabMode));
			if (memberTabMode === 'member' || memberTabMode === 'pending' || memberTabMode === 'invited') {
				dispatch(actions.changeTab('members', group.get('id')));
			}
		},
		fetchTalentPools: (isUpdating) => {
			dispatch(actions.fetchTalentPools(isUpdating));
		},
		newTalentPool: () => {
			dispatch(actions.newTalentPool());
			dispatch(actions.routeTalentPools('groups/create'));
		},
		onSort: (talentPools, sortField, sortDir, isNumeric = false, toggleDirection = false) => {
			dispatch(actions.sortTalentPools(
				talentPools.toJS(),
				sortField,
				sortDir,
				isNumeric,
				toggleDirection));
		},
		onCloseDrawer: () => {
			dispatch(actions.clearMessages());
			dispatch(actions.closeDrawer());
			dispatch(actions.routeTalentPools('/groups'));
		}
	};
};

const TalentPoolList = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default TalentPoolList;
