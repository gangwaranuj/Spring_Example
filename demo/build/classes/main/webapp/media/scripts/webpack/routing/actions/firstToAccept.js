'use strict';

import * as types from '../constants/firstToAcceptActionTypes';

const updateGroupIds = (value) => {
	return {
		type: types.UPDATE_FIRST_TO_ACCEPT_GROUP_IDS,
		value
	};
};

const updateResourceNumbers = (value) => {
	return {
		type: types.UPDATE_FIRST_TO_ACCEPT_RESOURCE_NUMBERS,
		value
	};
};

const updateVendorNumbers = (value) => {
	return {
		type: types.UPDATE_FIRST_TO_ACCEPT_VENDOR_NUMBERS,
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
