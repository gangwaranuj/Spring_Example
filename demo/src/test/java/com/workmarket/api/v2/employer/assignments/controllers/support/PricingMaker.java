package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.assignments.models.PricingDTO;
import com.workmarket.domains.model.pricing.PricingStrategyType;

import static com.natpryce.makeiteasy.Property.newProperty;

public class PricingMaker {
	public static final Property<PricingDTO, String> type = newProperty();
	public static final Property<PricingDTO, String> mode = newProperty();
	public static final Property<PricingDTO, Double> flatPrice = newProperty();
	public static final Property<PricingDTO, Integer> paymentTerms = newProperty();

	public static final Instantiator<PricingDTO> PricingDTO = new Instantiator<PricingDTO>() {
		@Override
		public PricingDTO instantiate(PropertyLookup<PricingDTO> lookup) {
			return new PricingDTO.Builder()
				.setType(lookup.valueOf(type, PricingStrategyType.FLAT.name()))
				.setMode(lookup.valueOf(mode, "pay"))
				.setFlatPrice(lookup.valueOf(flatPrice, 123.00))
				.setPaymentTermsDays(lookup.valueOf(paymentTerms, 0))
				.build();
		}
	};
}
