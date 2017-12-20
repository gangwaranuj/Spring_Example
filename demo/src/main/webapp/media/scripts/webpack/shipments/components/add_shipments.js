import { WMRadioButtonGroup, WMRadioButton, WMToggle, WMRaisedButton } from '@workmarket/front-end-components';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import wmSelect from '../../funcs/wmSelect';
import AssignmentListItem from '../../assignments/components/AssignmentListItem';

class ShipmentComponent extends Component {
	constructor (props) {
		super(props);
		this.state = {
			suppliedByWorker: 'shipped', // toggles worker will supply parts
			returnShipment: false, // default toggle needs to start of unchecked
			shippingDestinationType: 'WORKER'
		};
	}

	componentDidMount () {
		this.initialize();
	}

	componentWillReceiveProps (nextProps) {
		const { shipmentGroup:
			{ shippingDestinationType, returnShipment, suppliedByWorker } }
		= nextProps;

		if (shippingDestinationType && this.shipmentDestinationTypeSelector
		&& (this.shipmentDestinationTypeSelector.items[0] !== shippingDestinationType)) {
			this.shipmentDestinationTypeSelector.setValue(shippingDestinationType, true);
		}
		if (returnShipment !== this.props.shipmentGroup.returnShipment) {
			this.setState({ returnShipment });
		}
		this.setState({ suppliedByWorker });
	}

	componentDidUpdate () {
		if (!this.props.shipmentGroup.suppliedByWorker) {
			this.initialize();
		}
	}

	setValue = ({ target }) => {
		let { name, value } = target;
		name = name.replace(/^shipments-/, '');
		value = Number.isNaN(+value) ? value : +value;
		this.setState({ [name]: value });
	}

	setReturnValue = ({ target }) => {
		let { name, value } = target;
		name = name.replace(/^returnShipments-/, '');
		value = Number.isNaN(+value) ? value : +value;
		this.setState({ [name]: value });
	}

	initialize () {
		const root = this.node;

		this.shipmentDestinationTypeSelector = wmSelect({ selector: '[name="shipments-shippingDestinationType"]', root }, {
			onLoad: () => {
				if (!this.shipmentDestinationTypeSelector.items[0]) {
					const destinationType = this.props.shipmentGroup.shippingDestinationType;
					this.shipmentDestinationTypeSelector.setValue(destinationType);
				}
			},
			onChange: value => this.props.updateShippingDestinationType(value)
		})[0].selectize;
	}

	addShipment = () => {
		const {
			uuid, name, trackingNumber, trackingStatus, shippingProvider, value
		} = this.state;
		this.props.addShipment({
			uuid, name, trackingNumber, shippingProvider, trackingStatus, value
		});
	}

	addReturnShipment = () => {
		const {
			uuid, returnName, returnTrackingNumber, trackingStatus, shippingProvider, returnValue
		} = this.state;
		this.props.addReturnShipment({
			uuid,
			name: returnName,
			trackingNumber: returnTrackingNumber,
			shippingProvider,
			trackingStatus,
			value: returnValue
		});
	}

