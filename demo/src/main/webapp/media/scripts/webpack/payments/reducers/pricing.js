import { combineReducers } from 'redux';
import * as types from '../constants/actionTypes';

const mode = (state = '', action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_MODE:
		return action.value;
	default:
		return state;
	}
};

const type = (state = 'FLAT', action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_TYPE:
		return action.value;
	default:
		return state;
	}
};

const flatPrice = (state = '', action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_FLAT_PRICE:
		return action.value;
	default:
		return state;
	}
};

const perHourPrice = (state = '', action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_PER_HOUR_PRICE:
		return action.value;
	default:
		return state;
	}
};

const maxNumberOfHours = (state = '', action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_MAX_NUMBER_OF_HOURS:
		return action.value;
	default:
		return state;
	}
};

const perUnitPrice = (state = '', action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_PER_UNIT_PRICE:
		return action.value;
	default:
		return state;
	}
};

const maxNumberOfUnits = (state = '', action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_MAX_NUMBER_OF_UNITS:
		return action.value;
	default:
		return state;
	}
};

const initialPerHourPrice = (state = '', action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_INITIAL_PER_HOUR_PRICE:
		return action.value;
	default:
		return state;
	}
};

const initialNumberOfHours = (state = '', action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_INITIAL_NUMBER_OF_HOURS:
		return action.value;
	default:
		return state;
	}
};

const additionalPerHourPrice = (state = '', action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_ADDITIONAL_PER_HOUR_PRICE:
		return action.value;
	default:
		return state;
	}
};

const maxBlendedNumberOfHours = (state = '', action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_MAX_BLENDED_NUMBER_OF_HOURS:
		return action.value;
	default:
		return state;
	}
};

const paymentTermsDays = (state = '', action) => {
	switch (action.type) {
	case types.UPDATE_PAYMENT_TERMS_DAYS:
		return Math.abs(action.value);
	default:
		return state;
	}
};

const offlinePayment = (state = false, action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_OFFLINE_PAYMENT:
		return action.value;
	default:
		return state;
	}
};

const disablePriceNegotiation = (state = false, action) => {
	switch (action.type) {
	case types.UPDATE_PRICING_DISABLE_PRICE_NEGOTIATION:
		return action.value;
	default:
		return state;
	}
};

export default combineReducers({
	mode,
	type,
	flatPrice,
	perHourPrice,
	maxNumberOfHours,
	perUnitPrice,
	maxNumberOfUnits,
	initialPerHourPrice,
	initialNumberOfHours,
	additionalPerHourPrice,
	maxBlendedNumberOfHours,
	paymentTermsDays,
	offlinePayment,
	disablePriceNegotiation
});
