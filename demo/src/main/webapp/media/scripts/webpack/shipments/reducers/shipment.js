import {
	ADD_SHIPMENT,
	ADD_RETURN_SHIPMENT,
	REMOVE_SHIPMENT,
	UPDATE_SHIPPING_PROVIDER,
	CLEAR_SHIPMENTS,
	CLEAR_RETURN_SHIPMENT
} from '../constants/actionTypes';

const shipments = (state = [], action) => {
	switch (action.type) {
	case ADD_SHIPMENT:
		return [
			...state,
			{
				uuid: null,
				name: action.name,
				trackingNumber: action.trackingNumber,
				trackingStatus: action.trackingStatus,
				shippingProvider: action.shippingProvider,
				value: action.value,
				isReturn: false
			}
		];
	case ADD_RETURN_SHIPMENT:
		return [
			...state,
			{
				uuid: null,
				name: action.name,
				trackingNumber: action.trackingNumber,
				trackingStatus: action.trackingStatus,
				shippingProvider: action.shippingProvider,
				value: action.value,
				isReturn: true
			}
		];
	case CLEAR_SHIPMENTS:
		return [];
	case CLEAR_RETURN_SHIPMENT:
		return state.filter((shipment) => shipment.isReturn === false);
	case REMOVE_SHIPMENT:
		return state.filter((shipment, id) => id !== action.id);
	case UPDATE_SHIPPING_PROVIDER:
		return state.map((shipment) =>
			shipment.trackingNumber === action.trackingNumber ?
				Object.assign({}, shipment, {
					shippingProvider: action.shippingProvider
					? action.shippingProvider : shipment.shippingProvider
				}) :
				shipment
			);
	default:
		return state;
	}
};

export default shipments;
