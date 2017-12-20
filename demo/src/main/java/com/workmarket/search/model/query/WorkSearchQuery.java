package com.workmarket.search.model.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.solr.query.SearchQueryCreatorUtil;
import com.workmarket.data.solr.repository.WorkSearchableFields;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.search.model.WorkSearchTransientData;
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
import com.workmarket.utility.SearchUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static com.workmarket.data.solr.query.SearchQueryCreatorUtil.addFilterQueryStr;
import static com.workmarket.data.solr.query.SearchQueryCreatorUtil.createFacetFieldString;
import static com.workmarket.data.solr.query.SearchQueryCreatorUtil.createFacetSortString;
import static com.workmarket.utility.DateUtilities.getLuceneDate;
import static com.workmarket.utility.SearchUtilities.encode;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.Matchers.greaterThan;

public class WorkSearchQuery extends SearchQuery<WorkSearchRequest> {

	private static final long serialVersionUID = 7135290608633835804L;

	public WorkSearchQuery(WorkSearchRequest request) {
		super(request);
		set("qt", "/workSearch");
	}

	public WorkSearchQuery setFilters() {
		if (request.getFilterQueries() == null) {
			return this;
		}

		Map<String, String> wordsMap = request.getFilterQueries();
		for (String field : wordsMap.keySet()) {
			if (!StringUtils.isEmpty(wordsMap.get(field))) {
				String[] words = StringUtils.split(wordsMap.get(field), " ");
				if (words != null) {
					for (String word : words) {
						this.addFilterQuery("+" + field + ":" + SearchUtilities.sanitizeKeywords(word));
					}
				}
			}
		}
		return this;
	}

	private enum WorkSearchKeyword {
		ASSIGNED_TO("assignedTo", WorkSearchableFields.ASSIGNED_RESOURCE_NAME.getName()),
		PROJECT("project", WorkSearchableFields.PROJECT_NAME.getName()),
		WORK_STATUS("status", WorkSearchableFields.WORK_STATUS_TYPE_DESCRIPTION.getName());

		private final String qString;
		private final String schemaFieldName;

		WorkSearchKeyword(String qString, String schemaFieldName) {
			this.qString = qString;
			this.schemaFieldName = schemaFieldName;
		}
	}

	public WorkSearchQuery addMultipleWorkStatusFilterQuery() {
		if (isNotEmpty(request.getWorkStatusFilters())) {
			String filterQuery = "(" + SearchUtilities.joinWithOR(request.getWorkStatusFilters()) + ")";
			this.addFilterQuery("workStatusTypeCode" + ":" + filterQuery);
		}
		return this;
	}

	public WorkSearchQuery addIgnoreVirtualWorkQuery() {
		if (request.isIgnoreVirtual()) {
			this.addFilterQuery("latitude" + ":" + "[-180 TO 180]");
			this.addFilterQuery("longitude" + ":" + "[-180 TO 180]");
		}
		return this;
	}

	public WorkSearchQuery addBaseFilterQueries(WorkSearchTransientData data) {
		//If there's a filter by status
		if (request.hasWorkStatusFilter()) {
			String filter = request.getStatusFilter().getStatusCode();
			if (!WorkStatusType.ALL.equals(filter)) {

				switch (filter) {
					case WorkStatusType.AVAILABLE:
						this.addFilterQuery("{!tag=resourceWorkStatus}" + WorkSearchableFields.RESOURCE_WORK_STATUS_TYPE_CODE.getName() + ":" + filter);
						this.addFilterQuery("-" + WorkSearchableFields.APPLICANT_IDS.getName() + ":(" + data.getCurrentUser().getId() + ") ");

						break;
					case WorkStatusType.APPLIED:
						this.addFilterQuery("{!tag=applicantIds}" + WorkSearchableFields.APPLICANT_IDS.getName() + ":" + data.getCurrentUser().getId());
						break;
					default:
						this.addFilterQuery("{!tag=workStatus}" + WorkSearchableFields.SEARCHABLE_WORK_STATUS_TYPE_CODE.getName() + ":" + filter);
						break;
				}
				this.addFilterQuery("{!tag=visibility}" + getRoleSpecificFilterQuery(data));
			} else {
				this.addFilterQuery(getRoleSpecificFilterQuery(data));
			}
			//If no status filter, check for protected data
		} else if (request.hasProtectedWorkSearchDataFilter()) {
			this.addFilterQuery("{!tag=visibility}" + getRoleSpecificFilterQuery(data));
		} else {
			this.addFilterQuery(getRoleSpecificFilterQuery(data));
		}

		buildBlockedFilter();
		return this;
	}

