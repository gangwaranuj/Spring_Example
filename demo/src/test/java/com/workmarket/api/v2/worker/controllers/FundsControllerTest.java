package com.workmarket.api.v2.worker.controllers;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.exceptions.GenericApiException;
import com.workmarket.api.exceptions.NotFoundApiException;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.fulfillment.FundsFulfillmentProcessor;
import com.workmarket.api.v2.worker.model.WithdrawalRequestDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.service.infra.business.FastFundsService;
import com.workmarket.service.infra.business.wrapper.FastFundInvoiceResponse;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.FastFundsValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class FundsControllerTest extends BaseApiControllerTest {

	private MockHttpServletResponse response;

	@Mock private FundsFulfillmentProcessor fulfillmentProcessor;
	@Mock private FastFundsValidator fastFundsValidator;
	@Mock private FastFundsService fastFundsService;
	@InjectMocks private FundsController controller = new FundsController();

	@Before
	public void setup() {
		response = new MockHttpServletResponse();
	}

	@Test
	public void fastFundAssignment_withValidRequest_returnOK() {
		when(fastFundsService.fastFundInvoiceForWork(any(String.class))).thenReturn(FastFundInvoiceResponse.success());

		final ApiV2Response response = controller.postFastFundsAssignment("some-work-number", new MessageBundle());

		ApiJSONPayloadMap responseMeta = response.getMeta();
		expectApiV3ResponseMetaSupport(responseMeta);
		expectStatusCode(HttpStatus.OK.value(), responseMeta);
	}

	@Test
	public void fastFundAssignment_withRequestValidationFailure_throwValidationException() {
		final MessageBundle bundle = mock(MessageBundle.class);
		when(bundle.hasErrors()).thenReturn(true);

		try {
			controller.postFastFundsAssignment("some-work-number", bundle);
		}
		catch (final GenericApiException e) {
			assertEquals("Validation error.", e.getMessage());
			return;
		}
		fail();
	}

	@Test
	public void fastFundAssignment_withMissingInvoice_throwNotFound() {
		when(fastFundsService.fastFundInvoiceForWork(any(String.class))).thenReturn(FastFundInvoiceResponse.invoiceNotFound());

		try {
			controller.postFastFundsAssignment("some-work-number", new MessageBundle());
		}
		catch (final NotFoundApiException e) {
			assertEquals("Invoice not found.", e.getMessage());
			return;
		}
		fail();
	}

	@Test
	public void fastFundAssignment_withValidRequest_handleServiceFailure() {
		when(fastFundsService.fastFundInvoiceForWork(any(String.class))).thenReturn(FastFundInvoiceResponse.fail());

		try {
			controller.postFastFundsAssignment("some-work-number", new MessageBundle());
		}
		catch (final GenericApiException e) {
			assertEquals("Error fast funding assignment.", e.getMessage());
			return;
		}
		fail();
	}

	@Test
	public void getFunds_withValidRequest_shouldReturnValidResponse() {

		final FulfillmentPayloadDTO processorResult = createNonWithdrawableFundsMockResult();

		when(fulfillmentProcessor.getFunds((ExtendedUserDetails) anyObject())).thenReturn(processorResult);

		final ApiV2Response response = controller.getFunds();

		verify(fulfillmentProcessor, times(1)).getFunds((ExtendedUserDetails) anyObject());

		expectApiResponseMetaSupport(response.getMeta());
		expectStatusCode(HttpStatus.OK.value(), response.getMeta());
		assertEquals("Funds cannot be withdrawn until we have verified your tax information",
								 getMetaString("message", response.getMeta()));

		assertEquals(1, response.getResults().size());
		Map resultPayload = (Map) response.getResults().get(0);
		assertEquals(6532.75D, resultPayload.get("availableToWithdraw"));
		assertEquals(20876.53D, resultPayload.get("totalEarnings"));
		assertEquals(782.32D, resultPayload.get("pastDueReceivables"));
		assertEquals(2347.94D, resultPayload.get("currentReceivables"));
		assertEquals(Boolean.FALSE, resultPayload.get("canWithdrawFunds"));

		assertNull(response.getPagination());
	}

	@Test
	public void withdrawFunds_withValidRequest_shouldReturnValidResponse() throws Exception {

		final FulfillmentPayloadDTO withdrawalResult = createWithdrawFundsMockSuccessResult();

		final WithdrawalRequestDTO requestModel = new WithdrawalRequestDTO.Builder()
			.withAccount(8274682L)
			.withAmount(new BigDecimal(45.00D))
			.build();

		when(fulfillmentProcessor.withdrawFunds((ExtendedUserDetails) anyObject(), eq(requestModel))).thenReturn(
						withdrawalResult);

		final ApiV2Response apiResponse = controller.postWithdrawFunds(requestModel, response);

		verify(fulfillmentProcessor, times(1)).withdrawFunds((ExtendedUserDetails) anyObject(), eq(requestModel));

		assertNull(apiResponse.getPagination());
		assertEquals(1, apiResponse.getResults().size());
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());
		assertEquals("Withdrawal Successfully processed.", apiResponse.getResults().get(0));
	}

	@Test
	public void withdrawFunds_withBadRequest_shouldReturnErrorResponse() throws Exception {

		final FulfillmentPayloadDTO withdrawalResult = createWithdrawFundsMockFailureResult();

		final WithdrawalRequestDTO requestModel = new WithdrawalRequestDTO.Builder()
			.withAccount(8274682L)
			.withAmount(new BigDecimal(45.00D))
			.build();

		when(fulfillmentProcessor.withdrawFunds((ExtendedUserDetails) anyObject(), eq(requestModel))).thenReturn(
						withdrawalResult);

		try {

			final ApiV2Response apiResponse = controller.postWithdrawFunds(requestModel, response);
			assertEquals("Expected FundsController.withdraw to throw an exception", true, false);
		}
		catch(GenericApiException ex) {
			assertEquals("Unable to execute withdrawal", ex.getMessage());
			assertEquals("User does not have adequate funds.", ex.getErrors().get(0));
			assertEquals("Bank account not confirmed.", ex.getErrors().get(1));
		}

	}

	private FulfillmentPayloadDTO createNonWithdrawableFundsMockResult() {

		final Map<String, Object> resultsPayload = new HashMap<>();

		resultsPayload.put("availableToWithdraw", 6532.75D);
		resultsPayload.put("totalEarnings", 20876.53D);
		resultsPayload.put("pastDueReceivables", 782.32D);
		resultsPayload.put("currentReceivables", 2347.94D);
		resultsPayload.put("canWithdrawFunds", Boolean.FALSE);

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
		response.setPayload(ImmutableList.of((Object) resultsPayload));
		response.setMessage("Funds cannot be withdrawn until we have verified your tax information");

		return response;
	}

	private FulfillmentPayloadDTO createWithdrawFundsMockSuccessResult() {

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		response.addResponseResult("Withdrawal Successfully processed.");
		response.setSuccessful(Boolean.TRUE);

		return response;
	}

	private FulfillmentPayloadDTO createWithdrawFundsMockFailureResult() {

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		response.addResponseResult("User does not have adequate funds.");
		response.addResponseResult("Bank account not confirmed.");
		response.setSuccessful(Boolean.FALSE);

		return response;
	}
}
