package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.model.SurveyDTO;

import static com.natpryce.makeiteasy.Property.newProperty;

public class SurveyMaker {
	public static final Property<SurveyDTO, Long> surveyId = newProperty();

	public static Instantiator<SurveyDTO> SurveyDTO = new Instantiator<SurveyDTO>() {
		@Override
		public SurveyDTO instantiate(PropertyLookup<SurveyDTO> lookup) {
			return new SurveyDTO.Builder()
				.setId(lookup.valueOf(surveyId, 99999L))
				.setRequired(false)
				.build();
		}
	};
}
