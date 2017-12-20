package com.workmarket.web.helpers;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.solr.repository.WorkSearchableFields;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.DateRangeUtilities;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.user.UserDashboardInfo;
import com.workmarket.domains.work.service.dashboard.DashboardResultService;
import com.workmarket.domains.work.service.dashboard.WorkDashboardService;
import com.workmarket.domains.work.service.follow.WorkFollowService;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.search.model.WorkSearchResponse;
import com.workmarket.redis.repositories.WorkSearchRequestRepository;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.request.work.WorkSearchType;
import com.workmarket.search.response.work.DashboardAddressUtilities;
import com.workmarket.search.response.work.DashboardResource;
import com.workmarket.search.response.work.DashboardResponse;
import com.workmarket.search.response.work.DashboardResponseSidebar;
import com.workmarket.search.response.work.DashboardResult;
import com.workmarket.search.response.work.DashboardStatus;
import com.workmarket.search.response.work.WorkDateRangeFilter;
import com.workmarket.search.response.work.WorkMilestoneFilter;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.SpamSlayer;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.forms.work.WorkDashboardForm;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static com.workmarket.search.response.work.WorkDateRangeFilter.CUSTOM_RANGE;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.substring;
import static org.hamcrest.Matchers.greaterThan;

@Component
public class WorkDashboardHelper {
	@Autowired private WorkStatusService workStatusService;
	@Autowired private WorkDashboardService dashboardService;
	@Autowired private WorkFollowService workFollowService;
	@Autowired private WorkSearchRequestRepository workSearchRequestRepository;
	@Autowired private DashboardResultService dashboardResultService;
	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired private FeatureEvaluator featureEvaluator;

	private static Map<String, WorkStatusType> WORK_STATUS_TYPES;

	@PostConstruct
	private void init() {
		WORK_STATUS_TYPES = CollectionUtilities.newEntityIdMap(workStatusService.findAllStatuses(), "code");
		WORK_STATUS_TYPES.put(WorkStatusType.ALL, new WorkStatusType(WorkStatusType.ALL, "All"));
		WORK_STATUS_TYPES.put(WorkStatusType.INPROGRESS, new WorkStatusType(WorkStatusType.INPROGRESS, "In Progress"));
		WORK_STATUS_TYPES.put(WorkStatusType.AVAILABLE, new WorkStatusType(WorkStatusType.AVAILABLE, "Available"));
		WORK_STATUS_TYPES.put(WorkStatusType.SENT_WITH_OPEN_NEGOTIATIONS, new WorkStatusType(WorkStatusType.SENT_WITH_OPEN_NEGOTIATIONS, "Open Offers"));
		WORK_STATUS_TYPES.put(WorkStatusType.SENT_WITH_OPEN_QUESTIONS, new WorkStatusType(WorkStatusType.SENT_WITH_OPEN_QUESTIONS, "Open Questions"));
		WORK_STATUS_TYPES.put(WorkStatusType.APPLIED, new WorkStatusType(WorkStatusType.APPLIED, "Applied"));
	}

