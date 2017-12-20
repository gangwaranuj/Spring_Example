package com.workmarket.api.v2.worker.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.fulfillment.NegotiationFulfillmentProcessor;
import com.workmarket.api.v2.worker.model.NegotiationDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class AssignmentNegotiationControllerTest extends BaseApiControllerTest {

	private MockHttpServletResponse response;

	@Mock private NegotiationFulfillmentProcessor fulfillmentProcessor;
	@InjectMocks private AssignmentNegotiationController controller = new AssignmentNegotiationController();

	private FulfillmentPayloadDTO goodResponse;

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		response = new MockHttpServletResponse();

		when(messageHelper.getMessage("NotEmpty.budgetIncreaseForm.note")).thenReturn(
						"You must add a reason for the budget increase.");
		when(messageHelper.getMessage("NotEmpty.budgetIncreaseForm.price")).thenReturn(
						"You must specify an amount for the new budget.");
		when(messageHelper.getMessage("NotNull.reimbursementForm.additional_expenses")).thenReturn(
						"You must specify an amount for the reimbursement.");
		when(messageHelper.getMessage("Size.reimbursementForm")).thenReturn("The amount must be greater than zero");
		when(messageHelper.getMessage("NotEmpty.reimbursementForm.note")).thenReturn(
						"You must add a reason for the reimbursement.");
		when(messageHelper.getMessage("NotNull.bonusForm.bonus")).thenReturn(
						"You must specify a bonus amount greater than 0");
		when(messageHelper.getMessage("NotEmpty.bonusForm.note")).thenReturn("You must add a reason for the bonus.");

		goodResponse = new FulfillmentPayloadDTO();
		goodResponse.setSuccessful(Boolean.TRUE);
		goodResponse.setPayload(ImmutableList.of(ImmutableMap.of("id", 10001L)));

		when(fulfillmentProcessor.requestBudgetIncrease(anyString(),
																										(NegotiationDTO) anyObject())).thenReturn(goodResponse);

		when(fulfillmentProcessor.requestReimbursement(anyString(), (NegotiationDTO) anyObject())).thenReturn(goodResponse);

		when(fulfillmentProcessor.requestBonus(anyString(), (NegotiationDTO) anyObject())).thenReturn(goodResponse);
	}

	@Test(expected = com.workmarket.api.exceptions.GenericApiException.class)
	public void postBudgetIncrease_withNoNote_shouldReturnErrorResponse() throws Exception {

		final NegotiationDTO requestModel = new NegotiationDTO.Builder()
			.withFlatPrice(5000.00)
			.build();

		final ApiV2Response apiResponse = controller.postBudgetIncreaseRequest("7456373", requestModel, response);

		assertNull(apiResponse.getPagination());
		assertEquals(1, apiResponse.getResults().size());
		assertEquals("You must add a reason for the budget increase.", apiResponse.getResults().get(0));
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());
		assertEquals("Bad Request", getMetaString("message", apiResponse.getMeta()));
	}

	@Test(expected = com.workmarket.api.exceptions.GenericApiException.class)
	public void postBudgetIncrease_withNoBudget_shouldReturnErrorResponse() throws Exception {

		final NegotiationDTO requestModel = new NegotiationDTO.Builder()
			.withNote("I don't know what I want, I just know that I want")
			.build();

		final ApiV2Response apiResponse = controller.postBudgetIncreaseRequest("7456373", requestModel, response);

		assertNull(apiResponse.getPagination());
		assertEquals(1, apiResponse.getResults().size());
		assertEquals("You must specify an amount for the new budget.", apiResponse.getResults().get(0));
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		expectStatusCode(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta());
		assertEquals("Bad Request", getMetaString("message", apiResponse.getMeta()));
	}

	@Test
	public void postBudgetIncrease_withValidRequest_shouldReturnValidResponse() throws Exception {

		final NegotiationDTO requestModel = new NegotiationDTO.Builder()
			.withNote("I know what I want, and I'm getting it")
			.withFlatPrice(4000.00)
			.build();

		final ApiV2Response apiResponse = controller.postBudgetIncreaseRequest("7456373", requestModel, response);

		assertNull(apiResponse.getPagination());
		assertEquals(1, apiResponse.getResults().size());
		assertEquals(10001L, ((Map) apiResponse.getResults().get(0)).get("id"));
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());
		assertFalse(apiResponse.getMeta().containsKey("message"));
	}

	@Test(expected = com.workmarket.api.exceptions.GenericApiException.class)
	public void postReimbursement_withNoReimbursementValue_shouldReturnErrorResponse() throws Exception {

		final NegotiationDTO requestModel = new NegotiationDTO.Builder()
			.withNote("I don't know what I want, I just know that I want")
			.build();

		final ApiV2Response apiResponse = controller.postExpenseReimbursementRequest("7456373",
																																								 requestModel,
																																								 response);

		assertNull(apiResponse.getPagination());
		assertEquals(1, apiResponse.getResults().size());
		assertEquals("You must specify an amount for the reimbursement.", apiResponse.getResults().get(0));
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		expectStatusCode(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta());
		assertEquals("Bad Request", getMetaString("message", apiResponse.getMeta()));
	}

	@Test(expected = com.workmarket.api.exceptions.GenericApiException.class)
	public void postReimbursement_withInvalidReimbursementValue_shouldReturnErrorResponse() throws Exception {

		final NegotiationDTO requestModel = new NegotiationDTO.Builder()
			.withNote("I don't know what I want, I just know that I want")
			.withReimbursement(-10.0)
			.build();

		final ApiV2Response apiResponse = controller.postExpenseReimbursementRequest("7456373",
																																								 requestModel,
																																								 response);

		assertNull(apiResponse.getPagination());
		assertEquals(1, apiResponse.getResults().size());
		assertEquals("The amount must be greater than zero", apiResponse.getResults().get(0));
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		expectStatusCode(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta());
		assertEquals("Bad Request", getMetaString("message", apiResponse.getMeta()));
	}

	@Test(expected = com.workmarket.api.exceptions.GenericApiException.class)
	public void postReimbursement_withNoNote_shouldReturnErrorResponse() throws Exception {

		final NegotiationDTO requestModel = new NegotiationDTO.Builder()
			.withReimbursement(50.00)
			.build();

		final ApiV2Response apiResponse = controller.postExpenseReimbursementRequest("7456373",
																																								 requestModel,
																																								 response);

		assertNull(apiResponse.getPagination());
		assertEquals(1, apiResponse.getResults().size());
		assertEquals("You must add a reason for the reimbursement.", apiResponse.getResults().get(0));
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		expectStatusCode(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta());
		assertEquals("Bad Request", getMetaString("message", apiResponse.getMeta()));
	}

	@Test
	public void postReimbursement_withValidRequest_shouldReturnValidResponse() throws Exception {

		final NegotiationDTO requestModel = new NegotiationDTO.Builder()
			.withNote("Bought this")
			.withReimbursement(400.00)
			.build();

		final ApiV2Response apiResponse = controller.postExpenseReimbursementRequest("7456373",
																																								 requestModel,
																																								 response);

		assertNull(apiResponse.getPagination());
		assertEquals(1, apiResponse.getResults().size());
		assertEquals(10001L, ((Map) apiResponse.getResults().get(0)).get("id"));
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());
		assertFalse(apiResponse.getMeta().containsKey("message"));
	}

	@Test(expected = com.workmarket.api.exceptions.GenericApiException.class)
	public void postBonus_withNoNote_shouldReturnErrorResponse() throws Exception {

		final NegotiationDTO requestModel = new NegotiationDTO.Builder()
			.withBonus(50.00)
			.build();

		final ApiV2Response apiResponse = controller.postBonusRequest("7456373", requestModel, response);

		assertNull(apiResponse.getPagination());
		assertEquals(1, apiResponse.getResults().size());
		assertEquals("You must add a reason for the bonus.", apiResponse.getResults().get(0));
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		expectStatusCode(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta());
		assertEquals("Bad Request", getMetaString("message", apiResponse.getMeta()));
	}

	@Test(expected = com.workmarket.api.exceptions.GenericApiException.class)
	public void postBonus_withNoBonusValue_shouldReturnErrorResponse() throws Exception {

		final NegotiationDTO requestModel = new NegotiationDTO.Builder()
			.withNote("I want a bonus")
			.build();

		final ApiV2Response apiResponse = controller.postBonusRequest("7456373", requestModel, response);

		assertNull(apiResponse.getPagination());
		assertEquals(1, apiResponse.getResults().size());
		assertEquals("You must specify a bonus amount greater than 0", apiResponse.getResults().get(0));
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		expectStatusCode(HttpStatus.BAD_REQUEST.value(), apiResponse.getMeta());
		assertEquals("Bad Request", getMetaString("message", apiResponse.getMeta()));
	}

	@Test
	public void postBonus_withValidRequest_shouldReturnValidResponse() throws Exception {

		final NegotiationDTO requestModel = new NegotiationDTO.Builder()
			.withNote("Done good")
			.withBonus(200.00)
			.build();

		final ApiV2Response apiResponse = controller.postBonusRequest("7456373", requestModel, response);

		assertNull(apiResponse.getPagination());
		assertEquals(1, apiResponse.getResults().size());
		assertEquals(10001L, ((Map) apiResponse.getResults().get(0)).get("id"));
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());
		assertFalse(apiResponse.getMeta().containsKey("message"));
	}

	@Test(expected = com.workmarket.api.exceptions.BadRequestApiException.class)
	public void postBudgetIncrease_whichFails_shouldReturnErrorResponse() throws Exception {

		final NegotiationDTO requestModel = new NegotiationDTO.Builder()
			.withNote("Done good")
			.withFlatPrice(200.00)
			.build();

		final FulfillmentPayloadDTO badResponse = new FulfillmentPayloadDTO();

		badResponse.setSuccessful(Boolean.FALSE);
		badResponse.setPayload(ImmutableList.of("Cannot submit at this time"));

		when(fulfillmentProcessor.requestBudgetIncrease(anyString(), eq(requestModel))).thenReturn(badResponse);

		final ApiV2Response apiResponse = controller.postBudgetIncreaseRequest("7456373", requestModel, response);

		assertNull(apiResponse.getPagination());
		assertEquals(1, apiResponse.getResults().size());
		assertEquals("Cannot submit at this time", apiResponse.getResults().get(0));
		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
		expectStatusCode(HttpStatus.FORBIDDEN.value(), apiResponse.getMeta());
		assertEquals("Could not submit request", getMetaString("message", apiResponse.getMeta()));
	}
}
