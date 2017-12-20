package com.workmarket.api.v2.employer.clientlocations.controllers;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.service.business.CRMService;

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

@Api(value = "/v2/employer/client_locations", tags = "Clients")
@Controller("employerLocationsController")
@RequestMapping(value = {"/v2/employer/client_locations", "/employer/v2/client_locations"})
public class ClientLocationsController extends ApiBaseController {

	@Autowired private CRMService crmService;

	@ApiOperation("Get a list of client locations")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<Map> getClientLocations(@RequestParam(required = false) Long clientId,
																					@RequestParam String locationName, @RequestParam String[] fields) throws Exception {
		// TODO API - projection
		ImmutableList<Map> projectedClientLocations =  crmService.getProjectedClientLocations(clientId, locationName, fields);
		return ApiV2Response.valueWithResults(projectedClientLocations);
	}

}
