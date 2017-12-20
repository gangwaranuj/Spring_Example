package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.model.ContactDTO;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.domains.model.postalcode.Country;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ContactMaker.ContactDTO;

public class LocationMaker {
	public static final Property<LocationDTO, String> name = newProperty();
	public static final Property<LocationDTO, String> addressLine1 = newProperty();
	public static final Property<LocationDTO, String> addressLine2 = newProperty();
	public static final Property<LocationDTO, String> city = newProperty();
	public static final Property<LocationDTO, String> state = newProperty();
	public static final Property<LocationDTO, String> zip = newProperty();
	public static final Property<LocationDTO, String> country = newProperty();
	public static final Property<LocationDTO, Double> longitude = newProperty();
	public static final Property<LocationDTO, Double> latitude = newProperty();
	public static final Property<LocationDTO, ContactDTO.Builder> contact = newProperty();
	public static final Property<LocationDTO, ContactDTO.Builder> secondaryContact = newProperty();
	public static final Property<LocationDTO, String> instructions = newProperty();

	public static final Instantiator<LocationDTO> LocationDTO = new Instantiator<LocationDTO>() {
		@Override
		public LocationDTO instantiate(PropertyLookup<LocationDTO> lookup) {
			return new LocationDTO.Builder()
				.setName(lookup.valueOf(name, "My house"))
				.setAddressLine1(lookup.valueOf(addressLine1, "20 West 20th Street"))
				.setAddressLine2(lookup.valueOf(addressLine2, "4th Floor"))
				.setCity(lookup.valueOf(city, "New York"))
				.setState(lookup.valueOf(state, "NY"))
				.setZip(lookup.valueOf(zip, "10010"))
				.setCountry(lookup.valueOf(country, Country.USA))
				.setLongitude(lookup.valueOf(longitude, -73.992325))
				.setLatitude(lookup.valueOf(latitude, 40.740075))
				.setContact(lookup.valueOf(contact, new ContactDTO.Builder(make(a(ContactDTO)))))
				.setSecondaryContact(lookup.valueOf(secondaryContact, new ContactDTO.Builder(make(a(ContactDTO)))))
				.setInstructions(lookup.valueOf(instructions, "At the end of the hallway"))
				.build();
		}
	};
}
