package com.workmarket.api.v2.employer.assignments.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.assignments.models.AssignmentSearchRequestDTO;
import com.workmarket.api.v2.employer.assignments.models.AssignmentSearchResponseDTO;
import com.workmarket.api.v2.employer.assignments.services.AssignmentCustomFieldGroupsService;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.search.model.WorkSearchResponse;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.web.forms.work.WorkDashboardForm;
import com.workmarket.web.helpers.WorkDashboardHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@Api(tags = {"Assignments"})
@RequestMapping("/v2/assignments")
public class AssignmentSearchController extends ApiBaseController {
	@Autowired private WorkDashboardHelper workDashboardHelper;
	@Autowired private AssignmentCustomFieldGroupsService assignmentCustomFieldGroupsService;

	private static final Logger logger = LoggerFactory.getLogger(AssignmentSearchController.class);

	/**
	 * This endpoint does not use ApiV2Response, please use {@link #searchAssignments(AssignmentSearchRequestDTO)}
	 *
	 * @param model Spring ui Model
	 * @param params Assignment search request parameters
	 */
	@Deprecated
	@RequestMapping(
		method = GET,
		value = "/list",
		produces = APPLICATION_JSON_VALUE
	)
	@ApiOperation(value = "List assignments (deprecated, please use /v2/assignments/search)")
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK', 'PERMISSION_REPORTCOWORK', 'PERMISSION_REPORTMYWORK')")
	public void listAssignments(final Model model, final AssignmentSearchRequestDTO params) throws WorkActionException {
		final Map<String, Object> response = workDashboardHelper.getDashboard(model, toWorkDashboardForm(params), getCurrentUser());
		final Object data = response.get("data");

		if (params.isIncludeCustomFields() && data != null) {
			includeCustomFields((List<Map<String, Object>>) data);
		}
	}

	@ResponseBody
	@RequestMapping(
		method = GET,
		value = "/search",
		produces = APPLICATION_JSON_VALUE
	)
	@ApiOperation(value = "Search assignments")
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK', 'PERMISSION_REPORTCOWORK', 'PERMISSION_REPORTMYWORK')")
	public ApiV2Response<AssignmentSearchResponseDTO> searchAssignments(final AssignmentSearchRequestDTO params) throws WorkActionException {
		final WorkDashboardForm form = toWorkDashboardForm(params);
		final WorkSearchResponse searchResponse = workDashboardHelper.searchWork(form, getCurrentUser());
		final AssignmentSearchResponseDTO.Builder builder = new AssignmentSearchResponseDTO.Builder(searchResponse);

		if (params.isIncludeCustomFields()) {
			includeCustomFields(builder.getData());
		}

		return ApiV2Response.valueWithResult(builder.build());
	}

	private void includeCustomFields(final List<Map<String, Object>> assignments) throws WorkActionException {
		for (final Map<String, Object> assignment : assignments) {
			try {
				final String assignmentId = String.valueOf(assignment.get("id"));
				final Set<CustomFieldGroupDTO> customFields = assignmentCustomFieldGroupsService.get(assignmentId);

				assignment.put("custom_field_groups", customFields);
			} catch (Exception e) {
				// In case solr and db are out of sync
				logger.error("Fail to load custom fields", e);
				assignment.put("custom_field_groups", Collections.EMPTY_SET);
			}
		}
	}

	private WorkDashboardForm toWorkDashboardForm(final AssignmentSearchRequestDTO params) {
		final WorkDashboardForm form = new WorkDashboardForm();

		form.setStart(params.getStart());
		form.setPageSize(params.getPageSize());
		form.setDir(params.getDir());
		form.setSort(params.getSort());
		form.setType(params.getType());
		form.setStatus(params.getStatus());
		form.setSub_status(params.getSubStatus());
		form.setClient_companies(params.getClientCompanies());
		form.setProjects(params.getProjects());
		form.setInternal_owners(params.getInternalOwners());
		form.setAssigned_resources(params.getAssignedResources());
		form.setAssigned_vendors(params.getAssignedVendors());
		form.setBundles(params.getBundles());
		form.setWorkMilestone(params.getWorkMilestone());
		form.setWorkDateRange(params.getWorkDateRange());
		form.setAssigned_to_me(params.getAssignedToMe());
		form.setDispatched_by_me(params.getDispatchedByMe());
		form.setInclude_time(params.isIncludeTime());
		form.setFollowing(params.getFollowing());
		form.setKeyword(params.getKeyword());
		form.setFilterless(params.isFilterless());
		form.setFast(params.isFast());
		form.setTitle(params.getTitle());
		form.setAssignedResourceName(params.getAssignedResourceName());
		form.setClientCompanyName(params.getClientCompanyName());
		form.setBuyerFullName(params.getBuyerFullName());
		form.setProjectName(params.getProjectName());
		form.setFilterPendingMultiApprovals(params.isFilterPendingMultiApprovals());
		form.setSchedule_from(params.getScheduleFrom());
		form.setSchedule_through(params.getScheduleThrough());
		form.setTime_from(params.getTimeFrom());
		form.setTime_through(params.getTimeThrough());

		return form;
	}
}
