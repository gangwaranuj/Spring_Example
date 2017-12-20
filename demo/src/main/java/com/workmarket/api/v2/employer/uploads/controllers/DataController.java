package com.workmarket.api.v2.employer.uploads.controllers;

import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.uploads.exceptions.ConflictException;
import com.workmarket.api.v2.employer.uploads.models.DataDTO;
import com.workmarket.api.v2.employer.uploads.models.ErrorsDTO;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.services.CoordinationService;
import com.workmarket.api.v2.employer.uploads.services.CsvDataService;
import com.workmarket.service.exception.HostServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;

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
@Controller("uploadsDataController")
@RequestMapping(value = {"/v2/employer/uploads", "/employer/v2/uploads"})
public class DataController extends BaseUploadsController {

	@Autowired private CsvDataService csvDataService;
	@Autowired private CoordinationService coordinationService;

	@ApiOperation(value = "Create data")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/data"},
		method = POST
	)
	@ResponseStatus(ACCEPTED)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public void postCreateCSVData(
		@ApiParam(name = "uuid", required = true) @PathVariable("uuid") String uuid,
		@RequestBody(required = false) List<MappingDTO> builders) throws ConflictException {
		coordinationService.createData(uuid, collectList(builders));
	}

	@ApiOperation(value = "Get data")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/data"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<DataDTO> getCSVData(
		@ApiParam(name = "uuid", required = true) @PathVariable("uuid") String uuid,
		@RequestParam(value = "page", defaultValue = "1", required = false) long page,
		@RequestParam(value = "size", defaultValue = "10", required = false) long size
	) throws IOException, HostServiceException {
		DataDTO dataDTO = csvDataService.get(uuid, page, size);
		return ApiV2Response.valueWithResult(dataDTO);
	}

	@ApiOperation(value = "Get parsing errors")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/parsing_errors"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ErrorsDTO> getCSVParsingErrors(
		@ApiParam(name = "uuid", required = true) @PathVariable("uuid") String uuid,
		@RequestParam(value = "page", defaultValue = "1", required = false) long page,
		@RequestParam(value = "size", defaultValue = "10", required = false) long size
	) throws IOException, HostServiceException {
		ErrorsDTO errorsDTO = csvDataService.getParsingErrors(uuid, page, size);
		return ApiV2Response.valueWithResult(errorsDTO);
	}

}
