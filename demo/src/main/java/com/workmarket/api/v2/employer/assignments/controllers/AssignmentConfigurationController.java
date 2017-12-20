package com.workmarket.api.v2.employer.assignments.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.api.v2.employer.assignments.services.AssignmentSettingsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Api(tags = {"Assignments"})
@Controller("employerAssignmentConfigurationController")
@RequestMapping(value = {"/v2/employer/assignments/configuration", "/employer/v2/assignments/configuration"})
public class AssignmentConfigurationController extends ApiBaseController {

	@Autowired private AssignmentSettingsService assignmentSettingsService;

	@ApiOperation( value="Get default assignment settings")
	@RequestMapping(
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<ConfigurationDTO> getAssignmentsConfiguration() throws Exception {
		ConfigurationDTO settings = assignmentSettingsService.get();
		return ApiV2Response.valueWithResult(settings);
	}

	@ApiOperation(value="Update default assignment settings")
	@RequestMapping(
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<ConfigurationDTO> postAssignmentSettings(
		@RequestBody ConfigurationDTO builder
	) throws Exception {
		ConfigurationDTO configurationDTO = assignmentSettingsService.update(builder);
		return ApiV2Response.valueWithResult(configurationDTO);
	}

	@ApiOperation(value="Update default assignment settings")
	@RequestMapping(
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<ConfigurationDTO> putUpdateAssignmentSettings(
		@RequestBody ConfigurationDTO builder
	) throws Exception {
		ConfigurationDTO configurationDTO = assignmentSettingsService.update(builder);
		return ApiV2Response.valueWithResult(configurationDTO);
	}
}
