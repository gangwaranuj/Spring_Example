package com.workmarket.api.v2.employer.assignments.controllers;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;
import com.workmarket.api.v2.employer.assignments.services.AssignmentTemplateService;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Api(tags = {"Assignments"})
@Controller
@RequestMapping("/employer/v2/assignments/templates")
public class AssignmentTemplatesController extends ApiBaseController {
	@Autowired
	AssignmentTemplateService assignmentTemplateService;

	@ApiOperation(value = "Get an assignment template for a given list of fields")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})@RequestMapping(
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<Map> getAssignmentTemplatesByFields(@ApiParam(name = "fields") @RequestParam(required = false) String[] fields) throws Exception {
		// TODO API - projection
		ImmutableList<Map> projectedTemplates;
		if (isNotEmpty(fields)) {
			projectedTemplates = assignmentTemplateService.getProjectedTemplates(fields);
		} else {
			projectedTemplates = assignmentTemplateService.getTemplates();
		}

		return ApiV2Response.valueWithResults(projectedTemplates);
	}

	@ApiOperation(value = "Get an assignment template")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = "/{id}",
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<TemplateDTO> getAssignmentTemplate(@ApiParam(name = "id") @PathVariable String id) throws WorkActionException {
		TemplateDTO templateDTO = assignmentTemplateService.get(id);
		return ApiV2Response.valueWithResult(templateDTO);
	}

	@ApiOperation(value = "Create a new assignment template")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		method = POST,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<TemplateDTO> postCreateAssignmentTemplate(
		@RequestBody TemplateDTO builder
	) throws ValidationException {
		TemplateDTO templateDTO = assignmentTemplateService.create(builder);
		return ApiV2Response.valueWithResult(templateDTO);
	}

	@ApiOperation(value = "Update an assignment template")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{id}"},
		method = {PUT},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<TemplateDTO> putUpdateAssignmentTemplate(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody TemplateDTO builder
	) throws ValidationException, WorkActionException {
		TemplateDTO templateDTO = assignmentTemplateService.update(id, builder);
		return ApiV2Response.valueWithResult(templateDTO);
	}

	@ApiOperation(value = "Create an assignment template")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{id}"},
		method = {POST},
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<TemplateDTO> postCreateAssignmentTemplateById(
		@ApiParam(name = "id", required = true) @PathVariable String id,
		@RequestBody TemplateDTO builder
	) throws ValidationException, WorkActionException {
		TemplateDTO templateDTO = assignmentTemplateService.update(id, builder);
		return ApiV2Response.valueWithResult(templateDTO);
	}
}
