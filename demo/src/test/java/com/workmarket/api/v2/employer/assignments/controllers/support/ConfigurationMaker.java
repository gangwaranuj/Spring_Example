package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;

public class ConfigurationMaker {
	public static final Property<ConfigurationDTO, Boolean> customFieldsEnabled = Property.newProperty();
	public static final Property<ConfigurationDTO, Boolean> shipmentsEnabled = Property.newProperty();
	public static final Property<ConfigurationDTO, Boolean> requirementSetsEnabled = Property.newProperty();
	public static final Property<ConfigurationDTO, Boolean> deliverablesEnabled = Property.newProperty();
	public static final Property<ConfigurationDTO, Boolean> surveysEnabled = Property.newProperty();

	public static final Instantiator<ConfigurationDTO> ConfigurationDTO = new Instantiator<ConfigurationDTO>() {
		@Override
		public ConfigurationDTO instantiate(PropertyLookup<ConfigurationDTO> lookup) {
			return new ConfigurationDTO.Builder()
				.setCustomFieldsEnabled(lookup.valueOf(customFieldsEnabled, true))
				.setShipmentsEnabled(lookup.valueOf(shipmentsEnabled, true))
				.setRequirementSetsEnabled(lookup.valueOf(requirementSetsEnabled, true))
				.setDeliverablesEnabled(lookup.valueOf(deliverablesEnabled, true))
				.setSurveysEnabled(lookup.valueOf(surveysEnabled, true))
				.build();
		}
	};
}
