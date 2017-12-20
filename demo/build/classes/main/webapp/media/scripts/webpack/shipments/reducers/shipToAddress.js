import { Map } from 'immutable';
import * as types from '../constants/actionTypes';

const initialState = Map({
	name: null,
	addressLine1: null,
	addressLine2: null,
	city: null,
	state: null,
	country: null,
	zip: null
});

const shipToAddress = (state = initialState, action) => {
	switch (action.type) {
	case types.ADD_SHIP_TO_ADDRESS:
		return {
			name: action.name,
			addressLine1: action.addressLine1,
			addressLine2: action.addressLine2,
			city: action.city,
			state: action.state,
			country: action.country,
			zip: action.zip
		};
	case types.REMOVE_SHIP_TO_ADDRESS:
		return {
			name: null,
			addressLine1: null,
			addressLine2: null,
			city: null,
			state: null,
			country: null,
			zip: null
		};
	case types.UPDATE_SHIP_TO_ADDRESS_NAME:
		return { ...state, name: action.value };
	case types.UPDATE_SHIP_TO_ADDRESS_ID:
		return { ...state, id: action.value };
	case types.UPDATE_SHIP_TO_ADDRESS_LINE_1:
		return { ...state, addressLine1: action.value };
	case types.UPDATE_SHIP_TO_ADDRESS_LINE_2:
		return { ...state, addressLine2: action.value };
	case types.UPDATE_SHIP_TO_ADDRESS_CITY:
		return { ...state, city: action.value };
	case types.UPDATE_SHIP_TO_ADDRESS_STATE:
		return { ...state, state: action.value };
	case types.UPDATE_SHIP_TO_ADDRESS_COUNTRY:
		return { ...state, country: action.value };
	case types.UPDATE_SHIP_TO_ADDRESS_ZIP:
		return { ...state, zip: action.value };
	default:
		return state;
	}
};

export default shipToAddress;
