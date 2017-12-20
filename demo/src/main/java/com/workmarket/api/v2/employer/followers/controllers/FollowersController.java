package com.workmarket.api.v2.employer.followers.controllers;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.service.business.ProfileService;

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

@Api(tags = {"Followers"})
@Controller("employerFollowersController")
@RequestMapping(value = {"/v2/employer/followers", "/employer/v2/followers"})
public class FollowersController extends ApiBaseController {

	@Autowired private ProfileService profileService;

	@ApiOperation(value = "Get followers of your company")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response getFollowers(@RequestParam String[] fields) throws Exception {
		// TODO API - projection
		ImmutableList<Map> projectedFollowers = profileService.getProjectedFollowers(fields);
		return ApiV2Response.valueWithResults(projectedFollowers);
	}
}
