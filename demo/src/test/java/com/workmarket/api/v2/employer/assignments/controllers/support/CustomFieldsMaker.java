package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;

public class CustomFieldsMaker {
	public static Property<CustomFieldGroupDTO, Long> customFieldGroupId = newProperty();
	public static Property<CustomFieldGroupDTO, String> customFieldGroupName = newProperty();
	public static Property<CustomFieldGroupDTO, Integer> position = newProperty();
	public static Property<CustomFieldGroupDTO, CustomFieldDTO.Builder> field = newProperty();
	public static Property<CustomFieldDTO, Long> customFieldId = newProperty();
	public static Property<CustomFieldDTO, String> value = newProperty();
	public static Property<CustomFieldDTO, String> defaultValue = newProperty();
	public static Property<CustomFieldDTO, String> customFieldName = newProperty();
	public static Property<CustomFieldDTO, String> customFieldType = newProperty();

	public static final Instantiator<CustomFieldGroupDTO> CustomFieldGroupDTO = new Instantiator<CustomFieldGroupDTO>() {
		@Override
		public CustomFieldGroupDTO instantiate(PropertyLookup<CustomFieldGroupDTO> lookup) {
			return new CustomFieldGroupDTO.Builder()
				.setId(lookup.valueOf(customFieldGroupId, 99999L))
				.setName(lookup.valueOf(customFieldGroupName, "Awesome Custom Fields"))
				.setPosition(lookup.valueOf(position, 0))
				.setRequired(false)
				.addField(lookup.valueOf(field, new CustomFieldDTO.Builder(make(a(CustomFieldDTO)))))
				.build();
		}
	};

	public static final Instantiator<CustomFieldDTO> CustomFieldDTO = new Instantiator<CustomFieldDTO>() {
		@Override
		public CustomFieldDTO instantiate(PropertyLookup<CustomFieldDTO> lookup) {
			return new CustomFieldDTO.Builder()
				.setId(lookup.valueOf(customFieldId, 99999L))
				.setValue(lookup.valueOf(value, "blarg"))
				.setDefaultValue(lookup.valueOf(defaultValue, ""))
				.setName(lookup.valueOf(customFieldName, "a custom field name"))
				.build();
		}
	};
}