	public String getRoleSpecificFilterQuery(WorkSearchTransientData data) {
		StringBuilder filterQuery = new StringBuilder();

		if (request.getWorkSearchRequestUserType() != null) {
			switch (request.getWorkSearchRequestUserType()) {
				case CLIENT:
					if (request.isShowAllAtCompany()) {
						filterQuery
							.append(WorkSearchableFields.COMPANY_ID.getName())
							.append(":(")
							.append(data.getCurrentUser().getCompanyId())
							.append(")");
					} else {
						filterQuery
							.append(WorkSearchableFields.BUYER_USER_ID.getName())
							.append(":(")
							.append(data.getCurrentUser().getId())
							.append(")");
					}
					break;
				default:
					filterQuery.append("(");
					if (request.isShowAllAtCompany()) {
						if (request.isDispatcher()) {
							filterQuery
								.append("(")
								.append(WorkSearchableFields.ASSIGNED_RESOURCE_COMPANY_ID.getName()).append(":(").append(data.getCurrentUser().getCompanyId()).append(") OR ")
								.append("(-" + WorkSearchableFields.ASSIGNED_RESOURCE_COMPANY_ID.getName()).append(":(*)").append(" AND ")
								.append(WorkSearchableFields.RESOURCE_WORK_STATUS_TYPE_CODE.getName() + ":" + WorkStatusType.AVAILABLE).append(" AND ")
								.append(WorkSearchableFields.WORK_RESOURCES_COMPANY_IDS.getName()).append(":(").append(data.getCurrentUser().getCompanyId()).append("))) OR ");
						} else {
							filterQuery.append(WorkSearchableFields.ASSIGNED_RESOURCE_COMPANY_ID.getName()).append(":(").append(data.getCurrentUser().getCompanyId()).append(") OR ");
						}
					} else {
						filterQuery.append(WorkSearchableFields.ASSIGNED_RESOURCE_ID.getName()).append(":(").append(data.getCurrentUser().getId()).append(") OR ");
					}

					filterQuery
						.append("((")
						.append(WorkSearchableFields.WORK_RESOURCES_IDS.getName() + ":(" + data.getCurrentUser().getId() + ") ")
						.append(" OR " + WorkSearchableFields.APPLICANT_IDS.getName() + ":(" + data.getCurrentUser().getId() + ")) ")
						.append(" AND -" + WorkSearchableFields.CANCELLED_WORK_RESOURCE_IDS.getName() + ":(" + data.getCurrentUser().getId() + ") ")
						.append(" AND " + WorkSearchableFields.RESOURCE_WORK_STATUS_TYPE_CODE.getName() + ":" + WorkStatusType.AVAILABLE)
						.append("))");
			}
		}
		return filterQuery.toString();
	}

	// Remove assignments from blocked companies that are in available status
	// i.e. -(companyId:(1 6225) AND resourceWorkStatusTypeCode:available)
	public WorkSearchQuery buildBlockedFilter() {
		if (isNotEmpty(request.getBlockedCompanyIds())) {
			if ((WorkSearchRequestUserType.RESOURCE).equals(request.getWorkSearchRequestUserType())) {
				this.addFilterQuery(
					"-(" + WorkSearchableFields.COMPANY_ID.getName() + ":(" + StringUtils.join(request.getBlockedCompanyIds(), " ") +
					") AND " + WorkSearchableFields.RESOURCE_WORK_STATUS_TYPE_CODE.getName() + ":" + WorkStatusType.AVAILABLE + ")"
				);
			}
		}

		return this;
	}

