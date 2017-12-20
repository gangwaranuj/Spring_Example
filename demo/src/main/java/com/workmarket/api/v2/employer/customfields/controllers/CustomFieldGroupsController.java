package com.workmarket.api.v2.employer.customfields.controllers;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.service.business.CustomFieldService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Api(value = "/v2/employer/custom_field_groups", tags = "Custom Fields")
@Controller("employerCustomFieldGroupsController")
@RequestMapping(value = {"/v2/employer/custom_field_groups", "/employer/v2/custom_field_groups"})
public class CustomFieldGroupsController extends ApiBaseController {
	@Autowired private CustomFieldService customFieldService;

	@ApiOperation(value = "Get a list of custom field groups and suppress all but the given fields")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getCustomFieldGroups(@RequestParam String[] fields) throws Exception {
		// TODO API - projection
		ImmutableList<Map> projectedCustomFieldGroups = customFieldService.getProjectedCustomFieldGroups(fields);
		return ApiV2Response.valueWithResults(projectedCustomFieldGroups);
	}

	@ApiOperation(value = "BETA Get custom fields by group", hidden = true)
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/{id}/custom_fields", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getCustomFieldsByGroup(
		@ApiParam(name = "id", required = true) @PathVariable long id, @RequestParam String[] fields) throws Exception {
		// TODO API - projection
		ImmutableList<Map> projectedCustomFields = customFieldService.getProjectedCustomFields(id, fields);
		return ApiV2Response.valueWithResults(projectedCustomFields);
	}
}
