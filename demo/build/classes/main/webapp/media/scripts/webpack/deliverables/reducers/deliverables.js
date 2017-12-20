'use strict';

import { combineReducers } from 'redux';
import * as types from '../constants/actionTypes';
import DeliverableReducer from './deliverable';

const id = (state = null) => {
	return state;
};

const instructions = (state = '', action) => {
	switch (action.type) {
		case types.UPDATE_DELIVERABLES_INSTRUCTIONS:
			return action.value;
		default:
			return state;
	}
};

const hoursToComplete = (state = 24, action) => {
	switch (action.type) {
		case types.UPDATE_DELIVERABLES_TIME:
			return ~~action.value;
		default:
			return state;
	}
};

export default combineReducers({
	id,
	instructions,
	hoursToComplete,
	deliverables: DeliverableReducer
});
