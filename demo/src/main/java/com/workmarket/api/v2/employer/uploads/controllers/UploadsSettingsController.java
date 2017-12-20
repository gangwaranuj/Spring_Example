package com.workmarket.api.v2.employer.uploads.controllers;

import com.google.common.base.Optional;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.ResourceNotFoundException;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.uploads.models.SettingsDTO;
import com.workmarket.api.v2.employer.uploads.services.UploadSettingsService;
import com.workmarket.service.exception.HostServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = "Uploads")
@Controller
@RequestMapping(value = {"/v2/employer/uploads", "/employer/v2/uploads"})
public class UploadsSettingsController extends ApiBaseController {

	@Autowired private UploadSettingsService uploadSettingsService;

	@ApiOperation(value = "Create upload settings")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/settings"},
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<SettingsDTO> postCreateSettings(
		@ApiParam(name = "uuid") @PathVariable("uuid") String uuid,
		@RequestBody SettingsDTO builder
	) throws IOException, HostServiceException {
		SettingsDTO settingsDTO = uploadSettingsService.create(uuid, builder);
		return ApiV2Response.valueWithResult(settingsDTO);
	}

	@ApiOperation(value = "Get upload settings")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/{uuid}/settings"},
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<SettingsDTO> getSettings(
		@ApiParam(name = "uuid") @PathVariable("uuid") String uuid
	) throws IOException, HostServiceException, ResourceNotFoundException {
		Optional<SettingsDTO> settingsDTO = uploadSettingsService.get(uuid);
		if(settingsDTO.isPresent()) {
			return ApiV2Response.valueWithResult(settingsDTO.get());
		}
		else{
			throw new ResourceNotFoundException("No settings found for uuid: " + uuid);
		}
	}
}
