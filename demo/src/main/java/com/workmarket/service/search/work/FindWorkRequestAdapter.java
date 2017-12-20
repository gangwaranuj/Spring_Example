package com.workmarket.service.search.work;

import com.google.common.collect.Lists;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.query.SolrMetricConstants;
import com.workmarket.data.solr.query.location.LocationQueryCreationService;
import com.workmarket.data.solr.repository.WorkSearchableFields;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.search.cache.StateLookupCache;
import com.workmarket.search.gen.Common.PostalCode;
import com.workmarket.search.gen.Common.RequestField;
import com.workmarket.search.gen.Common.RequestMonitor;
import com.workmarket.search.gen.Common.SortDirectionType;
import com.workmarket.search.gen.WorkMessages;
import com.workmarket.search.gen.WorkMessages.FindWorkRequest;
import com.workmarket.search.gen.WorkMessages.RoleSpecificFilterQuery;
import com.workmarket.search.gen.WorkMessages.WorkSearchRequestType;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.model.WorkSearchTransientData;
import com.workmarket.search.request.SearchRequest;
import com.workmarket.search.request.SearchSortDirection;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.response.work.DashboardBundle;
import com.workmarket.search.response.work.DashboardClient;
import com.workmarket.search.response.work.DashboardInternalOwner;
import com.workmarket.search.response.work.DashboardProject;
import com.workmarket.search.response.work.DashboardResource;
import com.workmarket.search.response.work.DashboardStatusFilter;
import com.workmarket.search.response.work.WorkMilestoneFilter;
import com.workmarket.service.external.GeocodingException;
import com.workmarket.utility.SearchUtilities;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static com.workmarket.utility.DateUtilities.getLuceneDate;
import static com.workmarket.utility.SearchUtilities.encode;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.Matchers.greaterThan;

/**
 * This is basically a rewrite of WorkSearchQuery w.r.t. FindWorkRequest.
 */
@Component
public class FindWorkRequestAdapter {

	private static final Logger logger = LoggerFactory.getLogger(FindWorkRequestAdapter.class);

	private static double KILO_IN_MILE = 1.60934;

	@Autowired private StateLookupCache stateLookupCache;
	@Autowired private LocationQueryCreationService locationQueryCreationService;

	public FindWorkRequest buildFindWorkRequest(
		final WorkSearchRequest request,
		final WorkSearchTransientData transientData
	) {
		final FindWorkRequest.Builder findWorkRequest = FindWorkRequest.newBuilder();

		buildRequestMonitor(request, transientData, findWorkRequest);
		findWorkRequest
			.setStart(request.getStartRow())
			.setRows(request.getPageSize())
			.setIncludeLabelDrilldownFacet(request.isIncludeLabelDrilldownFacet());

		final WorkSearchRequestType requestType = getWorkSearchRequestType(request);
		findWorkRequest.setWorkSearchRequestType(requestType);

		buildLocationFilter(request, findWorkRequest);

		if (WorkSearchRequestType.ALL.equals(requestType)) {
			buildKeywordQuery(request, findWorkRequest);
			buildIngoreVirtualFilter(request, findWorkRequest);
			buildWorkSubStatusTypeFilter(request, findWorkRequest);
			buildWorkBeginEndDateFilter(request, findWorkRequest);
			buildWorkStatusFilter(request, findWorkRequest);
			buildClientCompanyFilter(request, findWorkRequest);
			buildProjectFilter(request, findWorkRequest);
			buildInternalOwnerFilter(request, findWorkRequest);
			buildAssignedResourceFilter(request, findWorkRequest);
			buildDispatcherFilter(request, findWorkRequest);
			buildBundleFilter(request, findWorkRequest);
			buildFollowerFilter(request, findWorkRequest);
			buildMultiApprovalFilter(request, findWorkRequest);
			buildRoleSpecificFilterQuery(request, transientData, findWorkRequest);
			buildWorkStatusFilter(request, transientData, findWorkRequest);
			buildSort(request, findWorkRequest);
		} else if (WorkSearchRequestType.WORK_KPI.equals(requestType)) {
			buildWorkBeginEndDateFilter(request, findWorkRequest);
			findWorkRequest.setWorkStatusTypeCode(
				RequestField.newBuilder().setFilter(true).addValue(WorkStatusType.PAID));
			if (transientData.getCurrentUser().getCompanyId() != null) {
				findWorkRequest.setCompanyId(
					RequestField.newBuilder()
						.setFilter(true)
						.addValue(transientData.getCurrentUser().getCompanyId().toString()));
			}
			findWorkRequest.setCountyId(
				RequestField.newBuilder()
					.setFilter(true)
					.setRange(true)
					.addAllValue(Lists.newArrayList("*", "*")));
		} else {
			buildKeywordQuery(request, findWorkRequest);
			buildRoleSpecificFilterQuery(request, transientData, findWorkRequest);
			buildWorkStatusFilter(request, transientData, findWorkRequest);
		}

		return findWorkRequest.build();
	}

