package com.workmarket.web.converters;

import com.workmarket.domains.model.LocationType;
import com.workmarket.dto.AddressDTO;
import com.workmarket.web.forms.base.AddressForm;
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
public class AddressDTOToAddressFormConverterTest {

	@InjectMocks AddressDTOToAddressFormConverter addressDTOToAddressFormConverter;

	AddressDTO addressDTO;

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
		postalCode = "z",
		country = "USA",
		locationType = LocationType.COMMERCIAL;

	@Before
	public void setup() {
		addressDTO = mock(AddressDTO.class);

		when(addressDTO.getAddress1()).thenReturn(address1);
		when(addressDTO.getAddress2()).thenReturn(address2);
		when(addressDTO.getCity()).thenReturn(city);
		when(addressDTO.getState()).thenReturn(state);
		when(addressDTO.getPostalCode()).thenReturn(postalCode);
		when(addressDTO.getCountry()).thenReturn(country);
		when(addressDTO.getLocationTypeId()).thenReturn(locationTypeCode);
	}

	@Test
	public void convert_withNullAddressDTO_returnNull() {
		assertNull(addressDTOToAddressFormConverter.convert(null));
	}

	@Test
	public void convert_withNonNullAddressDTO_setAllPropertiesOnAddressForm() {
		AddressForm form = addressDTOToAddressFormConverter.convert(addressDTO);

		assertEquals(form.getAddress1(), addressDTO.getAddress1());
		assertEquals(form.getAddress2(), addressDTO.getAddress2());
		assertEquals(form.getCity(), addressDTO.getCity());
		assertEquals(form.getState(), addressDTO.getState());
		assertEquals(form.getPostalCode(), addressDTO.getPostalCode());
		assertEquals(form.getCountry(), addressDTO.getCountry());
		assertEquals(form.getLocation_type(), addressDTO.getLocationTypeId());
	}
}
