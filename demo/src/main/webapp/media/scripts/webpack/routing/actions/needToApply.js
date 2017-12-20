'use strict';

import * as types from '../constants/needToApplyActionTypes';

const updateGroupIds = (value) => {
	return {
		type: types.UPDATE_NEED_TO_APPLY_GROUP_IDS,
		value
	};
};

const updateResourceNumbers = (value) => {
	return {
		type: types.UPDATE_NEED_TO_APPLY_RESOURCE_NUMBERS,
		value
	};
};

const updateVendorNumbers = (value) => {
	return {
		type: types.UPDATE_NEED_TO_APPLY_VENDOR_NUMBERS,
		value
	};
};

const clearInvitees = () => {
	return {
		type: types.CLEAR_INVITEES
	};
};

export default {
	updateGroupIds,
	updateResourceNumbers,
	updateVendorNumbers,
	clearInvitees
};
