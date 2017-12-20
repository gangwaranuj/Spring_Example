package com.workmarket.web.converters;

import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.web.forms.addressbook.LocationForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationDTOToLocationFormConverterTest {

	@Mock AddressDTOToAddressFormConverter addressDTOToAddressFormConverter;
	@InjectMocks LocationDTOToLocationFormConverter locationDTOToLocationFormConverter;

	LocationDTO locationDTO;

	Long
		id = 1L,
		companyId = 2L;
	String
		name = "name",
		number = "num";

	@Before
	public void setup() {
		locationDTO = mock(LocationDTO.class);

		when(locationDTO.getId()).thenReturn(id);
		when(locationDTO.getClientCompanyId()).thenReturn(companyId);
		when(locationDTO.getName()).thenReturn(name);
		when(locationDTO.getLocationNumber()).thenReturn(number);
	}

	@Test
	public void convert_withNullLocationDTO_returnNull() {
		assertNull(locationDTOToLocationFormConverter.convert(null));
	}

	@Test
	public void convert_withNonNullLocationDTO_setAllPropertiesOnLocationForm() {
		LocationForm form = locationDTOToLocationFormConverter.convert(locationDTO);

		assertEquals(form.getId(), locationDTO.getId());
		assertEquals(form.getClient_company(), locationDTO.getClientCompanyId());
		assertEquals(form.getName(), locationDTO.getName());
		assertEquals(form.getNumber(), locationDTO.getLocationNumber());
		verify(addressDTOToAddressFormConverter).convert(locationDTO);
	}
}
