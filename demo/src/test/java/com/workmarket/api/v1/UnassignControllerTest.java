package com.workmarket.api.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.UnassignDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class UnassignControllerTest extends BaseApiControllerTest {
	
	private static final TypeReference<ApiV1Response<ApiV1ResponseStatus>> apiV1ResponseStatusType = new TypeReference<ApiV1Response<ApiV1ResponseStatus>>() {};

	@Mock private Work work;
	@Mock private Company company;
	@Mock private WorkService workService;
	@InjectMocks private UnassignController controller = new UnassignController();

	UnassignDTO unassignDTO;

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		when(work.getCompany()).thenReturn(company);
		when(workService.findWorkByWorkNumber(anyString())).thenReturn(work);
		when(workService.unassignWorker(any(UnassignDTO.class))).thenReturn(ImmutableList.<ConstraintViolation>of());
		unassignDTO = new UnassignDTO()
			.setNote("hello")
			.setCancellationReasonTypeCode(CancellationReasonType.RESOURCE_CANCELLED)
			.setRollbackToOriginalPrice(true);
	}

	@Test
	public void testUnassign_sameCompany_success() throws Exception {
		String workNumber = "123456890";

		when(company.getId()).thenReturn(DEFAULT_COMPANY_ID);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post("/api/v1/assignments/" + workNumber + "/unassign")
			.header("accept", MediaType.APPLICATION_JSON)
			.param("cancellation_reason", unassignDTO.getCancellationReasonTypeCode())
			.param("rollback_to_original_price", String.valueOf(unassignDTO.isRollbackToOriginalPrice()))
			.param("note", unassignDTO.getNote())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)).andExpect(status().isOk()).andReturn();

		ApiV1Response<ApiV1ResponseStatus> apiResponse = expectApiV1Response(result, apiV1ResponseStatusType);
		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());
		verify(workService).findWorkByWorkNumber(workNumber);
		verify(workService).unassignWorker(refEq(unassignDTO, "workId"));
	}



	@Test
	public void testUnassign_differentCompany_failure() throws Exception {
		String workNumber = "123456890";

		when(company.getId()).thenReturn(9153L);
		
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post("/api/v1/assignments/" + workNumber + "/unassign")
			.header("accept", MediaType.APPLICATION_JSON)
			.param("cancellation_reason", unassignDTO.getCancellationReasonTypeCode())
			.param("rollback_to_original_price", String.valueOf(unassignDTO.isRollbackToOriginalPrice()))
			.param("note", unassignDTO.getNote())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)).andExpect(status().isOk()).andReturn();

		ApiV1Response<ApiV1ResponseStatus> apiResponse = expectApiV1Response(result, apiV1ResponseStatusType);
		expectStatusCode(HttpStatus.FORBIDDEN.value(), apiResponse.getMeta());
		verify(workService).findWorkByWorkNumber(workNumber);
	}

	@Test
	public void testUnassign_workNotFound_failure() throws Exception {
		String workNumber = "99999999";

		when(workService.findWorkByWorkNumber(anyString())).thenReturn(null);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post("/api/v1/assignments/" + workNumber + "/unassign")
			.header("accept", MediaType.APPLICATION_JSON)
			.param("cancellation_reason", unassignDTO.getCancellationReasonTypeCode())
			.param("rollback_to_original_price", String.valueOf(unassignDTO.isRollbackToOriginalPrice()))
			.param("note", unassignDTO.getNote())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)).andExpect(status().isOk()).andReturn();

		ApiV1Response<ApiV1ResponseStatus> apiResponse = expectApiV1Response(result, apiV1ResponseStatusType);
		expectStatusCode(HttpStatus.FORBIDDEN.value(), apiResponse.getMeta());
		verify(workService).findWorkByWorkNumber(workNumber);
	}
}