	public WorkSearchResponse searchWork(final WorkDashboardForm form, final ExtendedUserDetails currentUser) {

		WorkSearchRequest request;

		if (form.getFilterless()) {
			request = new WorkSearchRequest()
				.setUserNumber(currentUser.getUserNumber());

			request.setKeyword(form.getKeyword());
		} else {
			request = new WorkSearchRequest()
				.setUserNumber(currentUser.getUserNumber())
				.setStartRow(form.getStart())
				.setPageSize(form.getPageSize())
				.setSortBy(form.getSortType())
				.setSortDirection(form.getSortDirection())
				.setIncludeLabelDrilldownFacet(true)
				.setShowAllAtCompany(true)
				.setStatusFilter(form.getStatusFilter())
				.setSubStatusFilter(form.getSubStatusFilter())
				.setBundles(form.getBundleFilter())
				.setClients(form.getClientFilter())
				.setProjects(form.getProjectFilter())
				.setInternalOwners(form.getInternalOwnerFilter())
				.setFilterPendingMultiApprovals(form.getFilterPendingMultiApprovals())
				.addFilterQueries(CollectionUtilities.newStringMap(
					WorkSearchableFields.TITLE.getName(), form.getTitle(),
					WorkSearchableFields.ASSIGNED_RESOURCE_NAME.getName(), form.getAssignedResourceName(),
					WorkSearchableFields.CLIENT_COMPANY_NAME.getName(), form.getClientCompanyName(),
					WorkSearchableFields.BUYER_FULL_NAME.getName(), form.getBuyerFullName(),
					WorkSearchableFields.PROJECT_NAME.getName(), form.getProjectName()
				));

			if (form.getWorkMilestone() != null) {
				request.setWorkMilestoneFilter(WorkMilestoneFilter.valueOf(form.getWorkMilestone()));
			}

			if (isNotEmpty(form.getKeyword()))
				request.setKeyword(form.getKeyword());

			if (form.getFollowing() != null) {
				request.setFollowerIds(ImmutableSet.of(currentUser.getId()));
			}

			if (form.getAssigned_to_me() != null) {
				request.setAssignedResources(
					ImmutableSet.of(new DashboardResource().setResourceId(currentUser.getId()))
				);
			} else {
				request.setAssignedResources(form.getAssignedResourceFilter());
			}

			if (form.getDispatched_by_me() != null) {
				request.setDispatchers(ImmutableSet.of(currentUser.getId()));
			}

			TimeZone tZone = TimeZone.getTimeZone(currentUser.getTimeZoneId());
			if (form.getWorkDateRange() != null) {
				WorkDateRangeFilter drType = WorkDateRangeFilter.valueOf(form.getWorkDateRange());

				if (CUSTOM_RANGE.equals(drType)) {
					request.setDateRange(form.getDateRangeFilter(tZone));
				} else {
					request.setDateRange(drType.getFilteredDateRange(tZone));
				}
			}
		}

		WorkSearchRequestUserType workSearchRequestUserType = currentUser.getWorkSearchRequestUserType();
		request.setWorkSearchRequestUserType(workSearchRequestUserType);

		workSearchRequestRepository.set(currentUser.getId(), request);
		request.setWorkSearchType(WorkSearchType.DASHBOARD);
		DashboardResponse response = dashboardService.getDashboard(request);
		List<Map<String, Object>> data = Lists.newArrayList();

		boolean doShowBulkOps = false;
		int numFiltered = 0;
		DashboardResponseSidebar sidebar = response.getSidebar();
		Map<Long, DashboardResource> assignedResourceMap = sidebar.getDashboardAssignedResources();
		Map<String, Object> statusCounts = getStatusCounts(sidebar);
		Map<String, Object> subStatusCounts = getSubStatusCounts(sidebar);

		if (response.getDashboardResultList().getResultsSize() > 0) {
			List<Long> assignedResourceIds = select(extract(request.getAssignedResources(), on(DashboardResource.class).getResourceId()), greaterThan(0L));
			List<Long> assignedVendorIds = select(extract(request.getAssignedResources(), on(DashboardResource.class).getResourceCompanyId()), greaterThan(0L));

			for (DashboardResult item : response.getDashboardResultList().getResults()) {
				Map<String, Object> workItem;

				// If no resource filtering is requested
				if (CollectionUtilities.isEmpty(assignedResourceIds) && CollectionUtilities.isEmpty(assignedVendorIds)) {
					workItem = mapWorkItem(form, item, currentUser);
				} // otherwise see if the resource is set and check id
				else if (item.isSetResource() &&
					(assignedResourceIds.contains(item.getResource().getResourceId()) ||
					 assignedVendorIds.contains(item.getResource().getResourceCompanyId()))) {
					workItem = mapWorkItem(form, item, currentUser);
					item.getResource().setFilteredOn(true);
				} else { // skip work item mapping
					numFiltered++;
					continue;
				}

				// add work to response
				data.add(workItem);

				//check to see whether to show bulk Approve button - once is set the Boolean will remain true
				Boolean canApprove = Boolean.parseBoolean(String.valueOf(workItem.get("can_approve")));
				if (!doShowBulkOps && WorkStatusType.COMPLETE.equals(item.getWorkStatusTypeCode()) && canApprove) {
					doShowBulkOps = true;
				}
			}
		}

		final WorkSearchResponse dto = new WorkSearchResponse();

		dto.setData(data);
		dto.setStatusCounts(statusCounts);
		dto.setShowBulkOps(doShowBulkOps);
		dto.setSubStatusCounts(subStatusCounts);
		dto.setAssignedResources(assignedResourceMap);
		dto.setTotalResultsCount(response.getTotalResultsCount());
		dto.setResultIds(response.getDashboardResultList().getResultIds());
		dto.setResultsCount(response.getDashboardResultList().getTotalResults() - numFiltered);

		return dto;
	}

