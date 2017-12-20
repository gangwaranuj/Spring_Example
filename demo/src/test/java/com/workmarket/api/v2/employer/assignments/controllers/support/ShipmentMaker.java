package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.assignments.models.ShipmentDTO;
import com.workmarket.domains.work.model.part.ShippingProvider;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ShipmentMaker {
	public static final Property<ShipmentDTO, String> name = newProperty();
	public static final Property<ShipmentDTO, String> trackingNumber = newProperty();
	public static final Property<ShipmentDTO, ShippingProvider> shippingProvider = newProperty();

	private static final String TRACKING_NUMBER = "1234567890QWERTYUIOP";

	public static final Instantiator<ShipmentDTO> ShipmentDTO = new Instantiator<ShipmentDTO>() {
		@Override
		public ShipmentDTO instantiate(PropertyLookup<ShipmentDTO> lookup) {
			return new ShipmentDTO.Builder()
				.setName(lookup.valueOf(name, "Awesome Shipment"))
				.setTrackingNumber(lookup.valueOf(trackingNumber, TRACKING_NUMBER))
				.setShippingProvider(lookup.valueOf(shippingProvider, ShippingProvider.UPS))
				.build();
		}
	};
}

