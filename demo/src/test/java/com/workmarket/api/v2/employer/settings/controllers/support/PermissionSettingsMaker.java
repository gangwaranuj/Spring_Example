package com.workmarket.api.v2.employer.settings.controllers.support;


import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.settings.models.PermissionSettingsDTO;

import static com.natpryce.makeiteasy.Property.newProperty;

public class PermissionSettingsMaker {

	public static final Property<PermissionSettingsDTO, Boolean>
		paymentAccessible = newProperty(),
		fundsAccessible = newProperty(),
		counterOfferAccessible = newProperty(),
		pricingEditable = newProperty(),
		workApprovalAllowed = newProperty(),
		projectAccessible = newProperty();

	public static final Instantiator<PermissionSettingsDTO> DefaultPermissionSettings = new Instantiator<PermissionSettingsDTO>() {

		@Override
		public PermissionSettingsDTO instantiate(PropertyLookup<PermissionSettingsDTO> lookup) {
			return new PermissionSettingsDTO.Builder()
				.setPaymentAccessible(lookup.valueOf(paymentAccessible, true))
				.setFundsAccessible(lookup.valueOf(fundsAccessible, true))
				.setCounterOfferAccessible(lookup.valueOf(counterOfferAccessible, true))
				.setPricingEditable(lookup.valueOf(pricingEditable, true))
				.setWorkApprovalAllowed(lookup.valueOf(workApprovalAllowed, true))
				.setProjectAccessible(lookup.valueOf(projectAccessible, true))
				.build();
		}
	};

	public static final Instantiator<PermissionSettingsDTO> NoPermissionSettings = new Instantiator<PermissionSettingsDTO>() {

		@Override
		public PermissionSettingsDTO instantiate(PropertyLookup<PermissionSettingsDTO> lookup) {
			return new PermissionSettingsDTO.Builder()
				.setPaymentAccessible(lookup.valueOf(paymentAccessible, false))
				.setFundsAccessible(lookup.valueOf(fundsAccessible, false))
				.setCounterOfferAccessible(lookup.valueOf(counterOfferAccessible, false))
				.setPricingEditable(lookup.valueOf(pricingEditable, false))
				.setWorkApprovalAllowed(lookup.valueOf(workApprovalAllowed, false))
				.setProjectAccessible(lookup.valueOf(projectAccessible, false))
				.build();
		}
	};
}
