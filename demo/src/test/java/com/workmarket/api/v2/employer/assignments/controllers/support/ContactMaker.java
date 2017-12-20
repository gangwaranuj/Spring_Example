package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.model.ContactDTO;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ContactMaker {
	public static final Property<ContactDTO, String> firstName = newProperty();
	public static final Property<ContactDTO, String> lastName = newProperty();
	public static final Property<ContactDTO, String> workPhone = newProperty();

	public static final Instantiator<ContactDTO> ContactDTO = new Instantiator<ContactDTO>() {
		@Override
		public ContactDTO instantiate(PropertyLookup<ContactDTO> lookup) {
			return new ContactDTO.Builder()
				.setFirstName(lookup.valueOf(firstName, "Buddy"))
				.setLastName(lookup.valueOf(lastName, "Rich"))
				.setWorkPhone(lookup.valueOf(workPhone, "516-555-1212"))
				.build();
		}
	};
}
