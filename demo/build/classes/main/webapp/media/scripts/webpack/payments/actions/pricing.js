import * as types from '../constants/actionTypes';

const updatePricingMode = (value) => {
	return {
		type: types.UPDATE_PRICING_MODE,
		value
	};
};

const updatePricingType = (value) => {
	return {
		type: types.UPDATE_PRICING_TYPE,
		value
	};
};

const updatePricingFlatPrice = (value) => {
	return {
		type: types.UPDATE_PRICING_FLAT_PRICE,
		value
	};
};

const updatePricingPerHourPrice = (value) => {
	return {
		type: types.UPDATE_PRICING_PER_HOUR_PRICE,
		value
	};
};

const updatePricingMaxNumberOfHours = (value) => {
	return {
		type: types.UPDATE_PRICING_MAX_NUMBER_OF_HOURS,
		value
	};
};

const updatePricingPerUnitPrice = (value) => {
	return {
		type: types.UPDATE_PRICING_PER_UNIT_PRICE,
		value
	};
};

const updatePricingMaxNumberOfUnits = (value) => {
	return {
		type: types.UPDATE_PRICING_MAX_NUMBER_OF_UNITS,
		value
	};
};

const updatePricingInitialPerHourPrice = (value) => {
	return {
		type: types.UPDATE_PRICING_INITIAL_PER_HOUR_PRICE,
		value
	};
};

const updatePricingInitialNumberOfHours = (value) => {
	return {
		type: types.UPDATE_PRICING_INITIAL_NUMBER_OF_HOURS,
		value
	};
};

const updatePricingAdditionalPerHourPrice = (value) => {
	return {
		type: types.UPDATE_PRICING_ADDITIONAL_PER_HOUR_PRICE,
		value
	};
};

const updatePricingMaxBlendedNumberOfHours = (value) => {
	return {
		type: types.UPDATE_PRICING_MAX_BLENDED_NUMBER_OF_HOURS,
		value
	};
};

const updatePaymentTermsDays = (value) => {
	return {
		type: types.UPDATE_PAYMENT_TERMS_DAYS,
		value
	};
};

const updatePricingOfflinePayment = (value) => {
	return {
		type: types.UPDATE_PRICING_OFFLINE_PAYMENT,
		value
	};
};

const updatePricingDisablePriceNegotiation = (value) => {
	return {
		type: types.UPDATE_PRICING_DISABLE_PRICE_NEGOTIATION,
		value
	};
};

export default {
	updatePricingMode,
	updatePricingType,
	updatePricingFlatPrice,
	updatePricingPerHourPrice,
	updatePricingMaxNumberOfHours,
	updatePricingPerUnitPrice,
	updatePricingMaxNumberOfUnits,
	updatePricingInitialPerHourPrice,
	updatePricingInitialNumberOfHours,
	updatePricingAdditionalPerHourPrice,
	updatePricingMaxBlendedNumberOfHours,
	updatePaymentTermsDays,
	updatePricingOfflinePayment,
	updatePricingDisablePriceNegotiation
};
