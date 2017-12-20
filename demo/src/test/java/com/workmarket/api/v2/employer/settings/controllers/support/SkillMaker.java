package com.workmarket.api.v2.employer.settings.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.settings.models.SkillDTO;

import static com.natpryce.makeiteasy.Property.newProperty;

public class SkillMaker {

	public static final Property<SkillDTO, String> name = newProperty();

	public static final Instantiator<SkillDTO> SkillDTO = new Instantiator<SkillDTO>() {
		@Override
		public SkillDTO instantiate(PropertyLookup<SkillDTO> lookup) {
			return new SkillDTO.Builder()
				.setName(lookup.valueOf(name, "welding"))
				.build();
		}
	};
}
