package com.workmarket.api.v2.employer.uploads.controllers;

import com.workmarket.api.ApiRedirectResponse;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.uploads.exceptions.ConflictException;
import com.workmarket.api.v2.employer.uploads.models.CsvDTO;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.models.StatusDTO;
import com.workmarket.api.v2.employer.uploads.services.CoordinationService;
import com.workmarket.api.v2.employer.uploads.services.CsvErrorsService;
import com.workmarket.api.v2.employer.uploads.services.CsvHeaderService;
import com.workmarket.api.v2.employer.uploads.services.CsvLabelsService;
import com.workmarket.api.v2.employer.uploads.services.CsvStorageService;
import com.workmarket.service.exception.HostServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = "Uploads")
@Controller("uploadsController")
@RequestMapping(value = {"/v2/employer/uploads","/employer/v2/uploads"})
public class UploadsController extends BaseUploadsController {
	private static final Logger logger = LoggerFactory.getLogger(UploadsController.class);

	@Autowired private CoordinationService coordinationService;
	@Autowired private CsvStorageService csvStorageService;
	@Autowired private CsvHeaderService csvHeaderService;
	@Autowired private CsvErrorsService csvErrorsService;
	@Autowired private CsvLabelsService labelService;

	@ApiOperation(value = "Save new CSV")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/", ""},
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<CsvDTO> postNewCSV(@RequestPart("file") MultipartFile file) throws IOException, HostServiceException {
		CsvDTO csvDTO = csvStorageService.save(file);
		return ApiV2Response.valueWithResult(csvDTO);
	}


	@ApiOperation(value = "Get CSV info")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<CsvDTO> getCSV(
		@ApiParam(name = "uuid") @PathVariable("uuid") String uuid) throws IOException, HostServiceException {
		CsvDTO csvDTO = csvStorageService.get(uuid);
		return ApiV2Response.valueWithResult(csvDTO);
	}

	@ApiOperation(value = "Delete CSV info")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}"},
		method = DELETE
	)
	@ResponseStatus(NO_CONTENT)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public void deleteCSV(
		@ApiParam(name = "uuid") @PathVariable("uuid") String uuid) {
		coordinationService.reset(uuid);
	}

	@ApiOperation(value = "Download CSV")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/file"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiRedirectResponse getCSVFile(
		@ApiParam(name = "uuid") @PathVariable("uuid") String uuid)
		throws IOException, HostServiceException {
		return new ApiRedirectResponse(String.format("%s", csvStorageService.download(uuid)));
	}

	@ApiOperation(value = "Get CSV headers")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/headers"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	@ResponseBody
	public ApiV2Response<MappingDTO> getCSVHeaders(
		@ApiParam(name = "uuid", required = true) @PathVariable("uuid") String uuid)
		throws IOException, HostServiceException {
		return ApiV2Response.valueWithResults(csvHeaderService.get(uuid));
	}

	@ApiOperation(value = "Get error file")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/error_file"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiRedirectResponse getCSVErrorFile(
		@ApiParam(name = "uuid", required = true) @PathVariable("uuid") String uuid,
		@RequestParam(required = false, defaultValue = "false") boolean includeValid
	) throws IOException, HostServiceException {
		return new ApiRedirectResponse(String.format("%s", csvErrorsService.generate(uuid, includeValid)));
	}

	@ApiOperation(value = "Set label")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/labels"},
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseStatus(ACCEPTED)
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public void postCSVLabel(
		@ApiParam(name = "uuid", required = true) @PathVariable("uuid") String uuid)
		throws ConflictException {
		coordinationService.labelAssignments(uuid);
	}

	@ApiOperation(value = "Get status")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/status"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<StatusDTO> getCSVStatus(
		@ApiParam(name = "uuid", required = true) @PathVariable("uuid") String uuid) {
		StatusDTO status = coordinationService.getStatus(uuid);
		logger.debug("Status for upload[" + uuid + "]: " + status);
		return ApiV2Response.valueWithResult(status);
	}
}
