import { combineReducers } from 'redux';
import * as types from '../constants/locationActionTypes';
import ContactReducer from './contact';
import SecondaryContactReducer from './secondary_contact';

const locationMode = (state = 0, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_MODE:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return 0;
	default:
		return state;
	}
};

const id = (state = 0, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_ID:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return 0;
	default:
		return state;
	}
};

const name = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_NAME:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return '';
	default:
		return state;
	}
};

const number = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_NUMBER:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return '';
	default:
		return state;
	}
};

const addressLine1 = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_ADDRESS_LINE_1:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return '';
	default:
		return state;
	}
};

const addressLine2 = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_ADDRESS_LINE_2:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return '';
	default:
		return state;
	}
};

const city = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_CITY:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return '';
	default:
		return state;
	}
};

const zip = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_ZIP:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return '';
	default:
		return state;
	}
};

const country = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_COUNTRY:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return '';
	default:
		return state;
	}
};

const locationType = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_TYPE:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return 1;
	default:
		return state;
	}
};

const instructions = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_INSTRUCTIONS:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return '';
	default:
		return state;
	}
};

const longitude = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_LONGITUDE:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return 0;
	default:
		return state;
	}
};

const latitude = (state = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_LATITUDE:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return 0;
	default:
		return state;
	}
};

const clientCompanyId = (state = '', { type, value }) => {
	switch (type) {
	case types.UPDATE_CLIENT_COMPANY_ID:
		return value;
	default:
		return state;
	}
};

const state = (dataState = null, { type, value }) => {
	switch (type) {
	case types.UPDATE_LOCATION_STATE:
		return value;
	case types.CLEAR_LOCATION_FIELDS:
		return '';
	default:
		return dataState;
	}
};

export default combineReducers({
	locationMode,
	id,
	name,
	number,
	addressLine1,
	addressLine2,
	city,
	state,
	zip,
	country,
	instructions,
	locationType,
	longitude,
	latitude,
	clientCompanyId,
	contact: ContactReducer,
	secondaryContact: SecondaryContactReducer
});
