import { connect } from 'react-redux';
import * as LocationActions from '../actions/shipments';
import ShipmentComponent from '../components/add_shipments';

const mapStateToProps = ({ shipmentGroup }) => {
	return { shipmentGroup };
};

const mapDispatchToProps = (dispatch) => {
	return {
		updateShippingReturned:
			value => dispatch(LocationActions.updateShippingReturned(value)),
		updateReturnAddressName:
			value => dispatch(LocationActions.updateReturnAddressName(value)),
		updateReturnAddressLine1:
			value => dispatch(LocationActions.updateReturnAddressLine1(value)),
		updateReturnAddressLine2:
			value => dispatch(LocationActions.updateReturnAddressLine2(value)),
		updateReturnAddressCity:
			value => dispatch(LocationActions.updateReturnAddressCity(value)),
		updateReturnAddressState:
			value => dispatch(LocationActions.updateReturnAddressState(value)),
		updateReturnAddressZip:
			value => dispatch(LocationActions.updateReturnAddressZip(value)),
		updateReturnAddressCountry:
			value => dispatch(LocationActions.updateReturnAddressCountry(value)),
		updateShipToAddressName:
			value => dispatch(LocationActions.updateShipToAddressName(value)),
		updateShipToAddressLine1:
			value => dispatch(LocationActions.updateShipToAddressLine1(value)),
		updateShipToAddressLine2:
			value => dispatch(LocationActions.updateShipToAddressLine2(value)),
		updateShipToAddressCity:
			value => dispatch(LocationActions.updateShipToAddressCity(value)),
		updateShipToAddressState:
			value => dispatch(LocationActions.updateShipToAddressState(value)),
		updateShipToAddressZip:
			value => dispatch(LocationActions.updateShipToAddressZip(value)),
		updateShipToAddressCountry:
			value => dispatch(LocationActions.updateShipToAddressCountry(value)),
		updateShippingDestinationType:
			value => dispatch(LocationActions.updateShippingDestinationType(value)),
		updateReturnShipment: value => dispatch(LocationActions.updateReturnShipment(value)),
		updateShipmentDestination: value => dispatch(LocationActions.updateShipmentDestination(value)),
		updateReturnDestination: value => dispatch(LocationActions.updateReturnDestination(value)),
		updateSuppliedByWorker: value => dispatch(LocationActions.updateSuppliedByWorker(value)),
		updateShippingProvider: value => dispatch(LocationActions.updateShippingProvider(value)),
		addShipment: value => dispatch(LocationActions.addShipment(value)),
		addReturnShipment: value => dispatch(LocationActions.addReturnShipment(value)),
		removeShipment: id => dispatch(LocationActions.removeShipment(id)),
		addShipToAddress: value => dispatch(LocationActions.addShipToAddress(value)),
		addReturnAddress: value => dispatch(LocationActions.addShipToAddress(value)),
		removeShipToAddress: value => dispatch(LocationActions.removeShipToAddress(value)),
		removeReturnAddress: value => dispatch(LocationActions.removeReturnAddress(value)),
		clearShipments: value => dispatch(LocationActions.clearShipments(value)),
		clearReturnShipment: value => dispatch(LocationActions.clearReturnShipment(value))
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(ShipmentComponent);
