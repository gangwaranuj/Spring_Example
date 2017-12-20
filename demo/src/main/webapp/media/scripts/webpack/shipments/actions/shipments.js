import $ from 'jquery';
import * as types from '../constants/actionTypes';

export const updateReturnShipment = (value) => {
	return {
		type: types.UPDATE_RETURN_SHIPMENT,
		value
	};
};

export const updateSuppliedByWorker = (value) => {
	return {
		type: types.UPDATE_SUPPLIED_BY_WORKER,
		value
	};
};

export const updateShippingDestinationType = (value) => {
	return {
		type: types.UPDATE_SHIPPING_DESTINATION_TYPE,
		value
	};
};

export const updateShipmentDestination = (value) => {
	return {
		type: types.UPDATE_SHIPMENT_DESTINATION,
		value
	};
};

export const updateShippingProvider = (value) => {
	return {
		type: types.UPDATE_SHIPPING_PROVIDER,
		value
	};
};

export const updateReturnDestination = (value) => {
	return {
		type: types.UPDATE_RETURN_DESTINATION,
		value
	};
};

export const updateShippingReturned = (value) => {
	return {
		type: types.UPDATE_SHIPPING_RETURNED,
		value
	};
};

export const updateShipToAddressName = (value) => {
	return {
		type: types.UPDATE_SHIP_TO_ADDRESS_NAME,
		value
	};
};

export const updateShipToAddressLine1 = (value) => {
	return {
		type: types.UPDATE_SHIP_TO_ADDRESS_LINE_1,
		value
	};
};

export const updateShipToAddressLine2 = (value) => {
	return {
		type: types.UPDATE_SHIP_TO_ADDRESS_LINE_2,
		value
	};
};

export const updateShipToAddressCity = (value) => {
	return {
		type: types.UPDATE_SHIP_TO_ADDRESS_CITY,
		value
	};
};

export const updateShipToAddressState = (value) => {
	return {
		type: types.UPDATE_SHIP_TO_ADDRESS_STATE,
		value
	};
};

export const updateShipToAddressCountry = (value) => {
	return {
		type: types.UPDATE_SHIP_TO_ADDRESS_COUNTRY,
		value
	};
};

export const updateShipToAddressZip = (value) => {
	return {
		type: types.UPDATE_SHIP_TO_ADDRESS_ZIP,
		value
	};
};

export const updateReturnAddressName = (value) => {
	return {
		type: types.UPDATE_RETURN_ADDRESS_NAME,
		value
	};
};

export const updateReturnAddressLine1 = (value) => {
	return {
		type: types.UPDATE_RETURN_ADDRESS_LINE_1,
		value
	};
};

export const updateReturnAddressLine2 = (value) => {
	return {
		type: types.UPDATE_RETURN_ADDRESS_LINE_2,
		value
	};
};

export const updateReturnAddressCity = (value) => {
	return {
		type: types.UPDATE_RETURN_ADDRESS_CITY,
		value
	};
};

export const updateReturnAddressState = (value) => {
	return {
		type: types.UPDATE_RETURN_ADDRESS_STATE,
		value
	};
};

export const updateReturnAddressCountry = (value) => {
	return {
		type: types.UPDATE_RETURN_ADDRESS_COUNTRY,
		value
	};
};

export const updateReturnAddressZip = (value) => {
	return {
		type: types.UPDATE_RETURN_ADDRESS_ZIP,
		value
	};
};

export const updateReturnAddressId = (value) => {
	return {
		type: types.UPDATE_RETURN_ADDRESS_ID,
		value
	};
};

export const removeReturnAddress = (value) => {
	return {
		type: types.REMOVE_RETURN_ADDRESS,
		value
	};
};

export const removeShipToAddress = (value) => {
	return {
		type: types.REMOVE_SHIP_TO_ADDRESS,
		value
	};
};

export const clearShipments = (value) => {
	return {
		type: types.CLEAR_SHIPMENTS,
		value
	};
};

export const clearReturnShipment = (value) => {
	return {
		type: types.CLEAR_RETURN_SHIPMENT,
		value
	};
};

const receiveShipmentTracking = (trackingNumber, data) => {
	const shippingProviders = data.shippingProviders[0] || 'OTHER';
	const { responseCode } = data;
	if (shippingProviders && responseCode === 200) {
		return {
			type: types.UPDATE_SHIPPING_PROVIDER,
			trackingNumber,
			shippingProvider: shippingProviders
		};
	}
	return null;
};

const fetchShipmentTracking = ({ trackingNumber }) => {
	return (dispatch) => {
		$.getJSON(`/assignments/detect_shipping_provider/${trackingNumber}`, (json) => {
			dispatch(receiveShipmentTracking(trackingNumber, json.data));
		});
	};
};

export const newShipment = ({
	id,
	name,
	trackingNumber,
	trackingStatus,
	shippingProvider,
	value,
	isReturn }) => {
	return {
		type: types.ADD_SHIPMENT,
		id,
		name,
		trackingNumber,
		trackingStatus,
		shippingProvider,
		value,
		isReturn
	};
};

export const newReturnShipment = ({
	id,
	name,
	trackingNumber,
	trackingStatus,
	shippingProvider,
	value,
	isReturn }) => {
	return {
		type: types.ADD_RETURN_SHIPMENT,
		id,
		name,
		trackingNumber,
		trackingStatus,
		shippingProvider,
		value,
		isReturn
	};
};

export const addReturnAddress = ({
	name,
	addressLine1,
	addressLine2,
	city,
	state,
	country,
	zip
}) => {
	return {
		type: types.ADD_RETURN_ADDRESS,
		name,
		addressLine1,
		addressLine2,
		city,
		state,
		country,
		zip
	};
};

export const addShipToAddress = ({
	name,
	addressLine1,
	addressLine2,
	city,
	state,
	country,
	zip
}) => {
	return {
		type: types.ADD_SHIP_TO_ADDRESS,
		name,
		addressLine1,
		addressLine2,
		city,
		state,
		country,
		zip
	};
};

export const addShipment = (shipment) => {
	return (dispatch) => {
		dispatch(newShipment(shipment));
		dispatch(fetchShipmentTracking(shipment));
	};
};

export const addReturnShipment = (shipment) => {
	return (dispatch) => {
		dispatch(newReturnShipment(shipment));
		dispatch(fetchShipmentTracking(shipment));
	};
};

export const removeShipment = (id) => {
	return { type: types.REMOVE_SHIPMENT, id };
};
