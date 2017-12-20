package com.workmarket.api.v1;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v1.model.ApiAddProjectResponseDTO;
import com.workmarket.api.v1.model.ApiProjectDTO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.project.ProjectPagination;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.ProjectDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;

@Api(tags = "Projects")
@Controller("apiProjectsController")
@RequestMapping(value = {"/v1/employer/projects", "/api/v1/projects"})
public class ProjectsController extends ApiBaseController {
	private final static Logger logger = LoggerFactory.getLogger(ProjectsController.class);

	@Autowired private ProjectService projectService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private UserService userService;
	@Autowired private CRMService crmService;
	@Autowired private ApiResponseBuilder apiResponseBuilder;

	/**
	 * List projects.
	 * @param clientCompanyId is client company id
	 * @return ApiResponse
	 */
	@ApiOperation(value = "List projects")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/list", method=RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<List<ApiProjectDTO>> listProjectsAction(@RequestParam(value="client_id", required=false) Long clientCompanyId) {
		ApiV1Response<List<ApiProjectDTO>> apiResponse = new ApiV1Response<>();
		MessageBundle bundle = messageHelper.newBundle();

		if (clientCompanyId != null) {
			try {
				ProjectPagination pagination = new ProjectPagination();
				pagination.setReturnAllRows();

				User currentUser = userService.findUserById(authenticationService.getCurrentUser().getId());

				pagination = projectService.findAllProjectsForClientCompany(
						currentUser.getCompany().getId(), clientCompanyId, pagination
				);
				List<Project> results = pagination.getResults();
				List<ApiProjectDTO> apiProjectDtos = new LinkedList<>();

				for (Project project : results) {
					apiProjectDtos.add(new ApiProjectDTO.Builder()
						.withId(project.getId())
						.withName(project.getName())
						.build());
				}

				apiResponse.setResponse(apiProjectDtos);
			}
			catch (Exception ex) {
				logger.error("error retrieving project list for supplied client_id={}", new Object[] {clientCompanyId}, ex);
				messageHelper.addError(bundle, "api.v1.projects.list.error");
			}
		}
		else {
			messageHelper.addError(bundle, "api.v1.crm.invalid.clientId");
		}

		if (bundle.hasErrors()) {
			apiResponse.getMeta().setErrorMessages(bundle.getErrors());
		}

		return apiResponse;
	}

	@ApiOperation(value = "Create project")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/add", method=RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiAddProjectResponseDTO> addProjectAction(
		@RequestParam(value="client_id", required=false) Long clientCompanyId,
		@RequestParam(value="name", required=false) String name,
		@RequestParam(value="description", required=false) String description) {

		ApiV1Response<ApiAddProjectResponseDTO> apiResponse = new ApiV1Response<>();
		MessageBundle bundle = messageHelper.newBundle();

		if (clientCompanyId == null) {
			return ApiV1Response.wrap(ApiAddProjectResponseDTO.class, apiResponseBuilder.createErrorResponse("NotNull", "'client_id'"));
		}
		if (StringUtils.isEmpty(name)) {
			return ApiV1Response.wrap(ApiAddProjectResponseDTO.class, apiResponseBuilder.createErrorResponse("NotNull", "'name'"));
		}
		if (StringUtils.isEmpty(description)) {
			return ApiV1Response.wrap(ApiAddProjectResponseDTO.class, apiResponseBuilder.createErrorResponse("NotNull", "'description'"));
		}

		try {
			ProjectDTO dto = new ProjectDTO();
			dto.setName(name);
			dto.setDescription(description);
			dto.setClientCompanyId(clientCompanyId);

			ClientCompany clientCompany = crmService.findClientCompanyById(clientCompanyId);
			if (clientCompany == null) {
				messageHelper.addError(bundle, "api.v1.projects.add.client_not_found");
			} else {
				User currentUser = userService.findUserById(authenticationService.getCurrentUser().getId());
				Project newProject = projectService.saveOrUpdateProject(currentUser.getId(), dto);
				ApiAddProjectResponseDTO apiAddProjectResponseDTO = new ApiAddProjectResponseDTO.Builder()
				 	.withId(newProject.getId())
					.withSuccessful(true)
					.build();
				apiResponse.setResponse(apiAddProjectResponseDTO);
			}
		} catch (Exception ex) {
			logger.error("error saving project", ex);
			messageHelper.addError(bundle, "api.v1.projects.add.error");
		}

		if (bundle.hasErrors()) {
			apiResponse.setSuccessful(false);
			apiResponse.getMeta().setErrorMessages(bundle.getErrors());
			apiResponse.getMeta().setStatusCode(HttpStatus.SC_BAD_REQUEST);
		}

		return apiResponse;
	}

}