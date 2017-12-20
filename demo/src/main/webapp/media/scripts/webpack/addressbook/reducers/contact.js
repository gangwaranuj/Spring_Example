import { combineReducers } from 'redux';
import * as types from '../constants/contactActionTypes';

const id = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_CONTACT_ID:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
	case types.UPDATE_CONTACT_FIRST_NAME:
	case types.UPDATE_CONTACT_LAST_NAME:
	case types.UPDATE_CONTACT_EMAIL:
	case types.UPDATE_CONTACT_WORK_PHONE:
	case types.UPDATE_CONTACT_WORK_PHONE_EXTENSION:
		return null;
	default:
		return state;
	}
};

const firstName = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_CONTACT_FIRST_NAME:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return null;
	default:
		return state;
	}
};

const lastName = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_CONTACT_LAST_NAME:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return null;
	default:
		return state;
	}
};

const email = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_CONTACT_EMAIL:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return null;
	default:
		return state;
	}
};

const workPhone = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_CONTACT_WORK_PHONE:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return null;
	default:
		return state;
	}
};

const workPhoneExtension = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_CONTACT_WORK_PHONE_EXTENSION:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return null;
	default:
		return state;
	}
};

const mobilePhone = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_CONTACT_MOBILE_PHONE:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return null;
	default:
		return state;
	}
};

export default combineReducers({
	id,
	firstName,
	lastName,
	email,
	workPhone,
	workPhoneExtension,
	mobilePhone
});
