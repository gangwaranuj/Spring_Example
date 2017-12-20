package com.workmarket.web.converters;

import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.service.business.dto.LocationDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationToLocationDTOConverterTest {

	@Mock AddressToAddressDTOConverter addressToAddressDTOConverter;
	@InjectMocks LocationToLocationDTOConverter locationToLocationDTOConverter;

	Location location;
	Company company;
	Address address;
	State state;
	Country country;
	LocationType locationType;

	Long
		id = 1L,
		companyId = 2L,
		locationTypeCode = LocationType.COMMERCIAL_CODE;
	String
		name = "name",
		number = "num";

	@Before
	public void setup() {
		location = mock(Location.class);
		company = mock(Company.class);
		address = mock(Address.class);
		state = mock(State.class);
		country = mock(Country.class);
		locationType = mock(LocationType.class);

		when(location.getId()).thenReturn(id);
		when(location.getCompany()).thenReturn(company);
		when(location.getName()).thenReturn(name);
		when(location.getLocationNumber()).thenReturn(number);
		when(location.getCompany()).thenReturn(company);
		when(location.getAddress()).thenReturn(address);
		when(company.getId()).thenReturn(companyId);
		when(locationType.getId()).thenReturn(locationTypeCode);
	}

	@Test
	public void convert_withNullLocation_returnNull() {
		assertNull(locationToLocationDTOConverter.convert(null));
	}

	@Test
	public void convert_withNonNullLocation_setAllPropertiesOnLocationDTO() {
		LocationDTO locationDTO = locationToLocationDTOConverter.convert(location);

		assertEquals(locationDTO.getId(), location.getId());
		assertEquals(locationDTO.getName(), location.getName());
		assertEquals(locationDTO.getLocationNumber(), location.getLocationNumber());
		assertEquals(locationDTO.getCompanyId(), location.getCompany().getId());
		verify(addressToAddressDTOConverter).convert(address);
	}

	@Test
	public void convert_withNullCompany_companyIdNotSetOnLocationDTO() {
		when(location.getCompany()).thenReturn(null);

		LocationDTO locationDTO = locationToLocationDTOConverter.convert(location);

		assertNull(locationDTO.getCompanyId());
	}

	@Test
	public void convert_withNullAddress_dontConvertAddress() {
		when(location.getAddress()).thenReturn(null);

		locationToLocationDTOConverter.convert(location);

		verify(addressToAddressDTOConverter, never()).convert(address);
	}
}
