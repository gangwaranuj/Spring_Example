package com.workmarket.web.controllers.assignments;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.dto.UserSuggestionDTO;
import com.workmarket.dto.VendorSuggestionDTO;
import com.workmarket.redis.repositories.WorkSearchRequestRepository;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.DashboardResource;
import com.workmarket.search.response.work.WorkDateRangeFilter;
import com.workmarket.search.response.work.WorkMilestoneFilter;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.UserDashboardFilterService;
import com.workmarket.service.business.dto.BulkSelectionDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.service.infra.dto.WorkBundleSuggestionDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.work.WorkDashboardForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.WorkDashboardHelper;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/assignments")
public class WorkDashboardController extends BaseController {

	@Autowired private FormOptionsDataHelper formOptionsDataHelper;
	@Autowired private CompanyService companyService;
	@Autowired private UserDashboardFilterService userDashboardFilterService;
	@Autowired private SuggestionService suggestionService;
	@Autowired private WorkSearchRequestRepository workSearchRequestRepository;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkDashboardHelper workDashboardHelper;

	// Never link to this
	// TODO: Legacy support for /assignments/manage
	@RequestMapping(value = "/manage", method = GET)
	public String dashboardRedirect() {
		return "redirect:/assignments";
	}

	@RequestMapping(method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK', 'PERMISSION_REPORTCOWORK', 'PERMISSION_REPORTMYWORK')")
	public String dashboard(
		Model model,
		@RequestParam(value = "keyword", required = false) String keyword,
		@RequestParam(value = "calendar", required = false) String isCalendar,
		@ModelAttribute("form") WorkDashboardForm form) throws UnsupportedEncodingException {
		ExtendedUserDetails currentUser = getCurrentUser();

		form.setCurrentUser(currentUser.getId());
		form.setPageSizes(Lists.newArrayList(10, 25, 50));
		form.setKeyword(keyword);

		model.addAttribute("keyword", keyword);
		model.addAttribute("filterless", false);

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "assignments",
			"features", CollectionUtilities.newObjectMap(
				"isBuyer", currentUser.isBuyer(),
				"isWorkerCompany", isWorkerCompany(),
				"hidePricing", (currentUser.isCompanyHidesPricing() && !currentUser.hasAnyRoles("ACL_ADMIN", "ACL_MANAGER", "ACL_DISPATCHER")) || currentUser.isEmployeeWorker(),
				"isSeller", currentUser.isSeller() || currentUser.isDispatcher(),
				"defaultOwner", currentUser.getId(),
				"hasAvatar", companyService.findCompanyAvatars(currentUser.getCompanyId()) != null,
				"userTimezone", currentUser.getTimeZoneId(),
				"isNotAuthorizedForPayment", !currentUser.getApproveWorkCustomAuth() || currentUser.isMasquerading(),
				"hasProjectPermission", hasFeature("projectPermission"),
				"hasProjectAccess", authenticationService.hasProjectAccess(currentUser.getId()),
				"hasReserveFunds", hasFeature("reserveFunds")
			)
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));
		model.addAttribute("isCalendar", ("on".equals(isCalendar)) ? true : false);
		model.addAttribute("pagetitle", currentUser.isBuyer() ? "Dashboard" : "My Work");
		model.addAttribute("breadcrumbPage", currentUser.isBuyer() ? "Dashboard" : "My Work");

		addModelAttributes(model, form, currentUser);

