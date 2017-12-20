package com.workmarket.api.v2.controllers;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.service.infra.business.InvariantDataService;

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

@Api(tags = {"constants"})
@Controller("apiV2ConstantsController")
@RequestMapping("/v2/constants")
public class ConstantsController extends ApiBaseController {

	@Autowired private InvariantDataService invariantDataService;

	@RequestMapping(
		value = "/country_codes",
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ApiOperation(value = "Get the list of Country codes")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@ResponseBody
	public ApiV2Response getCountryCodes(@RequestParam String[] fields) throws Exception {
		ImmutableList<Map> projectedCountryCodes = invariantDataService.getProjectedActiveCallingCodes(fields);
		return ApiV2Response.valueWithResults(projectedCountryCodes);
	}

	@RequestMapping(
		value = "/countries",
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response getAllCountries() {

		Map<String, String> countries = invariantDataService.getAllCountries();
		countries.remove("");
		return ApiV2Response.valueWithResult(countries);
	}
}
