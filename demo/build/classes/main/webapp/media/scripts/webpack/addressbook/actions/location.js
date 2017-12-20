import * as types from '../constants/locationActionTypes';

const updateLocationMode = (value) => {
	return {
		type: types.UPDATE_LOCATION_MODE,
		value
	};
};

const updateLocationId = (value) => {
	return {
		type: types.UPDATE_LOCATION_ID,
		value
	};
};

const updateLocationName = (value) => {
	return {
		type: types.UPDATE_LOCATION_NAME,
		value
	};
};

const updateLocationNumber = (value) => {
	return {
		type: types.UPDATE_LOCATION_NUMBER,
		value
	};
};

const updateLocationAddressLine1 = (value) => {
	return {
		type: types.UPDATE_LOCATION_ADDRESS_LINE_1,
		value
	};
};

const updateLocationAddressLine2 = (value) => {
	return {
		type: types.UPDATE_LOCATION_ADDRESS_LINE_2,
		value
	};
};

const updateLocationCity = (value) => {
	return {
		type: types.UPDATE_LOCATION_CITY,
		value
	};
};

const updateLocationState = (value) => {
	return {
		type: types.UPDATE_LOCATION_STATE,
		value
	};
};

const updateLocationZip = (value) => {
	return {
		type: types.UPDATE_LOCATION_ZIP,
		value
	};
};

const updateLocationCountry = (value) => {
	return {
		type: types.UPDATE_LOCATION_COUNTRY,
		value
	};
};

const updateLocationInstructions = (value) => {
	return {
		type: types.UPDATE_LOCATION_INSTRUCTIONS,
		value
	};
};

const updateLocationType = (value) => {
	return {
		type: types.UPDATE_LOCATION_TYPE,
		value
	};
};

const updateLocationLongitude = (value) => {
	return {
		type: types.UPDATE_LOCATION_LONGITUDE,
		value
	};
};

const updateLocationLatitude = (value) => {
	return {
		type: types.UPDATE_LOCATION_LATITUDE,
		value
	};
};

const updateClientCompanyId = (value) => {
	return {
		type: types.UPDATE_CLIENT_COMPANY_ID,
		value
	};
};

const updateProjectId = (value) => {
	return {
		type: types.UPDATE_PROJECT_ID,
		value
	};
};

const clearLocationFields = () => {
	return {
		type: types.CLEAR_LOCATION_FIELDS
	};
};

export default {
	updateLocationId,
	updateLocationName,
	updateLocationNumber,
	updateLocationMode,
	updateLocationAddressLine1,
	updateLocationAddressLine2,
	updateLocationCity,
	updateLocationState,
	updateLocationZip,
	updateLocationCountry,
	updateLocationInstructions,
	updateLocationType,
	updateLocationLongitude,
	updateLocationLatitude,
	updateClientCompanyId,
	updateProjectId,
	clearLocationFields
};