	public WorkSearchQuery addWorkSubStatusTypeFilter() {
		if (request.isSetSubStatusFilter() && request.getSubStatusFilter().isSetStatusCode()) {
			String filter = request.getSubStatusFilter().getStatusCode();
			if (!WorkStatusType.ALL.equals(filter)) {
				this.addFilterQuery("{!tag=workSubStatusId}" + WorkSearchableFields.BUYER_LABELS_ID.getName() + ":" + filter);
			}

			//check if the filter is a combined filter with the work status
			if (request.isSetStatusFilter() && request.getStatusFilter().isSetStatusCode()) {
				filter = request.getStatusFilter().getStatusCode();
				this.addFilterQuery("{!tag=workStatus}" + WorkSearchableFields.SEARCHABLE_WORK_STATUS_TYPE_CODE.getName() + ":" + filter);
			}
		}

		if (request.isSetSubStatusMultiFilter()) {
			List<String> labelFilters = Lists.newArrayListWithExpectedSize(request.getSubStatusMultiFilter().size());
			for (DashboardStatusFilter dashboardStatusFilter : request.getSubStatusMultiFilter()) {
				if (dashboardStatusFilter.isSetStatusCode()) {
					labelFilters.add(dashboardStatusFilter.getStatusCode());
				}
			}
			addFilterQueryStr(this, WorkSearchableFields.BUYER_LABELS_ID, labelFilters);
		}
		return this;
	}

	public WorkSearchQuery addGenericFilterQuery(String filterString, String param) {
		this.addFilterQuery(filterString + ":" + param);
		return this;
	}

	public WorkSearchQuery addFacetFieldsWithDefaults() {
		this.set("rows", 0);
		this.setFacetMinCount(1);
		this.setFacetLimit(5000); // There are only around 3200 counties in the US and this number is not expected to increase significantly
		this.setFacet(true);
		for (WorkSearchableFields field : request.getFacetFields()) {
			this.addFacetField(createFacetFieldString(field));
		}
		return this;
	}


	public WorkSearchQuery addWorkBeginEndDateFilter() {
		DateRange dateRange = request.getDateRange();
		WorkMilestoneFilter wmFilter = request.getWorkMilestoneFilter();
		if (dateRange != null && wmFilter != null && dateRange.getFrom() != null && dateRange.getThrough() != null) {

			WorkSearchableFields workSearchableField = WorkSearchableFields.CREATED_DATE;
			long from = dateRange.getFrom().getTimeInMillis();
			long to = dateRange.getThrough().getTimeInMillis();
			switch (wmFilter) {
				case SCHEDULED_DATE:
					workSearchableField = WorkSearchableFields.SCHEDULE_FROM_DATE;
					break;
				case APPROVED_DATE:
					workSearchableField = WorkSearchableFields.APPROVED_DATE;
					break;
				case COMPLETED_DATE:
					workSearchableField = WorkSearchableFields.COMPLETED_DATE;
					break;
				case PAID_DATE:
					workSearchableField = WorkSearchableFields.PAID_DATE;
					break;
				case SENT_DATE:
					workSearchableField = WorkSearchableFields.SEND_DATE;
					break;
				case LAST_MODIFIED_DATE:
					workSearchableField = WorkSearchableFields.LAST_MODIFIED_DATE;
					break;
				case INDEX_DATE:
					workSearchableField = WorkSearchableFields.INDEX_DATE;
			}

			this.addFilterQuery(workSearchableField.getName() + ":[ " + getLuceneDate(from) + " TO " + getLuceneDate(to) + " ]");
		}
		return this;
	}

