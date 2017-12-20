package com.workmarket.api.v2.employer.requirementsets.controllers;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.service.business.requirementsets.RequirementSetsService;

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

@Api(value = "/v2/employer/requirement_sets", tags = "Requirements")
@Controller("employerRequirementSetsController")
@RequestMapping(value = {"/v2/employer/requirement_sets", "/employer/v2/requirement_sets"}) // TODO API - underscore in path?

public class RequirementSetsController extends ApiBaseController {
	@Autowired private RequirementSetsService requirementSetsService;

	@ApiOperation(value = "List requirement sets")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response getRequirementSets(@RequestParam String[] fields) throws Exception {

		// TODO API - projection
		ImmutableList<Map> projectedRequirementSets = requirementSetsService.getProjectedRequirementSets(fields);
		return ApiV2Response.valueWithResults(projectedRequirementSets);
	}
}
