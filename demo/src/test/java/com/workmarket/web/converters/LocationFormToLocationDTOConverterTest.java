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
public class LocationFormToLocationDTOConverterTest {

	@Mock AddressFormToAddressDTOConverter addressFormToAddressDTOConverter;
	@InjectMocks LocationFormToLocationDTOConverter locationFormToLocationDTOConverter;
	
	LocationForm locationForm;

	Long
		id = 1L,
		companyId = 2L;
	String
		name = "name",
		number = "num";

	@Before
	public void setup() {
		locationForm = mock(LocationForm.class);
		
		when(locationForm.getId()).thenReturn(id);
		when(locationForm.getClient_company()).thenReturn(companyId);
		when(locationForm.getName()).thenReturn(name);
		when(locationForm.getNumber()).thenReturn(number);
	}

	@Test
	public void convert_withNullLocationForm_returnNull() {
		assertNull(locationFormToLocationDTOConverter.convert(null));
	}
	
	@Test
	public void convert_withNonNullLocationForm_setAllPropertiesOnLocationDTO() {
		LocationDTO dto = locationFormToLocationDTOConverter.convert(locationForm);
		
		assertEquals(dto.getId(), locationForm.getId());
		assertEquals(dto.getClientCompanyId(), locationForm.getClient_company());
		assertEquals(dto.getName(), locationForm.getName());
		assertEquals(dto.getLocationNumber(), locationForm.getNumber());
		verify(addressFormToAddressDTOConverter).convert(locationForm);
	}
}
