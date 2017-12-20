package com.workmarket.api.v2.employer.uploads.controllers;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.ResourceNotFoundException;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.uploads.models.MappingsDTO;
import com.workmarket.api.v2.employer.uploads.services.UploadMappingService;
import com.workmarket.service.business.upload.transactional.WorkUploadMappingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Api(tags = "Uploads")
@Controller("employerUploadsMappingsController")
@RequestMapping(value = {"/v2/employer/mappings", "/employer/v2/mappings"})
public class MappingsController extends ApiBaseController {
	@Autowired WorkUploadMappingService mappingService;
	@Autowired UploadMappingService uploadMappingService;

	@ApiOperation(value = "Get Upload Mappings")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response getUploadMappings(@RequestParam String[] fields) throws Exception {
		// TODO API - projection
		ImmutableList<Map> projectedMappings = mappingService.getProjectedMappings(fields);
		return ApiV2Response.valueWithResults(projectedMappings);
	}

	@ApiOperation(value = "Create mappings")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<MappingsDTO> postCreateUploadMappings(@RequestBody MappingsDTO builder) throws Exception {
		MappingsDTO mappingsDTO = uploadMappingService.create(builder);
		return ApiV2Response.valueWithResult(mappingsDTO);
	}

	@ApiOperation(value = "Get mapping")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = "/{id}",
		method = GET,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<MappingsDTO> getUploadMapping(
		@ApiParam(name = "id", required = true) @PathVariable Long id) throws Exception {
		MappingsDTO mappingsDTO = uploadMappingService.get(id);
		if(mappingsDTO == null) {
			throw new ResourceNotFoundException("No mapping found with id: " + id);
		}
		return ApiV2Response.valueWithResult(mappingsDTO);
	}

	@ApiOperation(value = "Update mappings")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = "/{id}",
		method = {PUT},
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<MappingsDTO> putUpdateUploadMappings(
		@ApiParam(name = "id", required = true) @PathVariable Long id,
		@RequestBody MappingsDTO builder
	) throws Exception {
		MappingsDTO mappingsDTO = uploadMappingService.update(id, builder);
		return ApiV2Response.valueWithResult(mappingsDTO);
	}

	@ApiOperation(value = "Create mappings")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
			value = "/{id}",
			method = {POST},
			produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response<MappingsDTO> postCreateUploadMappings(
			@ApiParam(name = "id", required = true) @PathVariable Long id,
			@RequestBody MappingsDTO builder
	) throws Exception {
		MappingsDTO mappingsDTO = uploadMappingService.update(id, builder);
		return ApiV2Response.valueWithResult(mappingsDTO);
	}

	@ApiOperation(value = "Delete mapping")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = "/{id}",
		method = DELETE,
		produces = APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response deleteUploadMapping(
		@ApiParam(name = "id", required = true) @PathVariable Long id) throws Exception {
		uploadMappingService.delete(id);
		return ApiV2Response.OK();
	}
}
