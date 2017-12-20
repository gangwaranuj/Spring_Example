package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;

import static com.natpryce.makeiteasy.MakeItEasy.an;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.AssignmentDTO;

public class TemplateMaker {
	public static final Property<TemplateDTO,String> name = newProperty();
	public static final Property<TemplateDTO, String> description = newProperty();
	public static final Property<TemplateDTO, AssignmentDTO.Builder> assignment = newProperty();

	public static final Instantiator<TemplateDTO> TemplateDTO = new Instantiator<TemplateDTO>() {
		@Override
		public TemplateDTO instantiate(PropertyLookup<TemplateDTO> lookup) {
			return new TemplateDTO.Builder()
				.setName(lookup.valueOf(name, "This is my template."))
				.setDescription(lookup.valueOf(description, "There are many like it but this one is mine."))
				.setAssignment(lookup.valueOf(assignment, new AssignmentDTO.Builder(make(an(AssignmentDTO)))))
				.build();
		}
	};
}
