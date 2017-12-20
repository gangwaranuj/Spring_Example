'use strict';

import { connect } from 'react-redux';
import Component from '../components/add_routing';
import RoutingActions from '../actions/routing';
import FirstToAcceptActions from '../actions/firstToAccept';
import NeedToApplyActions from '../actions/needToApply';

const mapStateToProps = ({ routing, pricing }) => ({
	routing,
	pricing
});

const mapDispatchToProps = (dispatch) => {
	return {
		updateFirstToAcceptGroupIds: (value) => dispatch(FirstToAcceptActions.updateGroupIds(value)),
		updateFirstToAcceptResourceNumbers: (value) => dispatch(FirstToAcceptActions.updateResourceNumbers(value)),
		updateFirstToAcceptVendorNumbers: (value) => dispatch(FirstToAcceptActions.updateVendorNumbers(value)),
		updateNeedToApplyGroupIds: (value) => dispatch(NeedToApplyActions.updateGroupIds(value)),
		updateNeedToApplyResourceNumbers: (value) => dispatch(NeedToApplyActions.updateResourceNumbers(value)),
		updateNeedToApplyVendorNumbers: (value) => dispatch(NeedToApplyActions.updateVendorNumbers(value)),
		clearInvitees: () => {
			dispatch(FirstToAcceptActions.clearInvitees());
			dispatch(NeedToApplyActions.clearInvitees());
		},
		updateSmartRoute: (value) => dispatch(RoutingActions.updateSmartRoute(value)),
		toggleShownInFeed: () => dispatch(RoutingActions.toggleShownInFeed()),
		toggleBrowseMarketplace: (value) => dispatch(RoutingActions.toggleBrowseMarketplace(value)),
		updateValidity: (value) => dispatch(RoutingActions.updateValidity(value))
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(Component);