	public Map<String, Object> getDashboard(Model model, WorkDashboardForm form, ExtendedUserDetails currentUser) {
		final WorkSearchResponse searchResponse = searchWork(form, currentUser);
		final Map<String, Object> response = CollectionUtilities.newObjectMap(
			"assignedResources", searchResponse.getAssignedResources(),
			"results_count", searchResponse.getResultsCount(),
			"show_bulk_ops", searchResponse.isShowBulkOps(),
			"data", searchResponse.getData(),
			"counts", searchResponse.getStatusCounts(),
			"substatuses", searchResponse.getSubStatusCounts(),
			"result_ids", searchResponse.getResultIds(),
			"resultCount", searchResponse.getTotalResultsCount()
		);

		model.addAttribute("response", response);

		return response;
	}

	// The following is a bit of a mess as required
	// by the data structures that the frontend is expecting.
	// PHP previously had static data structures defining the list of statuses;
	// instead query the DB at[ startup to build a status lookup table, and use
	// static lists of status codes to dictate the display ordering.

	@SuppressWarnings("unchecked")
	private Map<String, Object> getStatusCounts(DashboardResponseSidebar sidebar) {
		List<Map<String, Object>> buyerStatuses = Lists.newArrayList();
		Map<String, Object> buyerSubStatuses = Maps.newLinkedHashMap();
		final Map<String, DashboardStatus> dashboardStatuses = MapUtils.emptyIfNull(sidebar.getDashboardStatuses());
		for (String s : WorkStatusType.ALL_DASHBOARD_STATUSES_FOR_DISPLAY) {
			WorkStatusType t = WORK_STATUS_TYPES.get(s);
			DashboardStatus status = dashboardStatuses.get(t.getCode());
			int count = status != null ? status.getStatusCount() : 0;
			buyerStatuses.add(CollectionUtilities.newObjectMap(
				"id", t.getCode(),
				"description", t.getDescription(),
				"count", count
			));

			if (sidebar.getDashboardClientSubStatusesByStatusSize() > 0 && sidebar.getDashboardClientSubStatusesByStatus().containsKey(t.getCode())) {
				List<Map<String, Object>> subStatuses = Lists.newArrayList();
				for (DashboardStatus st : sidebar.getDashboardClientSubStatusesByStatus().get(t.getCode())) {
					subStatuses.add(CollectionUtilities.newObjectMap(
						"id", st.getStatusId(),
						"description", st.getStatusDescription(),
						"count", st.getStatusCount(),
						"color_rgb", st.getColorRgb(),
						"substatus", 1,
						"parent", t.getCode()
					));
				}

				buyerSubStatuses.put(s, subStatuses);
			}
		}

		// Odd case for "sent" work where additional statuses need to be
		// presented as "virtual" sub-statuses
		List<Map<String, Object>> sentSubStatuses;
		if (buyerSubStatuses.containsKey(WorkStatusType.SENT)) {
			sentSubStatuses = (List<Map<String, Object>>) buyerSubStatuses.get(WorkStatusType.SENT);
		} else {
			sentSubStatuses = Lists.newArrayList();
			buyerSubStatuses.put(WorkStatusType.SENT, sentSubStatuses);
		}

		for (String s : WorkStatusType.BUYER_SENT_SUB_STATUSES_FOR_DISPLAY) {
			DashboardStatus status = dashboardStatuses.get(s);
			if (status != null) {
				WorkStatusType t = WORK_STATUS_TYPES.get(s);
				sentSubStatuses.add(CollectionUtilities.newObjectMap(
					"id", status.getStatusId(),
					"description", t.getDescription(),
					"count", status.getStatusCount(),
					"color_rgb", status.getColorRgb(),
					"substatus", 1,
					"parent", WorkStatusType.SENT
				));
			}
		}

		// case for my pending approvals
		if (dashboardStatuses.containsKey(WorkStatusType.PENDING_MULTI_APPROVALS)) {
			buyerStatuses.add(CollectionUtilities.newObjectMap(
				"id", WorkStatusType.PENDING_MULTI_APPROVALS,
				"description", "My Pending Approvals",
				"count", dashboardStatuses.get(WorkStatusType.PENDING_MULTI_APPROVALS).getStatusCount()
			));
		}

		return CollectionUtilities.newTypedObjectMap(
			"resource", Lists.newArrayList(),
			"buyer", buyerStatuses,
			"sub_menu", CollectionUtilities.newObjectMap(
				"buyer", buyerSubStatuses
			)
		);
	}

