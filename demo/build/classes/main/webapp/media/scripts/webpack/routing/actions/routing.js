'use strict';

import * as types from '../constants/routingActionTypes';

const updateSmartRoute = (value) => {
	return {
		type: types.UPDATE_SMART_ROUTE,
		value
	};
};

const toggleShownInFeed = (value) => {
	return {
		type: types.TOGGLE_SHOWN_IN_FEED,
		value
	};
};

const toggleBrowseMarketplace = (value) => {
	return {
		type: types.TOGGLE_BROWSE_MARKETPLACE,
		value
	};
};

const updateValidity = (value) => {
	return {
		type: types.UPDATE_VALIDITY,
		value
	};
};

export default {
	updateSmartRoute,
	toggleShownInFeed,
	toggleBrowseMarketplace,
	updateValidity
};
