import { combineReducers } from 'redux';
import * as types from '../constants/actionTypes';
import ShipmentReducer from './shipment';
import ShipToAddressReducer from './shipToAddress';
import AddressReducer from './returnAddress';

const uuid = (state = null) => {
	return state;
};

const returnShipment = (state = false, action) => {
	switch (action.type) {
	case types.UPDATE_RETURN_SHIPMENT:
		return action.value;
	default:
		return state;
	}
};

const suppliedByWorker = (state = false, action) => {
	switch (action.type) {
	case types.UPDATE_SUPPLIED_BY_WORKER:
		return action.value;
	default:
		return state;
	}
};

const shippingDestinationType = (state = 'WORKER', action) => {
	switch (action.type) {
	case types.UPDATE_SHIPPING_DESTINATION_TYPE:
		return action.value;
	default:
		return state;
	}
};

export default combineReducers({
	uuid,
	returnShipment,
	suppliedByWorker,
	shippingDestinationType,
	returnAddress: AddressReducer,
	shipToAddress: ShipToAddressReducer,
	shipments: ShipmentReducer
});
