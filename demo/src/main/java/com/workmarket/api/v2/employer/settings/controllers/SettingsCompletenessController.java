package com.workmarket.api.v2.employer.settings.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.settings.models.SettingsCompletenessDTO;
import com.workmarket.api.v2.employer.settings.services.SettingsCompletenessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Api(value = "/v2/employer/settings/completeness_percentage", tags = "Profile")
@Controller("employerSettingsCompletenessController")
@RequestMapping(value = {"/v2/employer/settings/completeness_percentage", "/employer/v2/settings/completeness_percentage"})
public class SettingsCompletenessController extends ApiBaseController {

	@Autowired private SettingsCompletenessService settingsCompletenessService;

	@ApiOperation(value = "Get profile completeness")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('ACL_ADMIN')")
	public ApiV2Response<SettingsCompletenessDTO> getProfileCompleteness() {
		SettingsCompletenessDTO settingsCompletenessDTO = settingsCompletenessService.calculateCompletedPercentage();
		return ApiV2Response.valueWithResult(settingsCompletenessDTO);
	}

}