	private Map<String, Object> getSubStatusCounts(DashboardResponseSidebar sidebar) {
		List<Map<String, Object>> buyerStatuses = Lists.newArrayList();
		if (MapUtils.isNotEmpty(sidebar.getDashboardSubStatuses())) {
			for (Map.Entry<String, DashboardStatus> entry : sidebar.getDashboardSubStatuses().entrySet()) {
				buyerStatuses.add(CollectionUtilities.newObjectMap(
					"id", entry.getKey(),
					"code", entry.getValue().getStatusName(),
					"description", entry.getValue().getStatusDescription(),
					"color_rgb", entry.getValue().getColorRgb(),
					"count", entry.getValue().getStatusCount(),
					"dashboard_display_type", entry.getValue().getDashboardDisplayType()
				));
			}
		}

		return CollectionUtilities.newTypedObjectMap(
			"resource", buyerStatuses,
			"buyer", buyerStatuses
		);
	}

	private Map<String, Object> mapWorkItem(WorkDashboardForm form, DashboardResult item, ExtendedUserDetails currentUser) {
		if (hasFeature("dashboard") && form.isFast()) {
			UserDashboardInfo userDashboardInfo = new UserDashboardInfo()
				.setId(currentUser.getId())
				.setCompanyId(currentUser.getCompanyId())
				.setCanManageWork(currentUser.hasAllRoles("PERMISSION_MANAGECOWORK"))
				.setCanApproveWork(currentUser.hasAllRoles("PERMISSION_APPROVEWORK"));

			return dashboardResultService.getMappedWorkItem(form, item, userDashboardInfo);
		}

		final boolean isOwnersCompany = currentUser.getCompanyId().equals(item.getOwnerCompanyId());
		final boolean isAdmin = currentUser.hasAllRoles("PERMISSION_MANAGECOWORK") && isOwnersCompany;
		final boolean isMe = currentUser.getId().equals(item.getBuyerId());
		final boolean isResource = !isMe && !isAdmin;
		final boolean canApprove = currentUser.hasAllRoles("PERMISSION_APPROVEWORK") && isOwnersCompany;
		final boolean canSeeClientInfo = isAdmin || Lists.newArrayList(WorkStatusType.ACTIVE, WorkStatusType.COMPLETE, WorkStatusType.PAYMENT_PENDING, WorkStatusType.PAID).contains(item.getWorkStatusTypeCode());

		// Last modified on
		String selectedStatus = form.getStatus();
		String lastModified = item.getFormattedLastModifiedDate();
		String price = item.getFormattedPrice(isResource);
		return CollectionUtilities.newObjectMap(
			"id", item.getWorkNumber(),
			"parent_id", item.getParentId(),
			"parent_title", StringUtilities.truncate(item.getParentTitle(), 30, "..."),
			"parent_description", item.getParentDescription(),
			"title", item.getTitle(),
			"title_short", StringUtilities.truncate(item.getTitle(), 70),
			"scheduled_date", item.getAssignmentAppointment() != null ? DateRangeUtilities.format("MMM dd hh:mm aa", "MMM dd hh:mm aa z", " - ", item.getAssignmentAppointment(), item.getTimeZoneId()) : null,
			"scheduled_date_from_in_millis", item.getAssignmentAppointment() != null ? item.getAssignmentAppointment().getFrom().getTime().getTime() : null,
			"scheduled_date_through_in_millis", item.getAssignmentAppointment() != null && item.getAssignmentAppointment().isRange() ? item.getAssignmentAppointment().getThrough().getTime().getTime() : null,
			"address", DashboardAddressUtilities.formatAddressShort(item.getAddress()),
			"location_name", (canSeeClientInfo && item.isSetAddress()) ? item.getAddress().getLocationName() : null,
			"location_number", (isAdmin && item.isSetAddress()) ? item.getAddress().getLocationNumber() : null,
			"location_offsite", !item.getResultFlags().isAddressOnsiteFlag(),
			"price", price,
			"work_amount", item.getBuyerTotalCost(),
			"amount_earned", NumberUtilities.currency(item.getAmountEarned()),
			"paid_on", (item.isSetPaidOn()) ? DateUtilities.formatMillis("MMM dd, yyyy hh:mm aa z", item.getPaidOn(), item.getTimeZoneId()) : null,
			"auto_pay_enabled", item.getResultFlags().isAutoPayEnabled(),
			"custom_fields", item.getCustomFieldMap(),

			"type", (currentUser.isSeller() ? "working" : "managing"),
			"status", item.getFormattedWorkStatusType(currentUser.getWorkSearchRequestUserType(), currentUser.getCompanyId(), selectedStatus),
			"raw_status", item.getWorkStatusTypeCode(),
			"substatuses", item.getUnresolvedWorkSubStatuses(),

			"buyer", item.getBuyerFullName(), // assigned to (w/in buyer company)
			"buyer_id", item.getBuyerId(),
			"company_id", item.getOwnerCompanyId(),
			"owner_company_name", item.getOwnerCompanyName(),
			"client", (canSeeClientInfo) ? item.getClient() : null,

			"lane", null,
			"resource", item.getResource(),
			"resource_id", item.isSetResource() && item.getResource().isSetResourceId() ? item.getResource().getResourceId() : null,
			"resource_user_number", (item.isSetResource()) ? item.getResource().getResourceUserNumber() : null,
			"resource_full_name", (item.isSetResource() && item.getResource().isSetResourceFirstName()) ? StringUtilities.fullName(item.getResource().getResourceFirstName(), item.getResource().getResourceLastName()) : null,
			"resource_company_id", item.isSetResource() && item.getResource().isSetResourceCompanyId() ? item.getResource().getResourceCompanyId() : null,
			"resource_company_name", (item.isSetResource()) ? item.getResource().getResourceCompanyName() : null,
			"resource_mobile_phone", item.isSetResource() ? SpamSlayer.slay(StringUtilities.formatPhoneNumber(item.getResource().getMobilePhone())) : null,
			"resource_work_phone", item.isSetResource() ? SpamSlayer.slay(StringUtilities.formatPhoneNumber(item.getResource().getWorkPhone())) : null,

			"last_modified_on", lastModified,
			"modifier_first_name", item.isSetModifierFirstName() ? substring(item.getModifierFirstName(), 0, 1) : "",
			"modifier_last_name", item.getModifierLastName(),

			"is_admin", isAdmin,
			"is_resource", isResource,
			"is_me", isMe,
			"can_approve", canApprove,
			"is_owners_company", isOwnersCompany,
			"is_assign_to_first_resource", item.getResultFlags().isAssignToFirstResource(),
			"is_applied", item.getResultFlags().isApplied(),
			"is_applications_pending", item.getResultFlags().isApplicationsPending(),

			"is_following", isMe || workFollowService.isFollowingWork(item.getId(), currentUser.getId()),
			"candidates", item.getDispatchCandidateNames(),
			"recurrenceUUID", item.getRecurrenceUUID(),

			"project_name", item.getProjectName(),
			"project_id", (item.isSetProjectId() ? item.getProjectId() : null),
			"city", (item.isSetAddress() ? item.getAddress().getCity() : null),
			"state", (item.isSetAddress() ? item.getAddress().getState() : null),
			"postal_code", (item.isSetAddress() ? item.getAddress().getPostalCode() : null)
		);
	}

	public boolean hasFeature(String feature) {
		return featureEvaluator.hasFeature(getAuthentication(), feature);
	}

	protected Authentication getAuthentication() {
		return securityContextFacade.getSecurityContext().getAuthentication();
	}
}
