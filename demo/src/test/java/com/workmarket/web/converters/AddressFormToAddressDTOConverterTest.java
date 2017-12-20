	package com.workmarket.web.converters;

import com.workmarket.domains.model.LocationType;
import com.workmarket.dto.AddressDTO;
import com.workmarket.web.forms.base.AddressForm;
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
public class AddressFormToAddressDTOConverterTest {

	@InjectMocks AddressFormToAddressDTOConverter addressFormToAddressDTOConverter;

	AddressForm addressForm;
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
		locationType = LocationType.COMMERCIAL,
		latitude = "123",
		longitude = "456",
		badLatLong = "ASDF";

	@Before
	public void setup() {
		addressForm = mock(AddressForm.class);

		when(addressForm.getAddress1()).thenReturn(address1);
		when(addressForm.getAddress2()).thenReturn(address2);
		when(addressForm.getCity()).thenReturn(city);
		when(addressForm.getState()).thenReturn(state);
		when(addressForm.getPostalCode()).thenReturn(postalCode);
		when(addressForm.getCountry()).thenReturn(country);
		when(addressForm.getLocation_type()).thenReturn(locationTypeCode);
		when(addressForm.getLatitude()).thenReturn(latitude);
		when(addressForm.getLongitude()).thenReturn(longitude);
	}

	@Test
	public void convert_withNullAddressForm_returnNull() {
		assertNull(addressFormToAddressDTOConverter.convert(null));
	}

	@Test
	public void convert_withNonNullAddressForm_setAllProperties() {
		AddressDTO addressDTO = addressFormToAddressDTOConverter.convert(addressForm);

		assertEquals(addressDTO.getAddress1(), addressForm.getAddress1());
		assertEquals(addressDTO.getAddress2(), addressForm.getAddress2());
		assertEquals(addressDTO.getCity(), addressForm.getCity());
		assertEquals(addressDTO.getState(), addressForm.getState());
		assertEquals(addressDTO.getPostalCode(), addressForm.getPostalCode());
		assertEquals(addressDTO.getCountry(), addressForm.getCountry());
		assertEquals(addressDTO.getLocationTypeId(), addressForm.getLocation_type());
		assertEquals(addressDTO.getLatitude(), new BigDecimal(addressForm.getLatitude()));
		assertEquals(addressDTO.getLongitude(), new BigDecimal(addressForm.getLongitude()));
	}

	@Test
	public void convert_withInvalidLatitude_setLatLongAsNull() {
		when(addressForm.getLatitude()).thenReturn(badLatLong);

		AddressDTO addressDTO = addressFormToAddressDTOConverter.convert(addressForm);

		assertEquals(addressDTO.getLatitude(), null);
		assertEquals(addressDTO.getLongitude(), null);
	}

	@Test
	public void convert_withInvalidLongitude_setLatLongAsNull() {
		when(addressForm.getLongitude()).thenReturn(badLatLong);

		AddressDTO addressDTO = addressFormToAddressDTOConverter.convert(addressForm);

		assertEquals(addressDTO.getLatitude(), null);
		assertEquals(addressDTO.getLongitude(), null);
	}

	@Test
	public void convert_withBlankLatitude_setLatLongAsNull() {
		when(addressForm.getLatitude()).thenReturn("  ");

		AddressDTO addressDTO = addressFormToAddressDTOConverter.convert(addressForm);

		assertEquals(addressDTO.getLatitude(), null);
	}

	@Test
	public void convert_withBlankLongitude_setLatLongAsNull() {
		when(addressForm.getLongitude()).thenReturn("  ");

		AddressDTO addressDTO = addressFormToAddressDTOConverter.convert(addressForm);

		assertEquals(addressDTO.getLongitude(), null);
	}
}
