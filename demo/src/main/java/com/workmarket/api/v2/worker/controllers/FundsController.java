package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.GenericApiException;
import com.workmarket.api.exceptions.NotFoundApiException;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.fulfillment.FundsFulfillmentProcessor;
import com.workmarket.api.v2.worker.model.WithdrawalRequestDTO;
import com.workmarket.service.infra.business.FastFundsService;
import com.workmarket.service.infra.business.wrapper.FastFundInvoiceResponse;
import com.workmarket.service.infra.status.FastFundInvoiceStatus;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.FastFundsValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Funds"})
@RequestMapping("/worker/v2/funds")
@Controller(value = "workerFundsController")
public class FundsController extends ApiBaseController {

	private static final Log logger = LogFactory.getLog(FundsController.class);

	@Autowired private FundsFulfillmentProcessor fundsFulfillmentProcessor;
	@Autowired private FastFundsValidator fastFundsValidator;
	@Autowired private FastFundsService fastFundsService;

	@ApiOperation("Get all funds for the logged in user")
	@RequestMapping(method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getFunds() {

		final FulfillmentPayloadDTO results = fundsFulfillmentProcessor.getFunds(getCurrentUser());

		final ApiJSONPayloadMap metadataBuilder = new ApiJSONPayloadMap();
		if (StringUtils.isNotBlank(results.getMessage())) {
			metadataBuilder.put(METADATA_MESSAGE_KEY, results.getMessage());
		}

		return new ApiV2Response(metadataBuilder, results.getPayload(), null);
	}

	@ApiOperation("Withdraw funds")
	@RequestMapping(value = "/withdraw", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postWithdrawFunds(@Valid @RequestBody final WithdrawalRequestDTO withdrawalRequestDTO,
																final HttpServletResponse response) throws Exception {

		final FulfillmentPayloadDTO results = fundsFulfillmentProcessor.withdrawFunds(getCurrentUser(),
																																									withdrawalRequestDTO);

		final ApiJSONPayloadMap metadataBuilder = new ApiJSONPayloadMap();

		if (results.isSuccessful()) {
			response.setStatus(HttpStatus.OK.value());
			if (StringUtils.isNotBlank(results.getMessage())) {
				metadataBuilder.put(METADATA_MESSAGE_KEY, results.getMessage());
			}
		}
		else {
			throw new GenericApiException("Unable to execute withdrawal", results.getPayload());
		}

		return new ApiV2Response(metadataBuilder, results.getPayload(), null);
	}

	/**
	 * Fast fund a work assignment.
	 *
	 * @param workNumber
	 * @param messageBundle
	 * @return
	 */
	@ApiOperation("Fast Fund an assignment")
	@RequestMapping(value = "/fastFund/{workNumber}", produces = APPLICATION_JSON_VALUE, method = POST)
	@ResponseBody
	public ApiV2Response postFastFundsAssignment(@PathVariable("workNumber") final String workNumber,
																					final MessageBundle messageBundle) {
		fastFundsValidator.validate(workNumber, messageBundle);

		if (messageBundle.hasErrors()) {
			throw new GenericApiException("Validation error.", messageBundle.getAllMessages());
		}

		FastFundInvoiceResponse fastFundInvoiceResponse = fastFundsService.fastFundInvoiceForWork(workNumber);

		if (fastFundInvoiceResponse.isSuccess()) {
			return ApiV2Response.OK();
		}
		else if (FastFundInvoiceStatus.INVOICE_NOT_FOUND.equals(fastFundInvoiceResponse.getStatus())) {
			throw new NotFoundApiException("Invoice not found.");
		}

		throw new GenericApiException("Error fast funding assignment.");
	}
}