	private WorkSearchRequestType getWorkSearchRequestType(final WorkSearchRequest request) {
		if (SearchRequest.ALL.equals(request.getType()) || StringUtils.isBlank(request.getType())) {
			return WorkSearchRequestType.ALL;
		} else if (SearchType.WORK_KPI.toString().equals(request.getType())) {
			return WorkSearchRequestType.WORK_KPI;
		} else {
			return WorkSearchRequestType.UNKNOWN;
		}
	}

	private void buildRequestMonitor(
		final WorkSearchRequest request,
		final WorkSearchTransientData data,
		final FindWorkRequest.Builder findWorkRequest) {
		final RequestMonitor.Builder monitor = RequestMonitor.newBuilder();
		if (data.getCurrentUser() != null) {
			if (data.getCurrentUser().getId() != null) {
				monitor.setMUser(data.getCurrentUser().getId().toString());
			}
			if (data.getCurrentUser().getCompanyId() != null) {
				monitor.setMCompany(data.getCurrentUser().getCompanyId().toString());
			}
		}
		monitor.setMRequestSource(request.isMobile() ? SolrMetricConstants.MOBILE_REQUEST : SolrMetricConstants.WEB_REQUEST);
		if (WorkSearchRequestUserType.CLIENT.equals(data.getUserType())) {
			monitor.setMPersona(SolrMetricConstants.EMPLOYER_PERSONA);
		} else if (WorkSearchRequestUserType.RESOURCE.equals(data.getUserType())) {
			monitor.setMPersona(SolrMetricConstants.WORKER_PERSONA);
		}
		if (request.getWorkSearchType() != null) {
			monitor.setMSearchType(request.getWorkSearchType().name().toLowerCase());
		}
		findWorkRequest.setRequestMonitor(monitor);
	}

	private void buildKeywordQuery(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (SearchType.WORK_KPI.toString().equals(request.getType())) {
			return;
		}
		if (StringUtils.isBlank(request.getKeyword())) {
			return;
		}

		// FIXME: Currently service side would quote each individual strings before putting them in the query,
		// so we actively tokenize the string here. But the following logic should be moved to service.
		// App dev should not be aware of those details.
		List<String> effectiveKeywords = Lists.newArrayList();
		for (String keyword : StringUtils.split(request.getKeyword(), " ")) {
			String effectiveKeyword = encode(keyword);
			if (StringUtils.isNotBlank(effectiveKeyword)) {
				effectiveKeywords.add(effectiveKeyword);
			}
		}
		if (effectiveKeywords.isEmpty()) {
			return;
		}

		final RequestField keywordField = RequestField.newBuilder()
			.setQuery(true)
			.addAllValue(effectiveKeywords)
			.build();
		switch (request.getType()) {
			case "title":
				findWorkRequest.setTitle(keywordField);
				break;
			case "description":
				findWorkRequest.setDescription(keywordField);
				break;
			case "instructions":
				findWorkRequest.setInstructions(keywordField);
				break;
			default:
				findWorkRequest.setMainFields(keywordField);
		}
	}

	private void buildWorkSubStatusTypeFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest) {
		if (request.isSetSubStatusFilter() && request.getSubStatusFilter().isSetStatusCode()) {
			String filter = request.getSubStatusFilter().getStatusCode();
			if (!WorkStatusType.ALL.equals(filter)) {
				findWorkRequest.setBuyerLabelsId(RequestField.newBuilder()
					.addValue(filter)
					.setFilter(true)
					.setFacetExcludeName("workSubStatusId"));
			}

			//check if the filter is a combined filter with the work status
			if (request.isSetStatusFilter() && request.getStatusFilter().isSetStatusCode()) {
				filter = request.getStatusFilter().getStatusCode();
				findWorkRequest.setSearchableWorkStatusTypeCode(RequestField.newBuilder()
					.addValue(filter)
					.setFilter(true)
					.setFacetExcludeName("workStatus"));
			}
		}

		if (request.isSetSubStatusMultiFilter()) {
			List<String> labelFilters = Lists.newArrayListWithExpectedSize(request.getSubStatusMultiFilter().size());
			for (DashboardStatusFilter dashboardStatusFilter : request.getSubStatusMultiFilter()) {
				if (dashboardStatusFilter.isSetStatusCode()) {
					labelFilters.add(dashboardStatusFilter.getStatusCode());
				}
			}
			// FIXME: will this replace the previous buyer labels id filtering
			findWorkRequest.setBuyerLabelsId(RequestField.newBuilder()
				.addAllValue(labelFilters)
				.setFilter(true)
				.setFacetExcludeName("buyerLabelsId"));
		}
	}

	private void buildWorkBeginEndDateFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		DateRange dateRange = request.getDateRange();
		WorkMilestoneFilter wmFilter = request.getWorkMilestoneFilter();
		if (dateRange != null && wmFilter != null && dateRange.getFrom() != null && dateRange.getThrough() != null) {
			long from = dateRange.getFrom().getTimeInMillis();
			long to = dateRange.getThrough().getTimeInMillis();
			final RequestField dateRangeReq = RequestField.newBuilder()
				.setRange(true)
				.setFilter(true)
				.addAllValue(Lists.newArrayList(getLuceneDate(from), getLuceneDate(to)))
				.build();
			switch (wmFilter) {
				case SCHEDULED_DATE:
					findWorkRequest.setScheduleFromDate(dateRangeReq);
					break;
				case APPROVED_DATE:
					findWorkRequest.setApprovedDate(dateRangeReq);
					break;
				case COMPLETED_DATE:
					findWorkRequest.setCompletedDate(dateRangeReq);
					break;
				case PAID_DATE:
					findWorkRequest.setPaidDate(dateRangeReq);
					break;
				case SENT_DATE:
					findWorkRequest.setSendDate(dateRangeReq);
					break;
				case LAST_MODIFIED_DATE:
					findWorkRequest.setLastModifiedDate(dateRangeReq);
					break;
				case INDEX_DATE:
					findWorkRequest.setIndexDate(dateRangeReq);
					break;
				default:
					findWorkRequest.setCreatedDate(dateRangeReq);
			}
		}
	}

	private RequestField buildIdsFilter(final Collection<Long> ids, final String tagName) {
		Collection<String> idsStr = Lists.newArrayListWithExpectedSize(ids.size());
		for (Long id : ids) {
			idsStr.add(id.toString());
		}
		return RequestField.newBuilder()
			.addAllValue(idsStr)
			.setFilter(true)
			.setFacetExcludeName(tagName)
			.build();
	}

	private void buildSort(final WorkSearchRequest request, final FindWorkRequest.Builder findWorkRequest) {
		RequestField.Builder sortField =
			RequestField.newBuilder().setSort(true).setSortDirection(SortDirectionType.desc);
		if (request.getSortBy() == null) {
			findWorkRequest.setWorkStatusTypeCode(sortField);
			return;
		}
		if (SearchSortDirection.ASCENDING.equals(request.getSortDirection())) {
			sortField.setSortDirection(SortDirectionType.asc);
		}
		switch (request.getSortBy()) {
			case CREATED_ON:
				findWorkRequest.setCreatedDate(sortField);
				break;
			case SCHEDULED_FROM:
				findWorkRequest.setScheduleFromDate(sortField);
				break;
			case APPROVED_DATE:
				findWorkRequest.setApprovedDate(sortField);
				break;
			case CLIENT:
				findWorkRequest.setClientCompanyName(sortField);
				break;
			case COMPLETED_DATE:
				findWorkRequest.setCompletedDate(sortField);
				break;
			case LAST_MODIFIED_DATE:
				findWorkRequest.setLastModifiedDate(sortField);
				break;
			case PAID_DATE:
				findWorkRequest.setPaidDate(sortField);
				break;
			case SENT_DATE:
				findWorkRequest.setSendDate(sortField);
				break;
			case TITLE:
				findWorkRequest.setTitleSort(sortField);
				break;
			case STATE:
				findWorkRequest.setState(sortField);
				break;
			case DUE_DATE:
				findWorkRequest.setDueDate(sortField);
				break;
			default:
				findWorkRequest.setWorkStatusTypeCode(sortField);
		}
	}

	private void buildRoleSpecificFilterQuery(
		final WorkSearchRequest request,
		final WorkSearchTransientData transientData,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (transientData.getCurrentUser() != null
			&& transientData.getCurrentUser().getId() != null
			&& transientData.getCurrentUser().getCompanyId() != null) {

			findWorkRequest.setRoleSpecificFilterQuery(RoleSpecificFilterQuery.newBuilder()
				.setSearcherId(transientData.getCurrentUser().getId().toString())
				.setSearcherCompanyId(transientData.getCurrentUser().getCompanyId().toString())
				.setDispatcher(request.isDispatcher())
				.setWorkSearchRequestUserTypeValue(request.getWorkSearchRequestUserType().getValue())
				.setShowAllAtCompany(request.isShowAllAtCompany())
				.build());
		}
	}

	private void buildIngoreVirtualFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (request.isIgnoreVirtual()) {
			findWorkRequest
				.setLatitude(
					RequestField.newBuilder()
						.setFilter(true)
						.setRange(true)
						.addAllValue(Lists.newArrayList("-90", "90")))
				.setLongitude(
					RequestField.newBuilder()
						.setFilter(true)
						.setRange(true)
						.addAllValue(Lists.newArrayList("-180", "180"))
				);
		}
	}

	private void buildWorkStatusFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (isNotEmpty(request.getWorkStatusFilters())) {
			findWorkRequest.setWorkStatusTypeCode(RequestField.newBuilder()
				.setFilter(true)
				.addValue(SearchUtilities.joinWithOR(request.getWorkStatusFilters())));
		}
	}

	private void buildClientCompanyFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (isNotEmpty(request.getClients())) {
			findWorkRequest.setClientCompanyId(
				buildIdsFilter(
					extract(request.getClients(), on(DashboardClient.class).getClientId()),
					WorkSearchableFields.CLIENT_COMPANY_ID.getName()));
		}
	}

	private void buildProjectFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (isNotEmpty(request.getProjects())) {
			findWorkRequest.setProjectId(
				buildIdsFilter(
					extract(request.getProjects(), on(DashboardProject.class).getProjectId()),
					WorkSearchableFields.PROJECT_ID.getName()));
		}
	}

	private void buildInternalOwnerFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (isNotEmpty(request.getInternalOwners())) {
			findWorkRequest.setBuyerUserId(
				buildIdsFilter(
					extract(request.getInternalOwners(), on(DashboardInternalOwner.class).getUserId()),
					WorkSearchableFields.BUYER_USER_ID.getName()));
		}
	}

	private void buildAssignedResourceFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (isNotEmpty(request.getAssignedResources())) {
			List<Long> assignedResourceIds = select(extract(request.getAssignedResources(), on(DashboardResource.class).getResourceId()), greaterThan(0L));
			List<Long> assignedVendorIds = select(extract(request.getAssignedResources(), on(DashboardResource.class).getResourceCompanyId()), greaterThan(0L));
			if (isNotEmpty(assignedResourceIds)) {
				findWorkRequest.setAssignedResourceId(
					buildIdsFilter(assignedResourceIds, WorkSearchableFields.ASSIGNED_RESOURCE_ID.getName()));
			}
			if (isNotEmpty(assignedVendorIds)) {
				findWorkRequest.setAssignedResourceCompanyId(
					buildIdsFilter(assignedVendorIds, WorkSearchableFields.ASSIGNED_RESOURCE_COMPANY_ID.getName()));
			}
		}
	}

	private void buildDispatcherFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (isNotEmpty(request.getDispatchers())) {
			findWorkRequest.setDispatcherId(
				buildIdsFilter(request.getDispatchers(), WorkSearchableFields.DISPATCHER_ID.getName()));
		}
	}

	private void buildBundleFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (isNotEmpty(request.getBundles())) {
			findWorkRequest.setParentId(
				buildIdsFilter(
					extract(request.getBundles(), on(DashboardBundle.class).getId()),
					WorkSearchableFields.PARENT_ID.getName()));
		}
	}

	private void buildFollowerFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (isNotEmpty(request.getFollowerIds())) {
			findWorkRequest.setFollowerIds(
				buildIdsFilter(request.getFollowerIds(), WorkSearchableFields.FOLLOWER_IDS.getName()));
		}
	}

	private void buildMultiApprovalFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (request.isFilterPendingMultiApprovals() && isNotEmpty(request.getDecisionFlowUuids())) {
			findWorkRequest.setExternalUniqueIds(
				RequestField.newBuilder()
					.setFilter(true)
					.addAllValue(request.getDecisionFlowUuids())
					.setFacetExcludeName(WorkSearchableFields.EXTERNAL_UNIQUE_IDS.getName()));
		}
	}

	private void buildLocationFilter(
		final WorkSearchRequest request,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (request.getLocationFilter() == null) {
			return;
		}

		final WorkMessages.LocationFilter.Builder locationFilter = WorkMessages.LocationFilter.newBuilder();
		// looks like willingToTravelTo can be state or zipcode!
		final String willingToTravelTo = request.getLocationFilter().getWillingToTravelTo();
		if (StringUtils.isNotBlank(willingToTravelTo)) {
			if (stateLookupCache.isStateQuery(willingToTravelTo)) {
				String stateKeyword = stateLookupCache.getStateCode(willingToTravelTo);
				// NOTE: escape reserved so we can search OR as Oregon
				locationFilter.setWillingToTravelTo(SearchUtilities.escapeReservedWords(stateKeyword));
			}
		}
		int maxMilesFromResourceToLocation = request.getLocationFilter().getMaxMileFromResourceToLocation();
		if (maxMilesFromResourceToLocation > 0 && !request.getLocationFilter().isAnywhere()) {
			locationFilter.setRadiusKilometers((long) (KILO_IN_MILE * maxMilesFromResourceToLocation));
		}
		try {
			// There is a hidden logic in the service that if willingToTravelTo is zipcode, translate to geoPoint
			final GeoPoint geoPoint = locationQueryCreationService.getGeoLocationPoint(request);
			if (geoPoint != null) {
				locationFilter.setPostalCode(PostalCode.newBuilder()
					.setLatitude(String.valueOf(geoPoint.getLatitude()))
					.setLongitude(String.valueOf(geoPoint.getLongitude())));
			}
		} catch (GeocodingException e) {
			logger.warn("failed to get geo code for FindWorkRequest {}", e.getMessage());
		}

		findWorkRequest.setLocationFilter(locationFilter);
	}

	// FIXME: consider how to apply to this to WORK_KPI condition
	private void buildWorkStatusFilter(
		final WorkSearchRequest request,
		final WorkSearchTransientData transientData,
		final FindWorkRequest.Builder findWorkRequest
	) {
		if (request.hasWorkStatusFilter()) {
			String workStatusFitler = request.getStatusFilter().getStatusCode();
			if (WorkStatusType.AVAILABLE.equals(workStatusFitler)) {
				// FIXME: missing negative filter
				findWorkRequest.setResourceWorkStatusTypeCode(
					RequestField.newBuilder()
						.setFilter(true)
						.setFacetExcludeName("resourceWorkStatus")
						.addValue(workStatusFitler));
			} else if (WorkStatusType.APPLIED.equals(workStatusFitler)) {
				findWorkRequest.setApplicantIds(
					RequestField.newBuilder()
						.setFilter(true)
						.setFacetExcludeName("applicantIds")
						.addValue(transientData.getCurrentUser().getId().toString()));
			} else if (!WorkStatusType.ALL.equals(workStatusFitler)) {
				findWorkRequest.setSearchableWorkStatusTypeCode(
					RequestField.newBuilder()
						.setFilter(true)
						.setFacetExcludeName("workStatus")
						.addValue(workStatusFitler));
			}
		}
	}
}
