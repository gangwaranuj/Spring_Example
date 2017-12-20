package com.workmarket.search.request.work;

import com.google.common.collect.Maps;
import com.workmarket.data.solr.repository.WorkSearchableFields;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.search.request.SearchRequest;
import com.workmarket.search.request.SearchSortDirection;
import com.workmarket.search.response.work.DashboardBundle;
import com.workmarket.search.response.work.DashboardClient;
import com.workmarket.search.response.work.DashboardInternalOwner;
import com.workmarket.search.response.work.DashboardProject;
import com.workmarket.search.response.work.DashboardResource;
import com.workmarket.search.response.work.DashboardStatusFilter;
import com.workmarket.search.response.work.WorkMilestoneFilter;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class WorkSearchRequest extends SearchRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private DateRange dateRange;
	private Set<DashboardClient> clients;
	private Set<DashboardProject> projects;
	private Set<DashboardInternalOwner> internalOwners;
	private Set<DashboardResource> assignedResources;
	private Set<Long> dispatchers;
	private Set<DashboardBundle> bundles;

	//Sort
	private WorkSearchSortType sortBy;

	private String userNumber;
	private boolean showAllAtCompany = true;
	private boolean includeCounts = true;
	private boolean includeLabelDrilldownFacet = false;
	private boolean isFullSelectAll = false;
	private boolean ignoreVirtual = false;
	private boolean isDispatcher = false;
	private boolean filterPendingMultiApprovals = false;

	private DashboardStatusFilter statusFilter;
	private DashboardStatusFilter subStatusFilter;
	private List<DashboardStatusFilter> subStatusMultiFilter;
	private Set<WorkStatusType> workStatusFilters;

	private WorkMilestoneFilter workMilestone;
	private WorkSearchRequestUserType workSearchRequestUserType;

	private Set<Long> followerIds;
	private final Map<String, String> filterQueries = Maps.newHashMap();
	private Set<WorkSearchableFields> facetFields;
	private List<Long> blockedCompanyIds;
	private boolean isMobile = false;
	private WorkSearchType workSearchType = WorkSearchType.UNKNOWN;

	private Set<String> decisionFlowUuids;


	public WorkSearchRequest() {}

	public WorkSearchRequest(String userNumber) {
		this.userNumber = userNumber;
	}

	public WorkSearchRequest(int startRow, int pageSize, boolean includeCounts) {
		setStartRow(startRow);
		setPageSize(pageSize);
		this.includeCounts = includeCounts;
	}

	public DateRange getDateRange() {
		return this.dateRange;
	}

	public WorkSearchRequest setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
		return this;
	}

	public boolean isSetDateRange() {
		return this.dateRange != null;
	}

	public Set<DashboardClient> getClients() {
		return this.clients;
	}

	public WorkSearchRequest setClients(Set<DashboardClient> clients) {
		this.clients = clients;
		return this;
	}

	public boolean isSetClient() {
		return this.clients != null;
	}

	public Set<DashboardProject> getProjects() {
		return this.projects;
	}

	public WorkSearchRequest setProjects(Set<DashboardProject> projects) {
		this.projects = projects;
		return this;
	}

	public boolean isSetProject() {
		return this.projects != null;
	}

	public Set<DashboardInternalOwner> getInternalOwners() {
		return this.internalOwners;
	}

	public WorkSearchRequest setInternalOwners(Set<DashboardInternalOwner> internalOwners) {
		this.internalOwners = internalOwners;
		return this;
	}

	public boolean isSetInternalOwner() {
		return this.internalOwners != null;
	}

	public Set<DashboardResource> getAssignedResources() {
		return this.assignedResources;
	}

	public WorkSearchRequest setAssignedResources(Set<DashboardResource> assignedResources) {
		this.assignedResources = assignedResources;
		return this;
	}

	public boolean isSetAssignedResources() {
		return this.assignedResources != null;
	}

	public boolean isSetDispatchers() {
		return this.dispatchers != null;
	}

	public Set<DashboardBundle> getBundles() { return this.bundles; }
	public WorkSearchRequest setBundles(Set<DashboardBundle> bundles) {
		this.bundles = bundles;
		return this;
	}
	public boolean isSetBundles() {
		return this.bundles != null;
	}

	public WorkSearchRequest addFilterQueries(Map<String, String> filterQueries) {
		this.filterQueries.putAll(filterQueries);
		return this;
	}

	public Map<String, String> getFilterQueries() {
		return filterQueries;
	}

	public WorkSearchSortType getSortBy() {
		return this.sortBy;
	}

	public WorkSearchRequest setSortBy(WorkSearchSortType sortBy) {
		this.sortBy = sortBy;
		return this;
	}

	public boolean isSetSortBy() {
		return this.sortBy != null;
	}

	public WorkSearchRequest setSortDirection(SearchSortDirection sortDirection) {
		super.setSortDirection(sortDirection);
		return this;
	}

	public boolean isShowAllAtCompany() {
		return this.showAllAtCompany;
	}

	public WorkSearchRequest setShowAllAtCompany(boolean showAllAtCompany) {
		this.showAllAtCompany = showAllAtCompany;
		return this;
	}

	public Set<Long> getDispatchers() {
		return this.dispatchers;
	}

	public WorkSearchRequest setDispatchers(Set<Long> dispatchers) {
		this.dispatchers = dispatchers;
		return this;
	}

	public boolean isDispatcher() {
		return isDispatcher;
	}

	public void setDispatcher(boolean isDispatcher) {
		this.isDispatcher = isDispatcher;
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public WorkSearchRequest setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public WorkSearchRequest setStartRow(int startRow) {
		super.setStartRow(startRow);
		return this;
	}

	public Set<WorkStatusType> getWorkStatusFilters() {
		return workStatusFilters;
	}

	public void setWorkStatusFilters(Set<WorkStatusType> workStatusFilters) {
		this.workStatusFilters = workStatusFilters;
	}

	public WorkSearchRequest setPageSize(int pageSize) {
		super.setPageSize(pageSize);
		return this;
	}

	public DashboardStatusFilter getStatusFilter() {
		return this.statusFilter;
	}

	public WorkSearchRequest setStatusFilter(DashboardStatusFilter statusFilter) {
		this.statusFilter = statusFilter;
		return this;
	}

	public boolean isSetStatusFilter() {
		return this.statusFilter != null;
	}

	public boolean isIncludeCounts() {
		return this.includeCounts;
	}

	public WorkSearchRequest setIncludeCounts(boolean includeCounts) {
		this.includeCounts = includeCounts;
		return this;
	}

	public DashboardStatusFilter getSubStatusFilter() {
		return this.subStatusFilter;
	}

	public WorkSearchRequest setSubStatusFilter(DashboardStatusFilter subStatusFilter) {
		this.subStatusFilter = subStatusFilter;
		return this;
	}

	public boolean isSetSubStatusFilter() {
		return this.subStatusFilter != null;
	}

	public WorkSearchRequest setWorkMilestoneFilter(WorkMilestoneFilter wmFilter) {
		this.workMilestone = wmFilter;
		return this;
	}

	public WorkMilestoneFilter getWorkMilestoneFilter() {
		return this.workMilestone;
	}

	public WorkSearchRequestUserType getWorkSearchRequestUserType() {
		return workSearchRequestUserType;
	}

	public WorkSearchRequest setWorkSearchRequestUserType(WorkSearchRequestUserType workSearchRequestUserType) {
		this.workSearchRequestUserType = workSearchRequestUserType;
		return this;
	}

	public boolean isIgnoreVirtual() {
		return ignoreVirtual;
	}

	public void setIgnoreVirtual(boolean ignoreVirtual) {
		this.ignoreVirtual = ignoreVirtual;
	}

	public boolean isIncludeLabelDrilldownFacet() {
		return includeLabelDrilldownFacet;
	}

	public WorkSearchRequest setIncludeLabelDrilldownFacet(boolean includeLabelDrilldownFacet) {
		this.includeLabelDrilldownFacet = includeLabelDrilldownFacet;
		return this;
	}

	public List<DashboardStatusFilter> getSubStatusMultiFilter() {
		return subStatusMultiFilter;
	}

	public void setSubStatusMultiFilter(List<DashboardStatusFilter> subStatusMultiFilter) {
		this.subStatusMultiFilter = subStatusMultiFilter;
	}

	public boolean isSetSubStatusMultiFilter() {
		return isNotEmpty(subStatusMultiFilter);
	}

	public boolean hasProtectedWorkSearchDataFilter() {
		if (this.isSetSubStatusFilter() && this.getSubStatusFilter().isSetStatusCode()) {
			return true;
		}
		if (isNotEmpty(this.getClients())) {
			return true;
		}
		if (isNotEmpty(this.getInternalOwners())) {
			return true;
		}
		if (isNotEmpty(this.getAssignedResources())) {
			return true;
		}
		if (isNotEmpty(this.getDispatchers())) {
			return true;
		}
		return isSetSubStatusMultiFilter();
	}

	public boolean hasWorkStatusFilter() {
		return (this.isSetStatusFilter() && this.getStatusFilter().isSetStatusCode());
	}

	public boolean isAvailableSearch() {
		return this.hasWorkStatusFilter() && WorkStatusType.AVAILABLE.equals(this.getStatusFilter().getStatusCode());
	}

	public Set<Long> getFollowerIds() {
		return followerIds;
	}

	public WorkSearchRequest setFollowerIds(Set<Long> followedAssignments) {
		this.followerIds = followedAssignments;
		return this;
	}

	public boolean isFullSelectAll() {
		return isFullSelectAll;
	}

	public WorkSearchRequest setFullSelectAll(boolean fullSelectAll) {
		isFullSelectAll = fullSelectAll;
		return this;
	}

	public Set<WorkSearchableFields> getFacetFields() {
		return facetFields;
	}

	public WorkSearchRequest setFacetFields(Set<WorkSearchableFields> facetFields) {
		this.facetFields = facetFields;
		return this;
	}

	public List<Long> getBlockedCompanyIds() {
		return blockedCompanyIds;
	}

	public void setBlockedCompanyIds(List<Long> blockedCompanyIds) {
		this.blockedCompanyIds = blockedCompanyIds;
	}

	public boolean isMobile() {
		return isMobile;
	}

	public WorkSearchRequest setMobile(boolean mobile) {
		isMobile = mobile;
		return this;
	}

	public WorkSearchType getWorkSearchType() {
		return workSearchType;
	}

	public WorkSearchRequest setWorkSearchType(WorkSearchType workSearchType) {
		this.workSearchType = workSearchType;
		return this;
	}

	public Set<String> getDecisionFlowUuids() {
		return decisionFlowUuids;
	}

	public WorkSearchRequest setDecisionFlowUuids(final Set<String> decisionFlowUuids) {
		this.decisionFlowUuids = decisionFlowUuids;
		return this;
	}

	public boolean isFilterPendingMultiApprovals() {
		return filterPendingMultiApprovals;
	}

	public WorkSearchRequest setFilterPendingMultiApprovals(final boolean filterPendingMultiApprovals) {
		this.filterPendingMultiApprovals = filterPendingMultiApprovals;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkSearchRequest)
			return this.equals((WorkSearchRequest) that);
		return false;
	}

	private boolean equals(WorkSearchRequest that) {
		if (that == null)
			return false;

		boolean this_present_dateRange = this.isSetDateRange();
		boolean that_present_dateRange = that.isSetDateRange();
		if (this_present_dateRange || that_present_dateRange) {
			if (!(this_present_dateRange && that_present_dateRange))
				return false;
			if (!this.dateRange.equals(that.dateRange))
				return false;
		}

		boolean this_present_client = this.isSetClient();
		boolean that_present_client = that.isSetClient();
		if (this_present_client || that_present_client) {
			if (!(this_present_client && that_present_client))
				return false;
			if (!this.clients.equals(that.clients))
				return false;
		}

		boolean this_present_project = this.isSetProject();
		boolean that_present_project = that.isSetProject();
		if (this_present_project || that_present_project) {
			if (!(this_present_project && that_present_project))
				return false;
			if (!this.projects.equals(that.projects))
				return false;
		}

		boolean this_present_internalOwner = this.isSetInternalOwner();
		boolean that_present_internalOwner = that.isSetInternalOwner();
		if (this_present_internalOwner || that_present_internalOwner) {
			if (!(this_present_internalOwner && that_present_internalOwner))
				return false;
			if (!this.internalOwners.equals(that.internalOwners))
				return false;
		}

		boolean this_present_dispatchers = this.isSetDispatchers();
		boolean that_present_dispatchers = that.isSetDispatchers();
		if (this_present_dispatchers || that_present_dispatchers) {
			if (!(this_present_dispatchers && that_present_dispatchers))
				return false;
			if (!this.dispatchers.equals(that.dispatchers))
				return false;
		}

		boolean this_present_assignedResources = this.isSetAssignedResources();
		boolean that_present_assignedResources = that.isSetAssignedResources();
		if (this_present_assignedResources || that_present_assignedResources) {
			if (!(this_present_assignedResources && that_present_assignedResources))
				return false;
			if (!this.assignedResources.equals(that.assignedResources))
				return false;
		}

		boolean this_present_sortBy = this.isSetSortBy();
		boolean that_present_sortBy = that.isSetSortBy();
		if (this_present_sortBy || that_present_sortBy) {
			if (!(this_present_sortBy && that_present_sortBy))
				return false;
			if (!this.sortBy.equals(that.sortBy))
				return false;
		}

		boolean this_present_sortDirection = this.isSetSortDirection();
		boolean that_present_sortDirection = that.isSetSortDirection();
		if (this_present_sortDirection || that_present_sortDirection) {
			if (!(this_present_sortDirection && that_present_sortDirection))
				return false;
			if (!this.getSortDirection().equals(that.getSortDirection()))
				return false;
		}

		if (this.showAllAtCompany != that.showAllAtCompany) {
			return false;
		}

		if (this.isDispatcher != that.isDispatcher()) {
			return false;
		}

		boolean this_present_userNumber = this.isSetUserNumber();
		boolean that_present_userNumber = that.isSetUserNumber();
		if (this_present_userNumber || that_present_userNumber) {
			if (!(this_present_userNumber && that_present_userNumber))
				return false;
			if (!this.userNumber.equals(that.userNumber))
				return false;
		}

		boolean this_present_startRow = true;
		boolean that_present_startRow = true;
		if (this_present_startRow || that_present_startRow) {
			if (!(this_present_startRow && that_present_startRow))
				return false;
			if (this.getStartRow() != that.getStartRow())
				return false;
		}

		boolean this_present_pageSize = true;
		boolean that_present_pageSize = true;
		if (this_present_pageSize || that_present_pageSize) {
			if (!(this_present_pageSize && that_present_pageSize))
				return false;
			if (this.getPageSize() != that.getPageSize())
				return false;
		}

		boolean this_present_statusFilter = this.isSetStatusFilter();
		boolean that_present_statusFilter = that.isSetStatusFilter();
		if (this_present_statusFilter || that_present_statusFilter) {
			if (!(this_present_statusFilter && that_present_statusFilter))
				return false;
			if (!this.statusFilter.equals(that.statusFilter))
				return false;
		}

		boolean this_present_includeCounts = true;
		boolean that_present_includeCounts = true;
		if (this_present_includeCounts || that_present_includeCounts) {
			if (!(this_present_includeCounts && that_present_includeCounts))
				return false;
			if (this.includeCounts != that.includeCounts)
				return false;
		}

		boolean this_present_subStatusFilter = this.isSetSubStatusFilter();
		boolean that_present_subStatusFilter = that.isSetSubStatusFilter();
		if (this_present_subStatusFilter || that_present_subStatusFilter) {
			if (!(this_present_subStatusFilter && that_present_subStatusFilter))
				return false;
			if (!this.subStatusFilter.equals(that.subStatusFilter))
				return false;
		}

		if (!this.filterQueries.equals(that.filterQueries)) {
			return false;
		}

		if (!this.facetFields.equals(that.facetFields)) {
			return false;
		}

		if (this.isMobile != that.isMobile) {
			return false;
		}

		if (this.workSearchType != that.workSearchType) {
			return false;

		}

		if (! this.decisionFlowUuids.equals(that.decisionFlowUuids)) {
			return false;
		}

		if (this.filterPendingMultiApprovals != that.filterPendingMultiApprovals) {
			return false;
		}

		return this.blockedCompanyIds.equals(that.blockedCompanyIds);

	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_dateRange = (isSetDateRange());
		builder.append(present_dateRange);
		if (present_dateRange)
			builder.append(dateRange);

		boolean present_client = (isSetClient());
		builder.append(present_client);
		if (present_client)
			builder.append(clients);

		boolean present_project = (isSetProject());
		builder.append(present_project);
		if (present_project)
			builder.append(projects);

		boolean present_internalOwner = (isSetInternalOwner());
		builder.append(present_internalOwner);
		if (present_internalOwner)
			builder.append(internalOwners);

		boolean present_dispatchers = (isSetDispatchers());
		builder.append(present_dispatchers);
		if (present_dispatchers) {
			builder.append(dispatchers);
		}

		boolean present_assignedResources = (isSetAssignedResources());
		builder.append(present_assignedResources);
		if (present_assignedResources) {
			builder.append(assignedResources);
		}

		boolean present_sortBy = (isSetSortBy());
		builder.append(present_sortBy);
		if (present_sortBy)
			builder.append(sortBy.getValue());

		boolean present_sortDirection = (isSetSortDirection());
		builder.append(present_sortDirection);
		if (present_sortDirection)
			builder.append(getSortDirection().getValue());

		builder
			.append(true)
			.append(showAllAtCompany)
			.append(isDispatcher);

		boolean present_userNumber = (isSetUserNumber());
		builder.append(present_userNumber);
		if (present_userNumber)
			builder.append(userNumber);

		boolean present_startRow = true;
		builder.append(present_startRow);
		if (present_startRow)
			builder.append(getStartRow());

		boolean present_pageSize = true;
		builder.append(present_pageSize);
		if (present_pageSize)
			builder.append(getPageSize());

		boolean present_statusFilter = (isSetStatusFilter());
		builder.append(present_statusFilter);
		if (present_statusFilter)
			builder.append(statusFilter);

		boolean present_includeCounts = true;
		builder.append(present_includeCounts);
		if (present_includeCounts)
			builder.append(includeCounts);

		boolean present_subStatusFilter = (isSetSubStatusFilter());
		builder.append(present_subStatusFilter);
		if (present_subStatusFilter)
			builder.append(subStatusFilter);

		builder
			.append(filterQueries)
			.append(facetFields)
			.append(blockedCompanyIds)
			.append(workSearchType)
			.append(decisionFlowUuids)
			.append(filterPendingMultiApprovals)
			.append(isMobile);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		return "WorkSearchRequest{" +
			"assignedResources=" + assignedResources +
			", dateRange=" + dateRange +
			", clients=" + clients +
			", projects=" + projects +
			", internalOwners=" + internalOwners +
			", sortBy=" + sortBy +
			", sortDirection=" + getSortDirection() +
			", showAllAtCompany=" + showAllAtCompany +
			", isDispatcher=" + isDispatcher +
			", userNumber='" + userNumber + '\'' +
			", startRow=" + getStartRow() +
			", pageSize=" + getPageSize() +
			", statusFilter=" + statusFilter +
			", includeCounts=" + includeCounts +
			", subStatusFilter=" + subStatusFilter +
			", workMilestone=" + workMilestone +
			", keyword='" + getKeyword() + '\'' +
			", workSearchRequestUserType=" + workSearchRequestUserType +
			", includeLabelDrilldownFacet=" + includeLabelDrilldownFacet +
			", filterQueries=" + filterQueries +
			", facetFields=" + facetFields +
			", blockedCompanyIds=" + blockedCompanyIds +
			", decisionFlowUuids=" + decisionFlowUuids +
			", filterMultiApproval=" + Boolean.toString(filterPendingMultiApprovals) +
			", isMobile=" + Boolean.toString(isMobile) +
			", workSearchType=" + workSearchType.name() +
			'}';
	}

}
