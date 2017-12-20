package com.workmarket.web.converters;

import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.domains.model.LocationType;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Company;
import com.workmarket.thrift.core.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationThriftToLocationDTOConverterTest {

	@InjectMocks LocationThriftToLocationDTOConverter locationThriftToLocationDTOConverter;

	Location location;
	Address address;
	Company company;
	GeoPoint latLong;

	Long
		id = 1L,
		companyId = 2L,
		locationTypeCode = LocationType.COMMERCIAL_CODE;
	String
		name = "name",
		number = "num",
		address1 = "1",
		address2 = "2",
		city = "c",
		state = "s",
		zip = "z",
		country = "country",
		locationType = LocationType.COMMERCIAL;
	double lat = 1, lng = 2;

	@Before
	public void setup() {
		location = mock(Location.class);
		address = mock(Address.class);
		company = mock(Company.class);
		latLong = mock(GeoPoint.class);

		when(location.getId()).thenReturn(id);
		when(location.getName()).thenReturn(name);
		when(location.getNumber()).thenReturn(number);
		when(location.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(companyId);
		when(location.getAddress()).thenReturn(address);
		when(address.getAddressLine1()).thenReturn(address1);
		when(address.getAddressLine2()).thenReturn(address2);
		when(address.getCity()).thenReturn(city);
		when(address.getState()).thenReturn(state);
		when(address.getZip()).thenReturn(zip);
		when(address.getCountry()).thenReturn(country);
		when(address.getType()).thenReturn(locationType);
		when(latLong.getLatitude()).thenReturn(lat);
		when(latLong.getLongitude()).thenReturn(lng);
		when(address.getPoint()).thenReturn(latLong);
	}

	@Test
	public void convert_nullLocationThrift_returnNull() {
		assertNull(locationThriftToLocationDTOConverter.convert(null));
	}

	@Test
	public void convert_withNonNullLocationThrift_setAllPropertiesOnLocationDTO() {
		LocationDTO locationDTO = locationThriftToLocationDTOConverter.convert(location);
		
		assertEquals(locationDTO.getId().longValue(), location.getId());
		assertEquals(locationDTO.getName(), location.getName());
		assertEquals(locationDTO.getLocationNumber(), location.getNumber());
		assertEquals(locationDTO.getCompanyId().longValue(), location.getCompany().getId());
		assertEquals(locationDTO.getAddress1(), location.getAddress().getAddressLine1());
		assertEquals(locationDTO.getAddress2(), location.getAddress().getAddressLine2());
		assertEquals(locationDTO.getCity(), location.getAddress().getCity());
		assertEquals(locationDTO.getState(), location.getAddress().getState());
		assertEquals(locationDTO.getPostalCode(), location.getAddress().getZip());
		assertEquals(locationDTO.getCountry(), location.getAddress().getCountry());
		assertEquals(locationDTO.getLocationTypeId(), locationTypeCode);
		assertEquals(locationDTO.getLatitude(), new BigDecimal(location.getAddress().getPoint().getLatitude()));
		assertEquals(locationDTO.getLongitude(), new BigDecimal(location.getAddress().getPoint().getLongitude()));
	}

	@Test
	public void convert_withNullCompany_companyIdNotSetOnLocationDTO() {
		when(location.getCompany()).thenReturn(null);

		LocationDTO locationDTO = locationThriftToLocationDTOConverter.convert(location);

		assertEquals(locationDTO.getCompanyId(), null);
	}

	@Test
	public void convert_withNullAddress_noAddressPropertiesSet() {
		when(location.getAddress()).thenReturn(null);

		LocationDTO locationDTO = locationThriftToLocationDTOConverter.convert(location);

		assertEquals(locationDTO.getAddress1(), null);
		assertEquals(locationDTO.getAddress2(), null);
		assertEquals(locationDTO.getCity(), null);
		assertEquals(locationDTO.getState(), null);
		assertEquals(locationDTO.getPostalCode(), null);
		assertEquals(locationDTO.getCountry(), null);
		assertEquals(locationDTO.getLocationTypeId(), null);
		assertEquals(locationDTO.getDressCodeId(), null);
		assertEquals(locationDTO.getLatitude(), null);
		assertEquals(locationDTO.getLongitude(), null);
	}

	@Test
	public void convert_withNullGeoPoint_latLongIsNotSet() {
		when(address.getPoint()).thenReturn(null);

		LocationDTO locationDTO = locationThriftToLocationDTOConverter.convert(location);

		assertEquals(locationDTO.getLatitude(), null);
		assertEquals(locationDTO.getLongitude(), null);
	}

	@Test
	public void convert_withZeroLatitude_latitudeIsNotSet() {
		when(address.getPoint().getLatitude()).thenReturn(0d);

		LocationDTO locationDTO = locationThriftToLocationDTOConverter.convert(location);

		assertEquals(locationDTO.getLatitude(), null);
	}

	@Test
	public void convert_withZeroLongitude_longitudeIsNotSet() {
		when(address.getPoint().getLongitude()).thenReturn(0d);

		LocationDTO locationDTO = locationThriftToLocationDTOConverter.convert(location);

		assertEquals(locationDTO.getLongitude(), null);
	}
}
