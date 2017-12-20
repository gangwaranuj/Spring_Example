package com.workmarket.api.v2.employer.paymentconfiguration.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.paymentconfiguration.models.PaymentConfigurationDTO;
import com.workmarket.api.v2.employer.paymentconfiguration.services.PaymentConfigurationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Api(value = "/v2/employer/payment_configuration", tags = "Profile")
@Controller("employerPaymentConfigurationController")
@RequestMapping(value = {"/v2/employer/payment_configuration", "/employer/v2/payment_configuration"})
public class PaymentConfigurationController extends ApiBaseController {
	@Autowired private PaymentConfigurationService paymentConfigurationService;

	@ApiOperation(value = "Get payment configuration")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<PaymentConfigurationDTO> getPaymentConfiguration() throws Exception {
		PaymentConfigurationDTO paymentConfigurationDTO = paymentConfigurationService.get();
		return ApiV2Response.valueWithResult(paymentConfigurationDTO);
	}
}
