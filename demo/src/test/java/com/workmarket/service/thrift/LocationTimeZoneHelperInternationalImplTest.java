package com.workmarket.service.thrift;

import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.thrift.work.Work;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationTimeZoneHelperInternationalImplTest {
	State state;
	@Mock InvariantDataService invariantDataService;
	@Mock CountryAssignmentHelperImpl countryAssignmentHelperImpl;
	@InjectMocks LocationTimeZoneHelperInternationalImpl locationTimeZoneHelperInternationalImpl;
	ClientLocation clientLocation = new ClientLocation();

	@Before
	public void setUp() throws Exception {
		state = mock(State.class);
		when(state.getCountry()).thenReturn(Country.newInstance("US"));
		PostalCode postalCode = mock(PostalCode.class);
		TimeZone timeZone = mock(TimeZone.class);
		when(postalCode.getTimeZone()).thenReturn(timeZone);
		when(timeZone.getId()).thenReturn(1L);
		when(timeZone.getTimeZoneId()).thenReturn("US/Pacific");
		when(countryAssignmentHelperImpl.getCountryForAssignments(any(WorkUploadColumn.class), any(WorkUploaderBuildResponse.class), anyString())).thenReturn("USA");
		when(invariantDataService.findStateWithCountryAndState(anyString(), anyString())).thenReturn(state);
		when(invariantDataService.getPostalCodeByCodeCountryStateCity(anyString(), anyString(), anyString(), anyString())).thenReturn(postalCode);
		when(invariantDataService.findOrCreatePostalCode(any(AddressDTO.class))).thenReturn(postalCode);

		LocationDTO locationDTO = new LocationDTO();
		locationDTO.setName("Footlocker Upper West ");
		locationDTO.setAddress1("20E 80st");
		locationDTO.setCity("New York");
		locationDTO.setState("NY");
		locationDTO.setPostalCode("10075");
		locationDTO.setCountry("USA");
		locationDTO.setDressCodeId(2L);
		BeanUtils.copyProperties(locationDTO, clientLocation);

		clientLocation.setId(2L);
		Address address = new Address();
		address.setAddress1("Footlocker Upper West ");
		address.setAddress2("20E 80st");
		address.setCity("New York");
		address.setState(state);
		address.setPostalCode("10075");
		address.setCountry(Country.newInstance("USA"));
		LocationType locationType = new LocationType();
		locationType.setDescription("RESIDENTIAL");
		DressCode dressCode = new DressCode();
		address.setLocationType(locationType);
		dressCode.setDescription("clothing");
		address.setDressCode(dressCode);
		clientLocation.setAddress(address);
	}

	@Test
	public void testSetLocationAndTimeZone_UsPacificTimeGiven_UsPacificTimeZoneRetrieved() throws Exception {
		Map<String, String> types = new HashMap<>();
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		locationTimeZoneHelperInternationalImpl.setLocationAndTimeZone(response, types, 4);
		Assert.assertEquals("US/Pacific", response.getWork().getTimeZone());
	}

	@Test
	public void testSetLocationAndTimeZone_() throws Exception {
		Map<String, String> types = new HashMap<>();
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work().setTimeZone(null));
		locationTimeZoneHelperInternationalImpl.setLocationAndTimeZone(response, types, 4);
		Assert.assertEquals("US/Pacific", response.getWork().getTimeZone());
	}

	@Test
	public void testSetLocationAndTimeZoneByLocationId_UsPacificTimeGiven_UsPacificTimeZoneRetrieved() throws Exception {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		locationTimeZoneHelperInternationalImpl.setLocationAndTimeZoneByLocationId(response, clientLocation, "4");
		Assert.assertEquals("US/Pacific", response.getWork().getTimeZone());
	}
}
