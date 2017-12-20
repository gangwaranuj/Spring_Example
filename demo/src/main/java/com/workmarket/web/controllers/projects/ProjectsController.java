package com.workmarket.web.controllers.projects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.project.ProjectPagination;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/projects")
public class ProjectsController extends BaseProjectsController {

	@Autowired private WorkReportService workReportService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private JsonSerializationService jsonSerializationService;

	@RequestMapping(method=RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("hasFeatureProjectBudget", hasFeature("projectBudget"));
		if(companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId()) != null) {
			model.addAttribute("hasProjectBudgetEnabled", companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId()).getBudgetEnabledFlag());
		}
		model.addAttribute("hasFeatureProjectPermission", hasFeature("projectPermission"));
		if(hasFeature("projectPermission")) {
			model.addAttribute("hasProjectAccess", authenticationService.hasProjectAccess(getCurrentUser().getId()));
		}

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "projects",
			"data", CollectionUtilities.newObjectMap(),
			"features", CollectionUtilities.newObjectMap(
				"hasProjectPermission", hasFeature("projectPermission"),
				"hasProjectAccess", authenticationService.hasProjectAccess(getCurrentUser().getId()),
				"hasProjectBudgetEnabled", companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId()).getBudgetEnabledFlag()
			)
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/projects/index";
	}

	@RequestMapping(value={"/list"}, method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void indexList(Model model, HttpServletRequest httpRequest) throws Exception {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.of(
			0, ProjectPagination.SORTS.NAME.toString(),
			1, ProjectPagination.SORTS.CLIENT.toString(),
			2, ProjectPagination.SORTS.OWNER.toString(),
			3, ProjectPagination.SORTS.DUE_DATE.toString()
		));

		ProjectPagination pagination = new ProjectPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		pagination = projectService.findProjectsForCompany(getCurrentUser().getCompanyId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (Project project : pagination.getResults()) {

			List<String> row = Lists.newArrayList(
					project.getName(),
					project.getClientCompany().getName(),
					project.getOwner().getFullName(),
					(project.getDueDate() != null) ? DateUtilities.format("MMM d, yyyy", project.getDueDate(), getCurrentUser().getTimeZoneId()) : "-",
					project.getRemainingBudgetForDisplay(),
					null
				);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
					"id", project.getId().toString(),
					"active", project.isActive()
			);

			response.addRow(row, meta);
		}

		if(hasFeature("projectPermission")) {
			model.addAttribute("hasProjectAccess", authenticationService.hasProjectAccess(getCurrentUser().getId()));
		}

		model.addAttribute("hasFeatureProjectBudget", hasFeature("projectBudget"));
		model.addAttribute("hasFeatureProjectPermission", hasFeature("projectPermission"));
		model.addAttribute("response", response);
	}

	@RequestMapping(value="/view/{id}", method=RequestMethod.GET)
	public String view(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		Project project = projectService.findById(id);
		Assert.notNull(project);

		int spentPercentage;
		int workInProcessPercentage;
		int workInPaidPercentage;

		if (project.getBudgetEnabledFlag()) {
			BigDecimal totalWorkValue = projectBudgetService.calcTotalWorkValue(project);
			BigDecimal totalWorkValueInProcess = projectBudgetService.calcTotalWorkValueInProcess(project);
			BigDecimal totalWorkValueInPaid = projectBudgetService.calcTotalWorkValueInPaid(project);
			int totalWorkCounts = projectBudgetService.countAssignmentsByProject(project);
			spentPercentage = (totalWorkValueInPaid.add(totalWorkValueInProcess)).divide(project.getBudget(), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.00)).intValue();
			workInProcessPercentage = totalWorkValueInProcess.divide(project.getBudget(), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.00)).intValue();
			workInPaidPercentage = totalWorkValueInPaid.divide(project.getBudget(), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.00)).intValue();
			model.addAttribute("totalWorkValue", totalWorkValue);
			model.addAttribute("totalWorkCounts", totalWorkCounts);
			model.addAttribute("totalWorkValueInProcess", totalWorkValueInProcess);
			model.addAttribute("totalWorkValueInPaid", totalWorkValueInPaid);

		} else {
			workInPaidPercentage = 0;
			workInProcessPercentage = 0;
			spentPercentage = 0;
		}

		model.addAttribute("spentPercentage", spentPercentage);
		model.addAttribute("workInProcessPercentage", workInProcessPercentage);
		model.addAttribute("workInPaidPercentage", workInPaidPercentage);


		if (project == null) {
			throw new HttpException404()
				.setMessageKey("projects.invalid")
				.setRedirectUri("redirect:/projects");
		}

		authorize(id);

		final Long currentUserId = getCurrentUser().getId();

		model.addAttribute("project", project);
		model.addAttribute("hasFeatureProjectPermission", hasFeature("projectPermission"));
		if(hasFeature("projectPermission")) {
			model.addAttribute("hasProjectAccess", authenticationService.hasProjectAccess(currentUserId));
		}

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "projects",
			"data", CollectionUtilities.newObjectMap(
				 "clientCompanyId", project.getClientCompany().getId(),
				"spentPercentage", spentPercentage,
				"workInProcessPercentage", workInProcessPercentage,
				"workInPaidPercentage", workInPaidPercentage
			),
			"features", CollectionUtilities.newObjectMap(
				"hasBudgetEnabled", project.getBudgetEnabledFlag(),
				"hasProjectPermission", hasFeature("projectPermission"),
				"hasProjectAccess", authenticationService.hasProjectAccess(currentUserId),
				"hasReserveFunds", hasFeature("reserveFunds")
			)
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/projects/view";
	}

	@RequestMapping(value="/view", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void viewList(@RequestParam("id") Long id, Model model, HttpServletRequest httpRequest) {
		authorize(id);

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.of(
				0, WorkSearchDataPagination.SORTS.TITLE.toString(),
				2, WorkSearchDataPagination.SORTS.SCHEDULE_FROM.toString(),
				3, WorkSearchDataPagination.SORTS.AMOUNT_EARNED.toString(),
				4, WorkSearchDataPagination.SORTS.STATUS.toString())
		);

		WorkSearchDataPagination pagination = new WorkSearchDataPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(WorkSearchDataPagination.SORTS.SCHEDULE_FROM.toString());
		pagination.setSortDirection(request.getSortColumnDirection());
		pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.PROJECT_ID, id);
		pagination.setShowAllCompanyAssignments(true);

		ExtendedUserDetails user = getCurrentUser();
		pagination = workReportService.generateWorkDashboardReportBuyer(user.getCompanyId(), user.getId(), pagination);

		List<SolrWorkData> results = pagination.getResults();
		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (SolrWorkData item : results) {
			String address = (StringUtils.hasText(item.getCity())) ? String.format("%s, %s, %s", item.getCity(), item.getState(), item.getPostalCode()) : "Virtual";
			BigDecimal workPrice = (WorkStatusType.SENT.equals(item.getWorkStatusTypeCode())) ? new BigDecimal(item.getSpendLimit()) : new BigDecimal(item.getBuyerTotalCost());
			WorkStatusType workStatusType = new WorkStatusType(item.getWorkStatusTypeCode());
			List<String> row = Lists.newArrayList(
					item.getTitle(),
					address,
					DateUtilities.format("MMM d, yyyy", item.getScheduleFrom(), getCurrentUser().getTimeZoneId()),
					StringUtilities.formatMoneyForDisplay(workPrice),
					workStatusType.getEmployerFormatted(item.getConfirmed(), item.getResourceConfirmationRequired()),
					(item.getAssignedResourceLastName()!= null) ? item.getAssignedResourceLastName() : "-"
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
					"id", item.getWorkNumber()
		);

			response.addRow(row, meta);
		}

		model.addAttribute("hasFeatureProjectBudget", hasFeature("projectBudget"));
		model.addAttribute("response", response);
	}

	@RequestMapping(value="/activate/{id}", method=RequestMethod.GET)
	public String activate(@PathVariable("id") Long id, RedirectAttributes flash) {
		authorize(id);
		projectService.activateProject(id);

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		messageHelper.addSuccess(bundle, "projects.activated.success");

		return "redirect:/projects";
	}

	@RequestMapping(value="/deactivate/{id}", method=RequestMethod.POST)
	public String deactivate(@PathVariable("id") Long id, RedirectAttributes flash) {
		authorize(id);
		projectService.deactivateProject(id);

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		messageHelper.addSuccess(bundle, "projects.deactivated.success");

		return "redirect:/projects";
	}

	@RequestMapping(value="/delete/{id}", method=RequestMethod.POST)
	public String delete(@PathVariable("id") Long id, RedirectAttributes flash) {

		authorize(id);
		projectService.deleteProject(id);

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		messageHelper.addSuccess(bundle, "projects.deleted.success");

		return "redirect:/projects";
	}
}
