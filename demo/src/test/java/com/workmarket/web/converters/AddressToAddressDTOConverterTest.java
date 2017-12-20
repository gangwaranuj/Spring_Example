package com.workmarket.web.converters;

import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.dto.AddressDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddressToAddressDTOConverterTest {

	@InjectMocks AddressToAddressDTOConverter addressToAddressDTOConverter;

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
		number = "num",
		address1 = "1",
		address2 = "2",
		city = "c",
		stateCode = "NY",
		zip = "z",
		countryCode = "USA";

	@Before
	public void setup() {
		address = mock(Address.class);
		state = mock(State.class);
		country = mock(Country.class);
		locationType = mock(LocationType.class);

		when(address.getAddress1()).thenReturn(address1);
		when(address.getAddress2()).thenReturn(address2);
		when(address.getCity()).thenReturn(city);
		when(address.getState()).thenReturn(state);
		when(address.getPostalCode()).thenReturn(zip);
		when(address.getCountry()).thenReturn(country);
		when(address.getLocationType()).thenReturn(locationType);
		when(state.getShortName()).thenReturn(stateCode);
		when(country.getId()).thenReturn(countryCode);
		when(locationType.getId()).thenReturn(locationTypeCode);
	}

	@Test
	public void convert_withNullAddress_returnNull() {
		assertNull(addressToAddressDTOConverter.convert(null));
	}

	@Test
	public void convert_withNonNullLocation_setAllPropertiesOnAddressDTO() {
		AddressDTO addressDTO = addressToAddressDTOConverter.convert(address);

		assertEquals(addressDTO.getAddress1(), address.getAddress1());
		assertEquals(addressDTO.getAddress2(), address.getAddress2());
		assertEquals(addressDTO.getCity(), address.getCity());
		assertEquals(addressDTO.getState(), address.getState().getShortName());
		assertEquals(addressDTO.getPostalCode(), address.getPostalCode());
		assertEquals(addressDTO.getCountry(), address.getCountry().getId());
		assertEquals(addressDTO.getLocationTypeId(), locationTypeCode);
	}

	@Test
	public void convert_withNullState_stateCodeNotSetOnAddressDTO() {
		when(address.getState()).thenReturn(null);

		AddressDTO addressDTO = addressToAddressDTOConverter.convert(address);

		assertNull(addressDTO.getState());
	}

	@Test
	public void convert_withNullCountry_countryIDNotSetOnAddressDTO() {
		when(address.getCountry()).thenReturn(null);

		AddressDTO addressDTO = addressToAddressDTOConverter.convert(address);

		assertNull(addressDTO.getCountry());
	}
}