	render () {
		return (
			<div
				ref={ node => (this.node = node) }
			>
				<div>
					<div className="assignment-creation--container">
						<label className="assignment-creation--label -required" htmlFor="shipments-mode">Delivery</label>
						<div className="assignment-creation--button">
							<WMRadioButtonGroup
								name="shipments-mode"
								onChange={ (event, value) => {
									const isMode = value === 'supplied';
									this.props.updateSuppliedByWorker(isMode);
									this.props.clearShipments();
									this.props.removeShipToAddress();
								} }
								valueSelected={ this.props.shipmentGroup.suppliedByWorker ? 'supplied' : 'shipped' }
								defaultSelected={ 'shipped' }
							>
								<WMRadioButton
									label="Parts are being shipped"
									value={ 'shipped' }
								/>
								<WMRadioButton
									label="Worker will supply parts"
									value={ 'supplied' }
								/>
							</WMRadioButtonGroup>
						</div>
					</div>

					{ !this.props.shipmentGroup.suppliedByWorker &&		
						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="shipments-shippingDestinationType">Shipping Destination</label>
							<div className="assignment-creation--field">
								<select
									className="wm-select"
									id="shipments-shippingDestinationType"
									name="shipments-shippingDestinationType"
									defaultValue={ this.props.shipmentGroup.shippingDestinationType }
								>
									<option value="WORKER">Shipped To Worker</option>
									<option value="ONSITE">Onsite</option>
									<option value="PICKUP">Specify Other Location</option>
								</select>
							</div>
						</div>
					}

					{ (this.props.shipmentGroup.shippingDestinationType === 'PICKUP' && !this.props.shipmentGroup.suppliedByWorker) ?
						(<div>
							<div className="assignment-creation--container">
								<label className="assignment-creation--label -required" htmlFor="location-addressLine1">Location Name</label>
								<div className="assignment-creation--field">
									<input
										type="text"
										name="location-name"
										placeholder="Name"
										onChange={ ({ target: { value } }) => this.props.updateShipToAddressName(value) }
										value={ this.props.shipmentGroup.shipToAddress.name }
									/>
								</div>
							</div>
							<div className="assignment-creation--container">
								<label className="assignment-creation--label -required" htmlFor="location-addressLine1">Location Address</label>
								<div id="location-addressLine1" className="assignment-creation--field">
									<input
										type="text"
										name="location-addressLine1"
										placeholder="Address"
										onChange={ ({ target: { value } }) => this.props.updateShipToAddressLine1(value) }
										value={ this.props.shipmentGroup.shipToAddress.addressLine1 }
									/>
									<input
										type="text"
										name="location-addressLine2"
										placeholder="Suite / Floor"
										onChange={ ({ target: { value } }) => this.props.updateShipToAddressLine2(value) }
										value={ this.props.shipmentGroup.shipToAddress.addressLine2 }
									/>
									<input
										type="text"
										name="location-city"
										placeholder="City"
										onChange={ ({ target: { value } }) => this.props.updateShipToAddressCity(value) }
										value={ this.props.shipmentGroup.shipToAddress.city }
									/>
									<input
										type="text"
										name="location-state"
										placeholder="State"
										onChange={ ({ target: { value } }) => this.props.updateShipToAddressState(value) }
										value={ this.props.shipmentGroup.shipToAddress.state }
									/>
									<input
										type="text"
										name="location-zip"
										placeholder="Zip"
										onChange={ ({ target: { value } }) => this.props.updateShipToAddressZip(value) }
										value={ this.props.shipmentGroup.shipToAddress.zip }
									/>
									<input
										type="text"
										name="location-country"
										placeholder="Country"
										onChange={ ({ target: { value } }) => this.props.updateShipToAddressCountry(value) }
										value={ this.props.shipmentGroup.shipToAddress.country }
									/>
								</div>
							</div>
						</div>
					) : ''}

					{ (this.state.shippingDestinationType === 'WORKER' || this.state.shippingDestinationType === 'ONSITE')
					&&
						<div className="assignment-creation--container">
							<label className="assignment-creation--label" htmlFor="shipments-returnShipment">
								Worker must return original parts
							</label>
							<div className="assignment-creation--button">
								<WMToggle
									label="Worker must return original parts."
									toggled={ this.props.shipmentGroup.returnShipment }
									onToggle={ () => {
										if (this.props.shipmentGroup.returnShipment === true) {
											this.props.updateReturnShipment(false);
											this.props.clearReturnShipment();
											this.props.removeReturnAddress();
										} else {
											this.props.updateReturnShipment(true);
										}
									} }
								/>
							</div>
						</div>
					}

					{ !this.props.shipmentGroup.suppliedByWorker &&
						<div>
							<div className="assignment-creation--container">
								<label className="assignment-creation--label" htmlFor="shipments-name">Tracking Information</label>
							</div>
							<div className="assignment-creation--container">
								<div className="assignment-creation--list">
									<div className="deliverables--type">
										<input type="text" name="shipments-name" placeholder="Part Name" onChange={ this.setValue } />
									</div>
									<div className="deliverables--description">
										<input type="text" name="shipments-trackingNumber" placeholder="Tracking Number" onChange={ this.setValue } />
									</div>
									<div className="deliverables--number">
										<input type="number" name="shipments-value" placeholder="Price" step=".01" min="0" onChange={ this.setValue } />
									</div>
									<WMRaisedButton
										primary
										label={ 'Add' }
										disabled={ !this.state.name || !this.state.trackingNumber }
										onClick={ this.addShipment }
										style={ { marginTop: '-5px' } }
									/>
								</div>
							</div>

							<div className="assignment-creation--container">
								<div className="assignment-creation--list">
									{ this.props.shipmentGroup.shipments.map((shipment, uuid) => {
										if (!shipment.isReturn) {
											return (
												<AssignmentListItem
													title={ `${shipment.name}  |  ${shipment.trackingNumber}  |  $${shipment.value}` }
													key={ uuid }
													item={ shipment }
													onRemoveToClick={ () => this.props.removeShipment(uuid) }
												/>
											);
										}
										return '';
									})}
								</div>
							</div>
						</div>
					}

					{ this.props.shipmentGroup.returnShipment ? (
						<div>
							<div className="assignment-creation--container">
								<label className="assignment-creation--label -required" htmlFor="returnLocation-name">Location Name</label>
								<div className="assignment-creation--field">
									<input
										type="text"
										name="returnLocation-name"
										placeholder="Name"
										onChange={ ({ target: { value } }) => this.props.updateReturnAddressName(value) }
										value={ this.props.shipmentGroup.returnAddress.name }
									/>
								</div>
							</div>

							<div className="assignment-creation--container">
								<label className="assignment-creation--label -required" htmlFor="returnLocation-addressLine1">Return Location Address</label>
								<div id="returnLocation-addressLine1" className="assignment-creation--field">
									<input
										type="text"
										name="returnLocation-addressLine1"
										placeholder="Address"
										onChange={ ({ target: { value } }) => this.props.updateReturnAddressLine1(value) }
										value={ this.props.shipmentGroup.returnAddress.addressLine1 }
									/>
									<input
										type="text"
										name="returnLocation-addressLine2"
										placeholder="Suite / Floor"
										onChange={ ({ target: { value } }) => this.props.updateReturnAddressLine2(value) }
										value={ this.props.shipmentGroup.returnAddress.addressLine2 }

									/>
									<input
										type="text"
										name="returnLocation-city"
										placeholder="City"
										onChange={ ({ target: { value } }) => this.props.updateReturnAddressCity(value) }
										value={ this.props.shipmentGroup.returnAddress.city }
									/>
									<input
										type="text"
										name="returnLocation-state"
										placeholder="State"
										onChange={ ({ target: { value } }) => this.props.updateReturnAddressState(value) }
										value={ this.props.shipmentGroup.returnAddress.state }
									/>
									<input
										type="text"
										name="returnLocation-zip"
										placeholder="Zip"
										onChange={ ({ target: { value } }) => this.props.updateReturnAddressZip(value) }
										value={ this.props.shipmentGroup.returnAddress.zip }
									/>
									<input
										type="text"
										name="returnLocation-country"
										placeholder="Country"
										onChange={ ({ target: { value } }) => this.props.updateReturnAddressCountry(value) }
										value={ this.props.shipmentGroup.returnAddress.country }
									/>
								</div>
							</div>

							<div className="assignment-creation--container">
								<label className="assignment-creation--label" htmlFor="deliverables-deliverable">Return Tracking Information</label>
							</div>
							<div className="assignment-creation--container">
								<div className="assignment-creation--list">
									<div className="deliverables--type">
										<input type="text" name="returnShipments-returnName" placeholder="Part Name" onChange={ this.setReturnValue } />
									</div>
									<div className="deliverables--description">
										<input type="text" name="returnShipments-returnTrackingNumber" placeholder="Tracking Number" onChange={ this.setReturnValue } />
									</div>
									<div className="deliverables--number">
										<input type="number" name="returnShipments-returnValue" placeholder="Price" step=".01" min="0" onChange={ this.setReturnValue } />
									</div>
									<WMRaisedButton
										primary
										label={ 'Add' }
										disabled={ !this.state.returnName || !this.state.returnTrackingNumber }
										onClick={ this.addReturnShipment }
										style={ { marginTop: '-5px' } }
									/>
								</div>
							</div>

							<div className="assignment-creation--container">
								<div className="assignment-creation--list">
									{ this.props.shipmentGroup.shipments.map((shipment, uuid) => {
										if (shipment.isReturn) {
											return (
												<AssignmentListItem
													title={ `${shipment.name}  |  ${shipment.trackingNumber}  |  $${shipment.value}` }
													key={ uuid }
													item={ shipment }
													onRemoveToClick={ () => this.props.removeShipment(uuid) }
												/>
											);
										}
										return '';
									})}
								</div>
							</div>
						</div>
					) : ''}
				</div>
			</div>

		);
	}
}

