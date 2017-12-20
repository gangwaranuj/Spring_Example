package com.workmarket.api.v2.employer.assignments.controllers.support;


import com.google.common.collect.Lists;
import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.assignments.models.ShipmentDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import com.workmarket.api.v2.employer.support.NullDonor;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.domains.work.model.part.ShippingDestinationType;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;

public class ShipmentGroupMaker {
	public static final Property<ShipmentGroupDTO, Boolean> suppliedByWorker = newProperty();
	public static final Property<ShipmentGroupDTO, Boolean> returnShipment = newProperty();
	public static final Property<ShipmentGroupDTO, ShippingDestinationType> shippingDestinationType = newProperty();
	public static final Property<ShipmentGroupDTO, LocationDTO.Builder> shippingAddress = newProperty();
	public static final Property<ShipmentGroupDTO, LocationDTO.Builder> returnAddress = newProperty();
	public static final Property<ShipmentGroupDTO, List<ShipmentDTO.Builder>> shipments = newProperty();
	public static Property<ShipmentGroupDTO, ShipmentDTO.Builder> shipment = newProperty();
	public static final Instantiator<ShipmentGroupDTO> ShipmentGroupDTO = new Instantiator<ShipmentGroupDTO>() {
		@Override
		public ShipmentGroupDTO instantiate(PropertyLookup<ShipmentGroupDTO> lookup) {
			return new ShipmentGroupDTO.Builder()
				.setSuppliedByWorker(lookup.valueOf(suppliedByWorker, false))
				.setReturnShipment(lookup.valueOf(returnShipment, false))
				.setShippingDestinationType(lookup.valueOf(shippingDestinationType, ShippingDestinationType.PICKUP))
				.setShipToAddress(lookup.valueOf(shippingAddress, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO)))))
				.setReturnAddress(lookup.valueOf(returnAddress, new NullDonor<LocationDTO.Builder>()))
				.setShipments(lookup.valueOf(shipments, Lists.newArrayList(new ShipmentDTO.Builder(make(a(ShipmentMaker.ShipmentDTO))))))
				.build();
		}
	};
}
