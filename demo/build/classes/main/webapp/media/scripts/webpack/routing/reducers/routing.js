'use strict';
import { combineReducers } from 'redux';
import * as types from '../constants/routingActionTypes';
import FirstToAcceptReducer from './firstToAccept';
import NeedToApplyReducer from './needToApply';

export const smartRoute = (state = false, { type, value }) => {
	switch (type) {
		case types.UPDATE_SMART_ROUTE:
			return value;
		default:
			return state;
	}
};

export const shownInFeed = (state = false, { type }) => {
	switch (type) {
		case types.TOGGLE_SHOWN_IN_FEED:
			return !state;
		default:
			return state;
	}
};

export const browseMarketplace = (state = false, { type, value }) => {
	switch (type) {
		case types.TOGGLE_BROWSE_MARKETPLACE:
			return value;
		default:
			return state;
	}
};

export const isValid = (state = false, { type, value }) => {
	switch (type) {
		case types.UPDATE_VALIDITY:
			return value;
		default:
			return state;
	}
};

export default combineReducers({
	smartRoute,
	shownInFeed,
	browseMarketplace,
	isValid,
	firstToAcceptCandidates: FirstToAcceptReducer,
	needToApplyCandidates: NeedToApplyReducer
});
