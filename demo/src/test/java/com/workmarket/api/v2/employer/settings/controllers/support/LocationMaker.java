package com.workmarket.api.v2.employer.settings.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.domains.model.postalcode.Country;
import org.apache.commons.lang3.StringUtils;

import static com.natpryce.makeiteasy.Property.newProperty;

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

	public static final Instantiator<LocationDTO> LocationDTO = new Instantiator<LocationDTO>() {
		@Override
		public LocationDTO instantiate(PropertyLookup<LocationDTO> lookup) {
			return new LocationDTO.Builder()
				.setAddressLine1(lookup.valueOf(addressLine1, "20 West 20th Street"))
				.setAddressLine2(lookup.valueOf(addressLine2, "4th Floor"))
				.setCity(lookup.valueOf(city, "New York"))
				.setState(lookup.valueOf(state, "NY"))
				.setZip(lookup.valueOf(zip, "10010"))
				.setCountry(lookup.valueOf(country, Country.USA))
				.setLongitude(lookup.valueOf(longitude, new Double(73.992325)))
				.setLatitude(lookup.valueOf(latitude, new Double(40.740075)))
				.build();
		}
	};

	public static final Instantiator<LocationDTO> EmptyLocationDTO = new Instantiator<LocationDTO>() {
		@Override
		public LocationDTO instantiate(PropertyLookup<LocationDTO> lookup) {
			return new LocationDTO.Builder()
				.setAddressLine1(lookup.valueOf(addressLine1, StringUtils.EMPTY))
				.setAddressLine2(lookup.valueOf(addressLine2, StringUtils.EMPTY))
				.setCity(lookup.valueOf(city, StringUtils.EMPTY))
				.setState(lookup.valueOf(state, StringUtils.EMPTY))
				.setZip(lookup.valueOf(zip, StringUtils.EMPTY))
				.setCountry(lookup.valueOf(country, StringUtils.EMPTY))
				.setLongitude(lookup.valueOf(longitude, 0.00d))
				.setLatitude(lookup.valueOf(latitude, 0.00d))
				.build();
		}
	};
}
