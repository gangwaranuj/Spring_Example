package com.workmarket.api.v2.employer.settings.controllers;


import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentDTO;
import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentResponseDTO;
import com.workmarket.api.v2.employer.settings.services.CreditCardPaymentService;
import com.workmarket.service.exception.account.CreditCardErrorException;
import com.workmarket.thrift.core.ValidationException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(value = "/v2/employer/settings/funds/credit_card", tags = "Profile")
@Controller("employerCreditCardController")
@RequestMapping(value = {"/v2/employer/settings/funds/credit_card", "/employer/v2/settings/funds/credit_card"})
public class CreditCardPaymentController extends ApiBaseController {

	@Autowired
	CreditCardPaymentService creditCardPaymentService;

	@ApiOperation(value = "Add funds via credit card (employer)")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	@RequestMapping(
		method = POST,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ApiV2Response<CreditCardPaymentResponseDTO> postAddFundsCreditCard(@RequestBody CreditCardPaymentDTO builder) throws ValidationException, BeansException, CreditCardErrorException {
		CreditCardPaymentResponseDTO paymentResponseDTO = creditCardPaymentService.addFunds(builder);
		return ApiV2Response.valueWithResult(paymentResponseDTO);
	}
}