		return "web/pages/assignments/dashboard";
	}

	@RequestMapping(
		value = "/filterless",
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK', 'PERMISSION_REPORTCOWORK', 'PERMISSION_REPORTMYWORK')")
	public String filterlessKeywordSearch(
		Model model,
		@RequestParam(value = "keyword", required = false) String keyword,
		@ModelAttribute("form") WorkDashboardForm form) throws UnsupportedEncodingException {
		ExtendedUserDetails currentUser = getCurrentUser();

		form.setCurrentUser(currentUser.getId());
		form.setPageSizes(Lists.newArrayList(10, 25, 50));
		form.setKeyword(keyword);

		model.addAttribute("filterless", true);
		model.addAttribute("keyword", keyword);
		addModelAttributes(model, form, currentUser);

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "assignments",
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/assignments/dashboard";
	}

	@RequestMapping(
		value = "/get_filters",
		method = GET)
	public @ResponseBody Map getFilters() {
		if (getCurrentUser().isSeller() && !getCurrentUser().isBuyer()) {
			return Maps.newHashMap();
		} else {
			return userDashboardFilterService.get(getCurrentUser().getId());
		}
	}

	@RequestMapping(
		value = "/set_filters",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public AjaxResponseBuilder setFilters(@RequestParam Map<String, String> filters) {
		if (getCurrentUser().isSeller() && !getCurrentUser().isBuyer()) {
			userDashboardFilterService.set(getCurrentUser().getId(), Maps.<String, String>newHashMap());
		} else {
			userDashboardFilterService.set(getCurrentUser().getId(), filters);
		}

		return new AjaxResponseBuilder().setSuccessful(true);
	}

	@RequestMapping(
		value = "/fetch_dashboard_results",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK', 'PERMISSION_REPORTCOWORK', 'PERMISSION_REPORTMYWORK')")
	public void fetchDashboardResults(Model model, @ModelAttribute("form") WorkDashboardForm form) {
		workDashboardHelper.getDashboard(model, form, getCurrentUser());
	}

	private Set<Long> getResourceIdsFrom(WorkDashboardForm form) {
		Set<DashboardResource> filter = form.getAssignedResourceFilter();
		if (CollectionUtilities.isEmpty(filter)) return Collections.emptySet();

		Set<Long> result = Sets.newHashSet();
		for (DashboardResource resource : filter) {
			result.add(resource.getResourceId());
		}
		return result;
	}

	@RequestMapping(
		value = "/assigned_resources",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getAssignedResources(@RequestParam("term") String term, Model model) throws Exception {
		List<Map<String, String>> response = Lists.newArrayList();

		// Generate Advanced Filter List For Type Ahead
		// Assigned Resources
		for (UserSuggestionDTO dto : suggestionService.suggestUser(term, getCurrentUser().getCompanyId())) {
			response.add(ImmutableMap.of(
				"id", dto.getId().toString(),
				"userNumber", dto.getUserNumber(),
				"address", dto.getCityStateCountry(),
				"name", dto.getValue()
			));
		}
		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/assigned_vendors",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getAssignedVendors(@RequestParam("term") String term, Model model) throws Exception {
		List<Map<String, String>> response = Lists.newArrayList();

		// Generate Advanced Filter List For Type Ahead
		// Assigned Resources
		for (VendorSuggestionDTO dto : suggestionService.suggestVendor(term)) {
			response.add(ImmutableMap.of(
				"id", dto.getId().toString(),
				"vendorNumber", dto.getCompanyNumber(),
				"address", dto.getCityStateCountry(),
				"name", dto.getEffectiveName()
			));
		}
		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/bundles_suggestion",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getAvailableBundles(@RequestParam("term") String term, Model model) throws Exception {
		List<Map<String, String>> response = Lists.newArrayList();

		for (WorkBundleSuggestionDTO dto : suggestionService.suggestWorkBundle(term, getCurrentUser().getId())) {
			response.add(ImmutableMap.of(
				"id", dto.getId().toString(),
				"internalOwner", dto.getInternalOwner(),
				"name", dto.getTitle()
			));
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/projects_suggestion",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getAvailableProjects(@RequestParam("term") String term, Model model) throws Exception {
		List<Map<String, String>> response = Lists.newArrayList();

		for (SuggestionDTO dto : suggestionService.suggestProject(term, getCurrentUser().getId())) {
			response.add(ImmutableMap.of(
				"id", dto.getId().toString(),
				"name", dto.getValue()
			));
		}

		model.addAttribute("response", response);
	}

	// This is for the new dashboard
	@RequestMapping(
		value = "/bulk_selection",
		method = POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder setBulkSelect(@RequestBody BulkSelectionDTO bulkSelect) {
		Optional<WorkSearchRequest> opt = workSearchRequestRepository.get(getCurrentUser().getId());
		WorkSearchRequest request = opt.isPresent() ? opt.get() : new WorkSearchRequest().setWorkSearchRequestUserType(getCurrentUser().getWorkSearchRequestUserType());
		request.setFullSelectAll(bulkSelect.isSelectAll());
		workSearchRequestRepository.set(getCurrentUser().getId(), request);

		return new AjaxResponseBuilder().setSuccessful(true);
	}

	// This can be removed when the old dashboard is retired
	@RequestMapping(
		value = "/full_select_all",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public AjaxResponseBuilder setFullSelectAll(@RequestParam(value = "is_select_all", required = false) boolean isSelectAll) {
		Optional<WorkSearchRequest> opt = workSearchRequestRepository.get(getCurrentUser().getId());
		WorkSearchRequest request = opt.isPresent() ? opt.get() : new WorkSearchRequest()
			.setWorkSearchRequestUserType(getCurrentUser().getWorkSearchRequestUserType());
		request.setFullSelectAll(isSelectAll);
		workSearchRequestRepository.set(getCurrentUser().getId(), request);

		return new AjaxResponseBuilder().setSuccessful(true);
	}

	@SuppressWarnings("unchecked") private void addModelAttributes(Model model, WorkDashboardForm form, ExtendedUserDetails currentUser) {
		model.addAttribute("clients", formOptionsDataHelper.getClients(currentUser));
		model.addAttribute("projects", formOptionsDataHelper.getProjects(currentUser));
		model.addAttribute("users", formOptionsDataHelper.getActiveUsers(currentUser));
		model.addAttribute("followers", formOptionsDataHelper.getFollowers(currentUser.getCompanyId(),
			Collections.EMPTY_LIST));
		model.addAttribute("companyAvatars", companyService.findCompanyAvatars(getCurrentUser().getCompanyId()));
		model.addAttribute("reserveFundsEnabledFlag", companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId()).getReserveFundsEnabledFlag());

		model.addAttribute("lastUpdated", DateUtilities.getCalendarNow());
		model.addAttribute("defaultScheduleFrom", DateUtilities.subtractTime(DateUtilities.getCalendarNow(), 90, Constants.DAY));
		model.addAttribute("defaultScheduleThrough", DateUtilities.addTime(DateUtilities.getCalendarNow(), 120, Constants.DAY));

		Date midnightNextDay = DateUtilities.getMidnightNextDay(new Date());
		model.addAttribute("defaultTimeFrom", DateUtilities.getCalendarNow(currentUser.getTimeZoneId()));
		model.addAttribute("defaultTimeThrough", DateUtilities.getCalendarFromDate(midnightNextDay, currentUser.getTimeZoneId()));
		model.addAttribute("include_time", form.getInclude_time());

		model.addAttribute("dateRangeFilters", WorkMilestoneFilter.getFilterMap());
		model.addAttribute("dateRangeSubFilters", WorkDateRangeFilter.getFilterMap());
	}
}
