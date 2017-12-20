package com.workmarket.api.v2.employer.paymentterms.controllers;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.service.business.CompanyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Api(value = "/v2/employer/payment_terms", tags = "Profile")
@Controller("employerPaymentTermsController")
@RequestMapping(value = {"/v2/employer/payment_terms", "/employer/v2/payment_terms"})
public class PaymentTermsController extends ApiBaseController {
	@Autowired private CompanyService companyService;

	@ApiOperation(value = "List payment terms options")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response getPaymentTerms(@RequestParam String[] fields) throws Exception {
		// TODO API - projection
		ImmutableList<Map> projectedPaymentTermsDurations = companyService.getProjectedPaymentTermsDurations(fields);
		return ApiV2Response.valueWithResults(projectedPaymentTermsDurations);
	}
}
