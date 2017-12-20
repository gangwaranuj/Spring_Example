'use strict';

import { combineReducers } from 'redux';
import * as types from '../constants/needToApplyActionTypes';

const groupIds = (state = [], { type, value }) => {
	switch (type) {
		case types.UPDATE_NEED_TO_APPLY_GROUP_IDS:
			return value;
		case types.CLEAR_INVITEES:
			return [];
		default:
			return state;
	}
};

const resourceNumbers = (state = [], { type, value }) => {
	switch (type) {
		case types.UPDATE_NEED_TO_APPLY_RESOURCE_NUMBERS:
			return value;
		case types.CLEAR_INVITEES:
			return [];
		default:
			return state;
	}
};

const vendorCompanyNumbers = (state = [], { type, value }) => {
	switch (type) {
		case types.UPDATE_NEED_TO_APPLY_VENDOR_NUMBERS:
			return value;
		case types.CLEAR_INVITEES:
			return [];
		default:
			return state;
	}
};

export default combineReducers({
	groupIds,
	resourceNumbers,
	vendorCompanyNumbers
});