	public WorkSearchQuery addWorkClientFilter() {
		if (isNotEmpty(request.getClients())) {
			List<Long> clientIds = extract(request.getClients(), on(DashboardClient.class).getClientId());
			this.addFilterQuery(SearchQueryCreatorUtil.createFilterQuery(WorkSearchableFields.CLIENT_COMPANY_ID, clientIds));
		}
		return this;
	}

	public WorkSearchQuery addWorkProjectFilter() {
		if (isNotEmpty(request.getProjects())) {
			List<Long> projectIds = extract(request.getProjects(), on(DashboardProject.class).getProjectId());
			this.addFilterQuery(SearchQueryCreatorUtil.createFilterQuery(WorkSearchableFields.PROJECT_ID, projectIds));
		}
		return this;
	}

	public WorkSearchQuery addWorkInternalOwnerFilter() {
		if (isNotEmpty(request.getInternalOwners())) {
			List<Long> internalOwnerIds = extract(request.getInternalOwners(), on(DashboardInternalOwner.class).getUserId());
			this.addFilterQuery(SearchQueryCreatorUtil.createFilterQuery(WorkSearchableFields.BUYER_USER_ID, internalOwnerIds));
		}
		return this;
	}

	public WorkSearchQuery addWorkAssignedResourceFilter() {
		if (isNotEmpty(request.getAssignedResources())) {
			List<Long> assignedResourceIds = select(extract(request.getAssignedResources(), on(DashboardResource.class).getResourceId()), greaterThan(0L));
			List<Long> assignedVendorIds = select(extract(request.getAssignedResources(), on(DashboardResource.class).getResourceCompanyId()), greaterThan(0L));
			if (CollectionUtils.isNotEmpty(assignedResourceIds)) {
				this.addFilterQuery(SearchQueryCreatorUtil.createFilterQuery(WorkSearchableFields.ASSIGNED_RESOURCE_ID, assignedResourceIds));
			}
			if (CollectionUtils.isNotEmpty(assignedVendorIds)) {
				this.addFilterQuery(SearchQueryCreatorUtil.createFilterQuery(WorkSearchableFields.ASSIGNED_RESOURCE_COMPANY_ID, assignedVendorIds));
			}
		}
		return this;
	}

	public WorkSearchQuery addDispatcherFilter() {
		if (isNotEmpty(request.getDispatchers())) {
			this.addFilterQuery(SearchQueryCreatorUtil.createFilterQuery(WorkSearchableFields.DISPATCHER_ID, request.getDispatchers()));
		}
		return this;
	}

	public WorkSearchQuery addBundleFilter() {
		if (isNotEmpty(request.getBundles())) {
			List<Long> bundleIds = extract(request.getBundles(), on(DashboardBundle.class).getId());
			this.addFilterQuery(SearchQueryCreatorUtil.createFilterQuery(WorkSearchableFields.PARENT_ID, bundleIds));
		}
		return this;
	}

	public WorkSearchQuery addFollowingAssignmentsFilter() {
		if (isNotEmpty(request.getFollowerIds())) {
			this.addFilterQuery(SearchQueryCreatorUtil.createFilterQuery(WorkSearchableFields.FOLLOWER_IDS, request.getFollowerIds()));
		}
		return this;
	}

	public WorkSearchQuery addDecisionFlowsFilter() {
		if (request.isFilterPendingMultiApprovals() && isNotEmpty(request.getDecisionFlowUuids())) {
			SearchQueryCreatorUtil.addFilterQueryStr(this, WorkSearchableFields.EXTERNAL_UNIQUE_IDS, request.getDecisionFlowUuids());
		}
		return this;
	}