ShipmentComponent.propTypes = {
	updateShipToAddressName: PropTypes.func,
	updateShipToAddressLine1: PropTypes.func,
	updateShipToAddressLine2: PropTypes.func,
	updateShipToAddressCity: PropTypes.func,
	updateShipToAddressState: PropTypes.func,
	updateShipToAddressCountry: PropTypes.func,
	updateShipToAddressZip: PropTypes.func,
	updateReturnAddressName: PropTypes.func,
	updateReturnAddressLine1: PropTypes.func,
	updateReturnAddressLine2: PropTypes.func,
	updateReturnAddressState: PropTypes.func,
	updateReturnAddressCity: PropTypes.func,
	updateReturnAddressCountry: PropTypes.func,
	updateReturnAddressZip: PropTypes.func,
	removeReturnAddress: PropTypes.func,
	removeShipToAddress: PropTypes.func,
	addReturnAddress: PropTypes.func,
	addShipToAddress: PropTypes.func,
	addShipment: PropTypes.func,
	addReturnShipment: PropTypes.func,
	removeShipment: PropTypes.func,
	clearShipments: PropTypes.func,
	clearReturnShipment: PropTypes.func,
	updateReturnShipment: PropTypes.func.isRequired,
	updateSuppliedByWorker: PropTypes.func.isRequired,
	updateShippingDestinationType: PropTypes.func.isRequired,
	shipmentGroup: PropTypes.shape({
		uuid: PropTypes.oneOfType([
			PropTypes.string,
			PropTypes.number
		]),
		returnShipment: PropTypes.bool,
		suppliedByWorker: PropTypes.bool,
		shippingDestinationType: PropTypes.string,
		shipments: PropTypes.arrayOf(PropTypes.shape({
			uuid: PropTypes.oneOfType([
				PropTypes.string,
				PropTypes.number
			]),
			name: PropTypes.string.isRequired,
			trackingNumber: PropTypes.oneOfType([
				PropTypes.string,
				PropTypes.number
			]).isRequired,
			trackingStatus: PropTypes.oneOfType([
				PropTypes.string,
				PropTypes.number
			]),
			shippingProvider: PropTypes.string,
			value: PropTypes.number.isRequired,
			isReturn: PropTypes.bool
		})),
		shipToAddress: PropTypes.shape({
			uuid: PropTypes.oneOfType([
				PropTypes.string,
				PropTypes.number
			]),
			name: PropTypes.string,
			addressLine1: PropTypes.string,
			addressLine2: PropTypes.string,
			city: PropTypes.string,
			state: PropTypes.string,
			zip: PropTypes.string,
			country: PropTypes.string
		}),
		returnAddress: PropTypes.shape({
			uuid: PropTypes.oneOfType([
				PropTypes.string,
				PropTypes.number
			]),
			name: PropTypes.string,
			addressLine1: PropTypes.string,
			addressLine2: PropTypes.string,
			city: PropTypes.string,
			state: PropTypes.string,
			zip: PropTypes.string,
			country: PropTypes.string
		})
	})
};

export default ShipmentComponent;
