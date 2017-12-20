import { connect } from 'react-redux';
import * as actions from '../../actions';
import template from './template';

const mapStateToProps = ({ rootData }) => ({
	talentPoolData: rootData
});

const mapDispatchToProps = dispatch => ({
	onChangeTab: (tab, id) => {
		dispatch(actions.changeTab(tab, id));
	},
	onCloseDrawer: () => {
		dispatch(actions.clearMessages());
		dispatch(actions.closeDrawer());
		dispatch(actions.routeTalentPools('/groups'));
	},
	switchToInvite: (inviting, id) => {
		dispatch(actions.showInviteFlow(inviting, id));
	}
});

const TalentPoolManage = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default TalentPoolManage;
