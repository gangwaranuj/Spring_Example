import { combineReducers } from 'redux';
import * as types from '../constants/actionTypes';
import approvals from './approvalReducer';

const showConfig = (state = false, { type }) => {
	switch (type) {
	case types.CREATE_APPROVAL_CONFIG:
		return true;
	default:
		return state;
	}
};

export default combineReducers({
	approvals,
	showConfig
});
