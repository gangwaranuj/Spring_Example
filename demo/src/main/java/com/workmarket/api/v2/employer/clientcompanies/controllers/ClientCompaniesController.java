package com.workmarket.api.v2.employer.clientcompanies.controllers;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.service.business.CRMService;

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

@Api(value = "/v2/employer/client_companies", tags = "Clients")
@Controller("employerClientCompaniesController")
@RequestMapping(value = {"/v2/employer/client_companies", "/employer/v2/client_companies"})
public class ClientCompaniesController extends ApiBaseController {
	@Autowired private CRMService crmService;
	@Autowired private ProjectService projectService;

	@ApiOperation(value = "Get client companies with the given fields")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response<Map> getClientCompanies(@RequestParam String[] fields) throws Exception {
		ImmutableList<Map> projectedClientCompanies = crmService.getProjectedClientCompanies(fields);
		return ApiV2Response.valueWithResults(projectedClientCompanies);
	}

	@ApiOperation(value = "Get projects for the given client company")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(method = GET, value = "/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getProjectsByClient(@ApiParam(name = "id", required = true) @PathVariable long id,
																					 @RequestParam String[] fields) throws Exception {
		// TODO API - projection
		ImmutableList<Map> projectedProjects = projectService.getProjectedProjectsByClientCompany(id, fields);
		return ApiV2Response.valueWithResults(projectedProjects);
	}
}
