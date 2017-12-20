package com.workmarket.api.v2.employer.uploads.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.uploads.exceptions.ConflictException;
import com.workmarket.api.v2.employer.uploads.models.AssignmentsDTO;
import com.workmarket.api.v2.employer.uploads.services.CoordinationService;
import com.workmarket.api.v2.employer.uploads.services.CsvAssignmentsService;
import com.workmarket.service.exception.HostServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = "Uploads")
@Controller("uploadsAssignmentsController")
@RequestMapping(value = {"/v2/employer/uploads", "/employer/v2/uploads"})
public class AssignmentsController extends BaseUploadsController {

	@Autowired private CsvAssignmentsService csvAssignmentsService;
	@Autowired private CoordinationService coordinationService;

	@ApiOperation(value = "Create assignments from CSV")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/assignments"},
		method = POST
	)
	@ResponseStatus(ACCEPTED)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public void postAssignmentsFromCSV(
		@ApiParam(name = "uuid") @PathVariable("uuid") String uuid
	) throws ConflictException {
		coordinationService.createAssignments(uuid);
	}

	@ApiOperation(value = "Get assignments from CSV")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/assignments"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<AssignmentsDTO> getAssignmentsFromCSV(
		@ApiParam(name = "uuid") @PathVariable("uuid") String uuid,
		@RequestParam(value = "page", defaultValue = "1", required = false) long page,
		@RequestParam(value = "size", defaultValue = "10", required = false) long size
	) throws IOException, HostServiceException {
		AssignmentsDTO assignmentsDTO = csvAssignmentsService.get(uuid, page, size);
		return ApiV2Response.valueWithResult(assignmentsDTO);
	}
}
