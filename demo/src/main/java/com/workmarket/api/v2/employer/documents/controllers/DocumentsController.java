package com.workmarket.api.v2.employer.documents.controllers;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.service.business.AssetManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Api(tags = "Documents")
@Controller("employerDocumentsController")
@RequestMapping(value = {"/v2/employer/documents", "/employer/v2/documents"})
public class DocumentsController extends ApiBaseController {
	@Autowired AssetManagementService assetManagementService;

	@ApiOperation(value = "Get documents")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response getDocuments(@RequestParam String[] fields) throws Exception {
		// TODO API - projection
		ImmutableList<Map> projectedDocuments = assetManagementService.getProjectedDocuments(fields);
		return ApiV2Response.valueWithResults(projectedDocuments);
	}
}