	public WorkSearchQuery addSort() {
		SolrQuery.ORDER order = SolrQuery.ORDER.desc;
		WorkSearchableFields sortField = WorkSearchableFields.WORK_STATUS_TYPE_CODE;
		if (request.getSortBy() != null) {
			if (request.getSortDirection().equals(SearchSortDirection.ASCENDING)) {
				order = SolrQuery.ORDER.asc;
			}

			switch (request.getSortBy()) {
				case CREATED_ON:
					sortField = WorkSearchableFields.CREATED_DATE;
					break;
				case SCHEDULED_FROM:
					sortField = WorkSearchableFields.SCHEDULE_FROM_DATE;
					break;
				case APPROVED_DATE:
					sortField = WorkSearchableFields.APPROVED_DATE;
					break;
				case CLIENT:
					sortField = WorkSearchableFields.CLIENT_COMPANY_NAME;
					break;
				case COMPLETED_DATE:
					sortField = WorkSearchableFields.COMPLETED_DATE;
					break;
				case LAST_MODIFIED_DATE:
					sortField = WorkSearchableFields.LAST_MODIFIED_DATE;
					break;
				case PAID_DATE:
					sortField = WorkSearchableFields.PAID_DATE;
					break;
				case SENT_DATE:
					sortField = WorkSearchableFields.SEND_DATE;
					break;
				case TITLE:
					sortField = WorkSearchableFields.TITLE;
					break;
				case STATE:
					sortField = WorkSearchableFields.STATE;
					break;
				case DUE_DATE:
					sortField = WorkSearchableFields.DUE_DATE;
			}
			this.addSort(new SolrQuery.SortClause(sortField.getName(), order));

		} else {
			this.addSort(new SolrQuery.SortClause("workStatusTypeCode", SolrQuery.ORDER.asc));
			this.addSort(new SolrQuery.SortClause("score", SolrQuery.ORDER.desc));
		}
		return this;
	}


	public WorkSearchQuery addWorkSearchStaticQueryOptions(WorkSearchTransientData data) {
		this.setFacet(true);
		this.setFacetMissing(true);
		this.addFacetField("{!ex=" + WorkSearchableFields.SEARCHABLE_WORK_STATUS_TYPE_CODE.getName() + ",workSubStatusId,workStatus,resourceWorkStatus,applicantIds,externalUniqueIds}"
				+ WorkSearchableFields.SEARCHABLE_WORK_STATUS_TYPE_CODE.getName());
		this.addFacetField("{!ex=" + WorkSearchableFields.BUYER_LABELS_ID_DESCRIPTION.getName() + ",workSubStatusId,workStatus,resourceWorkStatus,applicantIds,externalUniqueIds}"
				+ WorkSearchableFields.BUYER_LABELS_ID_DESCRIPTION.getName());
		this.addFacetField("{!ex=" + WorkSearchableFields.BUYER_LABELS_ID.getName() + ",workSubStatusId,workStatus,resourceWorkStatus,applicantIds,externalUniqueIds}"
				+ WorkSearchableFields.BUYER_LABELS_ID.getName());
		this.addFacetField("{!ex=" + WorkSearchableFields.EXTERNAL_UNIQUE_IDS.getName() + ",workSubStatusId,workStatus,resourceWorkStatus,applicantIds}"
			+ WorkSearchableFields.EXTERNAL_UNIQUE_IDS.getName());

		this.setFacetPrefix(WorkSearchableFields.BUYER_LABELS_ID_DESCRIPTION.getName(), data.getCurrentUser().getCompanyId() + "_");
		this.setFacetPrefix(WorkSearchableFields.BUYER_LABELS_WORK_STATUS_ID_DESCRIPTION.getName(), data.getCurrentUser().getCompanyId() + "_");
		this.set(createFacetSortString(WorkSearchableFields.BUYER_LABELS_ID_DESCRIPTION), "index");
		this.addFacetQuery("*:*");

		if (request.isIncludeLabelDrilldownFacet()) {
			this.addFacetField("{!ex=" + WorkSearchableFields.BUYER_LABELS_WORK_STATUS_ID_DESCRIPTION.getName() + ",workSubStatusId,workStatus}"
					+ WorkSearchableFields.BUYER_LABELS_WORK_STATUS_ID_DESCRIPTION.getName());
		}

		if (WorkSearchRequestUserType.RESOURCE.equals(request.getWorkSearchRequestUserType())) {
			this.addFacetField(
				"{!ex=" + WorkSearchableFields.RESOURCE_WORK_STATUS_TYPE_CODE.getName() +
					",workSubStatusId,workStatus,resourceWorkStatus,applicantIds}" + WorkSearchableFields.RESOURCE_WORK_STATUS_TYPE_CODE.getName()
			);
			this.addFacetField(
				"{!ex=" + WorkSearchableFields.APPLICANT_IDS.getName() +
					",workSubStatusId,workStatus,resourceWorkStatus,applicantIds}" + WorkSearchableFields.APPLICANT_IDS.getName()
			);
		}
		return this;
	}

