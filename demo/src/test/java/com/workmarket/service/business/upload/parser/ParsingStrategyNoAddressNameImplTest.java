package com.workmarket.service.business.upload.parser;


import com.workmarket.dao.LocationDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.thrift.CountryAssignmentHelperImpl;
import com.workmarket.service.thrift.LocationTimeZoneHelper;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * User: jasonpendrey
 * Date: 7/8/13
 * Time: 6:29 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class ParsingStrategyNoAddressNameImplTest {
	State state;
	@Mock InvariantDataService invariantDataService;
	@Mock CountryAssignmentHelperImpl countryAssignmentHelperImpl;
	@Mock LocationDAO locationDAO;
	@Mock LocationTimeZoneHelper locationTimeZoneHelper;
	@InjectMocks ParsingStrategyNoAddressNameImpl parsingStrategyNoAddressNameImpl;
	ClientLocation clientLocation = new ClientLocation();
	List<WorkRowParseError> errors;
	List<ClientLocation> locations = new ArrayList<ClientLocation>();

	@Before
	public void setUp() throws Exception {

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

		errors = new ArrayList<>();

		when(countryAssignmentHelperImpl.getCountryForAssignments(any(WorkUploadColumn.class), any(WorkUploaderBuildResponse.class), anyString())).thenReturn("USA");
		when(locationDAO.findLocationsByClientCompanyAndName(anyLong(), anyString())).thenReturn(locations);
	}

	@Test
	public void parseLocation_HasLocation_ReturnedTrue() throws Exception {
		locations.add(clientLocation);
		Map<String, String> types = new HashMap<>();
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		boolean parsed = parsingStrategyNoAddressNameImpl.parseLocation(types, response, errors, 4L);
		Assert.assertTrue(parsed);
	}

	@Test
	public void parseLocation_NoLocation_ReturnedFalse() throws Exception {
		Map<String, String> types = new HashMap<>();
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		boolean parsed = parsingStrategyNoAddressNameImpl.parseLocation(types, response, errors, 4L);
		Assert.assertFalse(parsed);
	}
}

