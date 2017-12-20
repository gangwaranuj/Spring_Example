package com.workmarket.api.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v1.model.ApiDressCodeDTO;
import com.workmarket.api.v1.model.ApiIndustryDTO;
import com.workmarket.api.v1.model.ApiLocationTypeDTO;
import com.workmarket.api.v1.model.ApiSubstatusDTO;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.service.business.dto.LocationTypeDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import org.apache.commons.collections.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by joshlevine on 1/20/17.
 */
@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ConstantsControllerTest extends BaseApiControllerTest {
	public static final String ENDPOINT_API_V1_LOCATION_TYPE = "/api/v1/constants/location_types";
	public static final String ENDPOINT_API_V1_DRESS_CODES = "/api/v1/constants/dress_codes";
	public static final String ENDPOINT_API_V1_INDUSTRIES = "/api/v1/constants/industries";
	public static final String ENDPOINT_API_V1_SUB_STATUSES = "/api/v1/constants/substatuses";
	public static final String ENDPOINT_API_V1_CANCEL_CODES = "/api/v1/constants/cancellation_codes";
	public static final String ENDPOINT_API_V1_UNASSIGN_CODES = "/api/v1/constants/unassign_codes";

	private static final TypeReference<ApiV1Response<List<ApiLocationTypeDTO>>> locationTypesType = new TypeReference<ApiV1Response<List<ApiLocationTypeDTO>>>() {};
	private static final TypeReference<ApiV1Response<List<ApiDressCodeDTO>>> dressCodesType = new TypeReference<ApiV1Response<List<ApiDressCodeDTO>>>() {};
	private static final TypeReference<ApiV1Response<List<ApiIndustryDTO>>> industriesType = new TypeReference<ApiV1Response<List<ApiIndustryDTO>>>() {};
	private static final TypeReference<ApiV1Response<List<ApiSubstatusDTO>>> substatusesType = new TypeReference<ApiV1Response<List<ApiSubstatusDTO>>>() {};
	private static final TypeReference<ApiV1Response<List<String>>> stringListType = new TypeReference<ApiV1Response<List<String>>>() {};


	@Mock InvariantDataService invariantDataService;
	@Mock IndustryService industryService;
	@Mock WorkSubStatusService workSubStatusService;
	@InjectMocks ConstantsController controller;
	
	@Before
	public void setup() throws Exception {
		super.setup(controller);
	}
	
	@Test
	public void testLocationTypes() throws Exception {
		LocationTypeDTO locationType = new LocationTypeDTO(1L, "description");
		List<LocationTypeDTO> locationTypes = ImmutableList.of(locationType);
		when(invariantDataService.getLocationTypeDTOs()).thenReturn(locationTypes);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.get(ENDPOINT_API_V1_LOCATION_TYPE)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
		
		ApiV1Response<List<ApiLocationTypeDTO>> apiResponse = expectApiV1Response(result, locationTypesType);
		assertTrue("Expect status 200", apiResponse.getMeta().getStatusCode() == HttpStatus.OK.value());
		assertTrue("Expect one locationType, found " + apiResponse.getResponse().size(), apiResponse.getResponse().size() == 1);
		assertTrue("Expect one locationTypes[0].id to match", apiResponse.getResponse().get(0).getId().equals(locationType.getId()));
		assertTrue("Expect one locationTypes[0].name to match", apiResponse.getResponse().get(0).getName().equals(locationType.getDescription()));
	}

	@Test
	public void testDressCodes() throws Exception {
		DressCode dressCode = new DressCode();
		dressCode.setId(1L);
		dressCode.setDescription("description");
		List<DressCode> dressCodes = ImmutableList.of(dressCode);
		when(invariantDataService.getDressCodes()).thenReturn(dressCodes);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.get(ENDPOINT_API_V1_DRESS_CODES)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		ApiV1Response<List<ApiDressCodeDTO>> apiResponse = expectApiV1Response(result, dressCodesType);
		assertTrue("Expect status 200", apiResponse.getMeta().getStatusCode() == HttpStatus.OK.value());
		assertTrue("Expect one dressCode, found " + apiResponse.getResponse().size(), apiResponse.getResponse().size() == 1);
		assertTrue("Expect one dressCodes[0].id to match", apiResponse.getResponse().get(0).getId().equals(dressCode.getId()));
		assertTrue("Expect one dressCodes[0].description to match", apiResponse.getResponse().get(0).getName().equals(dressCode.getDescription()));
	}
	
	@Test
	public void testIndustrys() throws Exception {
		IndustryDTO industry = new IndustryDTO();
		industry.setId(1L);
		industry.setName("name");
		industry.setOtherName("otherName");
		List<IndustryDTO> industries = ImmutableList.of(industry);
		when(industryService.getAllIndustryDTOs()).thenReturn(industries);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.get(ENDPOINT_API_V1_INDUSTRIES)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		ApiV1Response<List<ApiIndustryDTO>> apiResponse = expectApiV1Response(result, industriesType);
		assertTrue("Expect status 200", apiResponse.getMeta().getStatusCode() == HttpStatus.OK.value());
		assertTrue("Expect one industry, found " + apiResponse.getResponse().size(), apiResponse.getResponse().size() == 1);
		assertTrue("Expect one industries[0].id to match", apiResponse.getResponse().get(0).getId().equals(industry.getId()));
		assertTrue("Expect one industries[0].name to match", apiResponse.getResponse().get(0).getName().equals(industry.getName()));
	}	
	
	@Test
	public void testSubStatuss() throws Exception {
		WorkSubStatusType subStatus = new WorkSubStatusType();
		subStatus.setCode("code");
		subStatus.setDescription("description");
		List<WorkSubStatusType> substatuses = ImmutableList.of(subStatus);
		final ArgumentCaptor<WorkSubStatusTypeFilter> workSubstatusTypeFilterCaptor = ArgumentCaptor.forClass(WorkSubStatusTypeFilter.class);
		doReturn(substatuses).when(workSubStatusService).findAllSubStatuses(eq(user.getCompany().getId().longValue()), workSubstatusTypeFilterCaptor.capture());

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.get(ENDPOINT_API_V1_SUB_STATUSES)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		WorkSubStatusTypeFilter filter = workSubstatusTypeFilterCaptor.getValue();


		filter.setShowSystemSubStatus(true);
		filter.setShowCustomSubStatus(true);
		filter.setShowDeactivated(false);
		filter.setClientVisible(true);
		filter.setResourceVisible(true);

		assertTrue("Expect showSystemSubstatus to be true", filter.isShowSystemSubStatus());
		assertTrue("Expect showCustomSubstatus to be true", filter.isShowCustomSubStatus());
		assertFalse("Expect setShowDeactivated to be false", filter.isShowDeactivated());
		assertTrue("Expect setClientVisible to be true", filter.isClientVisible());
		assertTrue("Expect setResourceVisible to be true", filter.isResourceVisible());

		ApiV1Response<List<ApiSubstatusDTO>> apiResponse = expectApiV1Response(result, substatusesType);
		assertTrue("Expect status 200", apiResponse.getMeta().getStatusCode() == HttpStatus.OK.value());
		assertTrue("Expect one subStatus, found " + apiResponse.getResponse().size(), apiResponse.getResponse().size() == 1);
		assertTrue("Expect one subStatus[0].code to match", apiResponse.getResponse().get(0).getId().equals(subStatus.getCode()));
		assertTrue("Expect one sub_status[0].name to match", apiResponse.getResponse().get(0).getName().equals(subStatus.getDescription()));
	}

	@Test
	public void testCancellationCodes() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.get(ENDPOINT_API_V1_CANCEL_CODES)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		ApiV1Response<List<String>> apiResponse = expectApiV1Response(result, stringListType);

		assertTrue("Expect status 200", apiResponse.getMeta().getStatusCode() == HttpStatus.OK.value());
		assertTrue("Expect codes to match", ListUtils.isEqualList(apiResponse.getResponse(), CancellationReasonType.cancellationReasons));
	}

	@Test
	public void testUnassignCodes() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.get(ENDPOINT_API_V1_UNASSIGN_CODES)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		ApiV1Response<List<String>> apiResponse = expectApiV1Response(result, stringListType);
		List<String> unassignCodes = ImmutableList.of(CancellationReasonType.RESOURCE_ABANDONED, CancellationReasonType.RESOURCE_CANCELLED);

		assertTrue("Expect status 200", apiResponse.getMeta().getStatusCode() == HttpStatus.OK.value());
		assertTrue("Expect codes to match", ListUtils.isEqualList(apiResponse.getResponse(), unassignCodes));
	}
}
