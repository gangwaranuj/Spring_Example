package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.exceptions.MessageSourceApiException;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.fulfillment.NegotiationFulfillmentProcessor;
import com.workmarket.api.v2.worker.model.AssignmentApplicationDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ApplicationControllerTest extends BaseApiControllerTest {

	private static final String APPLICATION_URL = "/worker/v2/assignments/{workNumber}/application";
	private static final String VALID_WORK_NUMBER = "123456";
	private static final String INVALID_WORK_NUMBER = "xxxxxx";

	@InjectMocks private ApplicationController controller;
	@Mock private NegotiationFulfillmentProcessor negotiationFulfillmentProcessor;

	private FulfillmentPayloadDTO successResponse;

	private ObjectMapper jackson = new ObjectMapper();

	@Before
	public void setup() {

		successResponse = createSuccessResponse();
	}

	@Test
	public void apply_withValidWorkNumber_shouldReturnValidResult() throws Exception {

		final AssignmentApplicationDTO dto = new AssignmentApplicationDTO.Builder().build();

		when(negotiationFulfillmentProcessor.applyForAssignment(VALID_WORK_NUMBER, dto)).thenReturn(successResponse);

		final ApiV2Response apiResponse = controller.postApplyAssignment(VALID_WORK_NUMBER, dto);

		verify(negotiationFulfillmentProcessor, times(1)).applyForAssignment(VALID_WORK_NUMBER, dto);


		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());
		assertEquals("Successfully applied to assignment.", apiResponse.getResults().get(0));
	}

	@Test
	public void apply_withFailure_shouldThrowsMessageSourceApiException() throws Exception {

		final AssignmentApplicationDTO dto = new AssignmentApplicationDTO.Builder().build();

		final FulfillmentPayloadDTO failureResponse = new FulfillmentPayloadDTO();
		failureResponse.setSuccessful(Boolean.FALSE);

		when(negotiationFulfillmentProcessor.applyForAssignment(VALID_WORK_NUMBER, dto)).thenReturn(failureResponse);

		try {

			final ApiV2Response response = controller.postApplyAssignment(VALID_WORK_NUMBER, dto);
			fail("Expected a MessageSourceApiException to be thrown");
		}
		catch (final MessageSourceApiException msae) {

			verify(negotiationFulfillmentProcessor, times(1)).applyForAssignment(VALID_WORK_NUMBER, dto);
			assertEquals("assignment.apply.failure", msae.getMessage());
		}
		catch (final Exception e) {

			fail("Expected a MessageSourceApiException to be thrown instead, was a " + e.getClass());
		}
	}

	private FulfillmentPayloadDTO createSuccessResponse() {

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		response.setSuccessful(Boolean.TRUE);
		response.addResponseResult("Successfully applied to assignment.");

		return response;
	}
}
