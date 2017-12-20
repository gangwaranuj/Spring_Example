package com.workmarket.api.v2.employer.uploads.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.uploads.exceptions.ConflictException;
import com.workmarket.api.v2.employer.uploads.models.ErrorsDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewsDTO;
import com.workmarket.api.v2.employer.uploads.services.CoordinationService;
import com.workmarket.api.v2.employer.uploads.services.CsvPreviewsService;
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
@Controller("uploadsPreviewsController")
@RequestMapping(value = {"/v2/employer/uploads", "/employer/v2/uploads"})
public class PreviewsController extends BaseUploadsController {

	@Autowired private CsvPreviewsService csvPreviewsService;
	@Autowired private CoordinationService coordinationService;

	@ApiOperation(value = "Create previews")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/previews"},
		method = POST
	)
	@ResponseStatus(ACCEPTED)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public void postCreatePreviews(
		@ApiParam(name = "uuid", required = true) @PathVariable("uuid") String uuid)
		throws ConflictException {
		coordinationService.createPreviews(uuid);
	}

	@RequestMapping(
		value = {"/{uuid}/previews"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<PreviewsDTO> getPreviews(
		@ApiParam(name = "uuid", required = true) @PathVariable("uuid") String uuid,
		@RequestParam(value = "page", defaultValue = "1", required = false) long page,
		@RequestParam(value = "size", defaultValue = "10", required = false) long size
	) throws IOException, HostServiceException {
		PreviewsDTO previewsDTO = csvPreviewsService.get(uuid, page, size);
		return ApiV2Response.valueWithResult(previewsDTO);
	}

	@RequestMapping(
		value = {"/{uuid}/validation_errors"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ErrorsDTO> getValidationErrors(
		@ApiParam(name = "uuid", required = true) @PathVariable("uuid") String uuid,
		@RequestParam(value = "page", defaultValue = "1", required = false) long page,
		@RequestParam(value = "size", defaultValue = "10", required = false) long size
	) throws IOException, HostServiceException {
		ErrorsDTO errorsDTO = csvPreviewsService.getValidationErrors(uuid, page, size);
		return ApiV2Response.valueWithResult(errorsDTO);
	}
}
