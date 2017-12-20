package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.model.ApiAcceptWorkDTO;
import com.workmarket.api.v2.worker.model.ApiDeclineWorkDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import com.workmarket.service.business.status.AcceptWorkStatus;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class AcceptControllerTest extends BaseApiControllerTest {
	private static final TypeReference<ApiV2Response<ApiAcceptWorkDTO>> acceptWorkResponseType = new TypeReference<ApiV2Response<ApiAcceptWorkDTO>>() {};
	private static final TypeReference<ApiV2Response<ApiDeclineWorkDTO>> declineWorkResponseType = new TypeReference<ApiV2Response<ApiDeclineWorkDTO>>() {};

	private static final String ACCEPT_URL = "/worker/v2/assignments/{workNumber}/accept";
	private static final String DECLINE_URL = "/worker/v2/assignments/{workNumber}/decline";

	private static final String DEPRECATED_ACCEPT_URL = "/worker/v2/assignments/accept/{workNumber}";
	private static final String DEPRECATED_DECLINE_URL = "/worker/v2/assignments/reject/{workNumber}";

	private static final String VALID_WORK_NUMBER = "123456";
	private static final String INVALID_WORK_NUMBER = "xxxxxx";

	@Mock private XAssignment xAssignment;
	@Mock protected MessageBundleHelper messageHelper;

	@InjectMocks
	private AcceptController controller = new AcceptController();

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		final AcceptWorkResponse acceptWorkResponse = new AcceptWorkResponse(AcceptWorkStatus.SUCCESS);

		when(xAssignment.accept(anyLong(), anyString())).thenReturn(acceptWorkResponse);

		when(xAssignment.accept(anyLong(), eq(INVALID_WORK_NUMBER))).thenThrow(new RuntimeException("sss"));

		when(xAssignment.decline(anyLong(), anyString())).thenReturn(true);

		when(xAssignment.decline(anyLong(), eq(INVALID_WORK_NUMBER))).thenThrow(new RuntimeException("sss"));
	}

	@Test
	public void accept_withValidWorkNumber_shouldReturnValidResponse() throws Exception {
		successfulAcceptWorkApiCall(VALID_WORK_NUMBER, ACCEPT_URL);
	}

	@Test
	public void accept_withInvalidWorkNumber_shouldReturnValidResponse() throws Exception {
		internalErrorApiCall(INVALID_WORK_NUMBER, ACCEPT_URL);
	}


	@Test
	public void decline_withValidWorkNumber_shouldReturnValidResponse() throws Exception {
		successfulDeclineWorkApiCall(VALID_WORK_NUMBER, DECLINE_URL);
	}

	@Test
	public void decline_withInvalidWorkNumber_shouldReturnValidResponse() throws Exception {
		internalErrorApiCall(INVALID_WORK_NUMBER, DECLINE_URL);
	}

	@Test
	public void deprecatedaccept_withValidWorkNumber_shouldReturnValidResponse() throws Exception {
		successfulAcceptWorkApiCall(VALID_WORK_NUMBER, DEPRECATED_ACCEPT_URL);
	}

	@Test
	public void deprecatedaccept_withInvalidWorkNumber_shouldReturnValidResponse() throws Exception {
		internalErrorApiCall(INVALID_WORK_NUMBER, DEPRECATED_ACCEPT_URL);
	}


	@Test
	public void deprecateddecline_withValidWorkNumber_shouldReturnValidResponse() throws Exception {
		successfulDeclineWorkApiCall(VALID_WORK_NUMBER, DEPRECATED_DECLINE_URL);
	}

	@Test
	public void deprecateddecline_withInvalidWorkNumber_shouldReturnValidResponse() throws Exception {
		internalErrorApiCall(INVALID_WORK_NUMBER, DEPRECATED_DECLINE_URL);
	}

	private ResultActions apiCall(String workNumber, String endpoint) throws Exception {
		return mockMvc.perform(post(endpoint, workNumber));
	}

	private void successfulAcceptWorkApiCall(String workNumber, String endpoint) throws Exception {
		MvcResult result = apiCall(workNumber, endpoint).andExpect(status().isOk()).andReturn();
		ApiV2Response<ApiAcceptWorkDTO> apiResponse = expectApiV2Response(result, acceptWorkResponseType);
		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());
		assertTrue("Expected isSuccessful to be true", apiResponse.getResults().get(0).isSuccessful());
	}

	private void successfulDeclineWorkApiCall(String workNumber, String endpoint) throws Exception {
		MvcResult result = apiCall(workNumber, endpoint).andExpect(status().isOk()).andReturn();
		ApiV2Response<ApiDeclineWorkDTO> apiResponse = expectApiV2Response(result, declineWorkResponseType);
		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());
		assertTrue("Expected isSuccessful to be true", apiResponse.getResults().get(0).isSuccessful());
	}

	private void internalErrorApiCall(String workNumber, String endpoint) throws Exception {
		apiCall(workNumber, endpoint).andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.meta.code", is(500)));
	}


}
