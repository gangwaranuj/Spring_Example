import { combineReducers } from 'redux';
import * as types from '../constants/actionTypes';

const range = (state = false, { type, value }) => {
	switch (type) {
	case types.TOGGLE_SCHEDULE_RANGE:
		return value;
	default:
		return state;
	}
};

const from = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_SCHEDULE_FROM:
		return value;
	default:
		return state;
	}
};

const through = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_SCHEDULE_THROUGH:
		return value;
	default:
		return state;
	}
};

const confirmationRequired = (state = false, { type }) => {
	switch (type) {
	case types.TOGGLE_CONFIRMATION_REQUIRED:
		return !state;
	default:
		return state;
	}
};

const confirmationLeadTime = (state = 0, { type, value }) => {
	switch (type) {
	case types.UPDATE_CONFIRMATION_LEAD_TIME:
		return ~~value;
	default:
		return state;
	}
};

const checkinRequired = (state = false, { type }) => {
	switch (type) {
	case types.TOGGLE_CHECKIN_REQUIRED:
		return !state;
	default:
		return state;
	}
};

const checkinCallRequired = (state = false, { type }) => {
	switch (type) {
	case types.TOGGLE_CHECKIN_CALL_REQUIRED:
		return !state;
	default:
		return state;
	}
};

const checkinContactName = (state = '', { type, value }) => {
	switch (type) {
	case types.UPDATE_CHECKIN_CONTACT_NAME:
		return value;
	default:
		return state;
	}
};

const checkinContactPhone = (state = '', { type, value }) => {
	switch (type) {
	case types.UPDATE_CHECKIN_CONTACT_PHONE:
		return value;
	default:
		return state;
	}
};

const checkoutNoteDisplayed = (state = false, { type }) => {
	switch (type) {
	case types.TOGGLE_CHECKOUT_NOTE_DISPLAYED:
		return !state;
	default:
		return state;
	}
};

const checkoutNote = (state = '', { type, value }) => {
	switch (type) {
	case types.UPDATE_CHECKOUT_NOTE:
		return value;
	default:
		return state;
	}
};

export default combineReducers({
	range,
	from,
	through,
	confirmationRequired,
	confirmationLeadTime,
	checkinRequired,
	checkinCallRequired,
	checkinContactName,
	checkinContactPhone,
	checkoutNoteDisplayed,
	checkoutNote
});