	private String extractKeyWord(WorkSearchKeyword searchKeyword, String keywords) {
		if (StringUtils.contains(keywords, searchKeyword.qString)) {
			String filterValue = StringUtils.substringAfter(keywords, searchKeyword.qString);
			if (StringUtils.isNotBlank(filterValue)) {
				for (WorkSearchKeyword otherWorkSearchKey : WorkSearchKeyword.values()) {
					//Is there anything after this value?
					if (filterValue.contains(otherWorkSearchKey.qString)) {
						filterValue = StringUtils.substringBefore(filterValue, otherWorkSearchKey.qString);
					}
				}
				return filterValue;
			}
		}
		return StringUtils.EMPTY;
	}

	public WorkSearchQuery addWorkKeywordFilter(WorkSearchTransientData transientData) {
		String effectiveKeywords = encode(request.getKeyword());
		if (StringUtils.isBlank(effectiveKeywords)) {
			return this;
		}

		String stringQuery = effectiveKeywords;
		Map<WorkSearchKeyword, String> keywordStringMap = Maps.newHashMap();

		for (WorkSearchKeyword searchKeyword : WorkSearchKeyword.values()) {
			String extractedWord = extractKeyWord(searchKeyword, stringQuery);
			if (StringUtils.isNotBlank(extractedWord)) {
				keywordStringMap.put(searchKeyword, extractedWord);
				stringQuery = stringQuery.replace(searchKeyword.qString, "");
				stringQuery = stringQuery.replace(extractedWord, "");
			}
		}

		if (MapUtils.isEmpty(keywordStringMap)) {
			if (StringUtils.isNotBlank(stringQuery)) {
				this.setQuery(stringQuery);
			}
		} else {
			return addWorkKeywordOptions(keywordStringMap, transientData.getCurrentUser().getCompanyId());
		}
		return this;
	}

	WorkSearchQuery addWorkKeywordOptions(Map<WorkSearchKeyword, String> keywordStringMap, Long companyId) {
		Assert.notNull(keywordStringMap);
		List<String> queryStrings = Lists.newArrayList();
		for (Map.Entry<WorkSearchKeyword, String> entry : keywordStringMap.entrySet()) {
			queryStrings.add(String.format("%s:%s", entry.getKey().schemaFieldName, entry.getValue()));

			switch (entry.getKey()) {
				case PROJECT:
					break;
				case ASSIGNED_TO:
					this.addFilterQuery("(-workStatusTypeCode:draft AND -workStatusTypeCode:sent AND -workStatusTypeCode:void)");
					this.addSort(new SolrQuery.SortClause("workStatusTypeCode", SolrQuery.ORDER.asc));
					break;
				case WORK_STATUS:
					break;
				default:
					break;
			}
		}
		this.addFilterQuery(String.format("companyIdString:%s", companyId));
		this.setQuery("(" + SearchUtilities.joinWithAND(queryStrings) + ")");
		return this;
	}

	public WorkSearchQuery addLocationQuery(String locationBoostFunction) {
		NamedList<String> list = new NamedList<>();
		list.add("bf", locationBoostFunction);
		this.add(SolrParams.toSolrParams(list));

		return this;
	}
}
