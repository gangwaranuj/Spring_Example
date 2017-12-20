package com.workmarket.api.v2.employer.projects.controllers;

import com.google.common.collect.ImmutableList;

import com.google.common.collect.Lists;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.ProjectApiDTO;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.project.ProjectService;

import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.ProjectDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.utility.ProjectionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = "Projects")
@Controller("employerProjectsController")
public class ProjectsController extends ApiBaseController {
	@Autowired private ProjectService projectService;
	@Autowired private ProfileService profileService;

	@ApiOperation(value = "List projects")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(
		value = {"/v2/employer/projects", "/employer/v2/projects"},
		method = GET,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	public ApiV2Response getProjects(@RequestParam String[] fields) throws Exception {
		// TODO API - finish this (projection)
		ImmutableList<Map> projectedProjects = projectService.getProjectedProjects(fields);
		return ApiV2Response.valueWithResults(projectedProjects);
	}

	@ApiOperation(value = "Create a new Project")
	@RequestMapping(
			value = {"/v2/employer/projects", "/employer/v2/projects"},
			method = POST,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response<ProjectApiDTO> createProject(
			@RequestBody ProjectApiDTO project
	) throws WorkActionException, ValidationException {
		final Project savedProject = projectService.saveOrUpdateProject(project);
		return ApiV2Response.valueWithResult(new ProjectApiDTO.Builder(savedProject).build());
	}

	@ApiOperation(value = "List users that can be project owners")
	@RequestMapping(
			value = {"/v2/employer/projects/users", "/employer/v2/projects/users"},
			method = GET,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseBody
	@PreAuthorize("hasAnyRole('PERMISSION_CREATEWORK')")
	public ApiV2Response getProjectOwners(@RequestParam String[] fields) throws Exception {
		final List users = profileService.findAllUsersByCompanyId(
				getCurrentUser().getCompanyId(), Lists.newArrayList(UserStatusType.APPROVED));
		final ImmutableList projectedUsers = ImmutableList.copyOf(ProjectionUtilities.projectAsArray(fields, users));
		return ApiV2Response.valueWithResults(projectedUsers);
	}
}
