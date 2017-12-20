package com.workmarket.domains.work.service.dashboard;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.business.decision.gen.Messages.Decision;
import com.workmarket.business.decision.gen.Messages.GetDoableDecisionsRequest;
import com.workmarket.dao.UserDAO;
import com.workmarket.data.report.work.WorkSubStatusTypeReportRow;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.response.work.DashboardResponse;
import com.workmarket.search.response.work.DashboardResponseSidebar;
import com.workmarket.search.response.work.DashboardResultList;
import com.workmarket.search.response.work.DashboardStatus;
import com.workmarket.search.response.work.DashboardStatusFilter;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.dto.WorkAggregatesDTO;
import com.workmarket.service.decisionflow.DecisionFlowService;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.utility.BeanUtilities;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Service
public class WorkDashboardServiceImpl implements WorkDashboardService {

	private static final Log logger = LogFactory.getLog(WorkDashboardServiceImpl.class);

	@Autowired private WorkSearchService workSearchService;
	@Autowired private UserDAO userDAO;
	@Autowired private WorkDashboardResultParser workDashboardResultParser;
	@Autowired private DashboardResultService dashboardResultService;
	@Autowired private DecisionFlowService decisionFlowService;

	@Override
	public DashboardResponseSidebar generateWorkDashboardSidebar(WorkSearchRequest request) {
		final WorkSearchResponse workSearchResponse = executeWorkSearchForDashboard(request);
		return createSidebar(workSearchResponse);
	}

	@Override
	public DashboardResponse getDashboard(WorkSearchRequest request) {
		WorkSearchResponse workSearchResponse = executeWorkSearchForDashboard(request);
		DashboardResultList results = createResultList(workSearchResponse);

		dashboardResultService.decorateDashBoardResultFlags(request, workSearchResponse, results);

		DashboardResponseSidebar sidebar = createSidebar(workSearchResponse);
		int numOfPendingApprovals = dashboardResultService.getPendingApprovalsCount(request, workSearchResponse);
		if (numOfPendingApprovals > 0) {
			parseStatus(sidebar, Maps.immutableEntry(WorkStatusType.PENDING_MULTI_APPROVALS, numOfPendingApprovals));
		}
		DashboardResponse dashboardResponse = new DashboardResponse();
		dashboardResponse.setSidebar(sidebar);
		dashboardResponse.setDashboardResultList(results);
		dashboardResponse.setResults(workSearchResponse.getResults()); // API uses this set of results
		return dashboardResponse;
	}

	private WorkSearchResponse executeWorkSearchForDashboard(WorkSearchRequest request) {
		final User user = userDAO.findUserByUserNumber(request.getUserNumber(), false);
		final Set<String> pendingApprovalUuids = getDoableDecisions(user.getUuid());
		request.setDecisionFlowUuids(pendingApprovalUuids);

		if (request.isAvailableSearch()) {
			request.setStatusFilter(new DashboardStatusFilter().setStatusCode(WorkStatusType.ALL));
			final WorkSearchResponse workSearchResponse = workSearchService.searchAllWorkByUserId(user.getId(), request);

			final WorkSearchRequest availableSearchRequest = copyWorkSearchRequest(request);
			availableSearchRequest.setStatusFilter(new DashboardStatusFilter().setStatusCode(WorkStatusType.AVAILABLE));
			WorkSearchResponse invitedResourceWorkSearchResponse = workSearchService.searchAllWorkByUserId(user.getId(), availableSearchRequest);

			workSearchResponse.setResults(invitedResourceWorkSearchResponse.getResults());
			workSearchResponse.setResultsLimit(invitedResourceWorkSearchResponse.getResultsLimit());
			workSearchResponse.setTotalResultsCount(invitedResourceWorkSearchResponse.getTotalResultsCount());

			return workSearchResponse;
		}

		return workSearchService.searchAllWorkByUserId(user.getId(), request);
	}

	private DashboardResultList createResultList(WorkSearchResponse workSearchResponse) {
		DashboardResultList resultList = new DashboardResultList();
		resultList.setLastUpdated(System.currentTimeMillis());
		resultList.setPageNumber(workSearchResponse.getCurrentPage());
		resultList.setTotalNumberOfPages(workSearchResponse.getNumberOfPages());
		workDashboardResultParser.parseResult(workSearchResponse.getResults(), resultList);
		resultList.setTotalResults(workSearchResponse.getTotalResultsCount());

		List<String> resultIds = Lists.newArrayListWithCapacity(resultList.getResultsSize());
		for (SolrWorkData result : workSearchResponse.getResults()) {
			resultIds.add(String.valueOf(result.getWorkId()));
		}
		resultList.setResultIds(resultIds);
		return resultList;
	}

	private DashboardResponseSidebar createSidebar(WorkSearchResponse workSearchResponse) {
		return createSidebar(workSearchResponse.getAggregates(), workSearchResponse.getAggregatesBuyerWorkStatusDrillDownBySubStatus());
	}

	private DashboardResponseSidebar createSidebar(
			WorkAggregatesDTO aggregatesBuyer,
			Map<String, WorkAggregatesDTO> aggregatesBuyerWorkStatusDrillDownBySubStatus) {

		DashboardResponseSidebar sidebar = new DashboardResponseSidebar();
		// buyer statuses
		if (aggregatesBuyer != null) {
			for (Entry<String, Integer> entry : aggregatesBuyer.getCounts().entrySet()) {
				parseStatus(sidebar, entry);
			}

			// buyer sub-statuses
			for (Entry<Long, WorkSubStatusTypeReportRow> entry : aggregatesBuyer.getWorkSubStatusCounts().entrySet()) {
				parseSubStatus(sidebar, entry, WorkSearchRequestUserType.CLIENT);
			}
			// buyer by status and sub status drill-down
			parseBuyerStatusMapBySubStatus(sidebar, aggregatesBuyerWorkStatusDrillDownBySubStatus);
		}
		return sidebar;
	}

	private void parseBuyerStatusMapBySubStatus(DashboardResponseSidebar sidebar, Map<String, WorkAggregatesDTO> aggregatesBuyerWorkStatusDrillDownBySubStatus) {
		if (MapUtils.isEmpty(aggregatesBuyerWorkStatusDrillDownBySubStatus)) {
			return;
		}
		Map<String, List<DashboardStatus>> resultStatusMap = Maps.newLinkedHashMap();

		for (Map.Entry<String, WorkAggregatesDTO> aggregatesBuyerSet : aggregatesBuyerWorkStatusDrillDownBySubStatus.entrySet()) {
			String statusName = aggregatesBuyerSet.getKey();
			Map<Long, WorkSubStatusTypeReportRow> workSubStatusAggregates = aggregatesBuyerSet.getValue().getWorkSubStatusCounts();
			if (!resultStatusMap.containsKey(statusName)) {
				List<DashboardStatus> subStatusList = Lists.newArrayList();
				resultStatusMap.put(statusName, subStatusList);
			}
			for (Map.Entry<Long, WorkSubStatusTypeReportRow> entryPair : workSubStatusAggregates.entrySet()) {
				Long workSubStatusId = entryPair.getKey();
				if (workSubStatusId != null) {
					WorkSubStatusTypeReportRow entry = entryPair.getValue();
					DashboardStatus subStatus = new DashboardStatus();
					subStatus.setStatusId(workSubStatusId.toString());
					subStatus.setStatusCount(entry.getCount());
					subStatus.setStatusName(entry.getCode());
					subStatus.setStatusDescription(entry.getDescription());
					subStatus.setColorRgb(entry.getColorRgb());
					subStatus.setDashboardDisplayType(entry.getDashboardDisplayType());
					resultStatusMap.get(statusName).add(subStatus);
				}
			}
		}

		// Push the result Map to the thrift response
		for (Entry<String, List<DashboardStatus>> entry : resultStatusMap.entrySet()) {
			sidebar.putToDashboardClientSubStatusesByStatus(entry.getKey(), entry.getValue());
		}
	}

	private void parseStatus(DashboardResponseSidebar sidebar, Entry<String, Integer> entry) {
		DashboardStatus status = new DashboardStatus();
		status.setStatusId(entry.getKey());
		status.setStatusCount(entry.getValue());
		status.setStatusName(entry.getKey());
		sidebar.putToDashboardStatuses(status.getStatusId(), status);
	}

	private void parseSubStatus(DashboardResponseSidebar sidebar, Map.Entry<Long, WorkSubStatusTypeReportRow> entry, WorkSearchRequestUserType workSearchRequestUserType) {

		if (entry == null || entry.getKey() == null) { return; }
		WorkSubStatusTypeReportRow workSubStatusTypeReportRow = entry.getValue();
		DashboardStatus status = new DashboardStatus();

		status.setStatusId(entry.getKey().toString());
		status.setStatusCount(workSubStatusTypeReportRow.getCount());
		status.setStatusName(workSubStatusTypeReportRow.getCode());
		status.setStatusDescription(workSubStatusTypeReportRow.getDescription());
		status.setColorRgb(workSubStatusTypeReportRow.getColorRgb());
		status.setDashboardDisplayType(workSubStatusTypeReportRow.getDashboardDisplayType());
		switch (workSearchRequestUserType) {
			case CLIENT:
				sidebar.putToDashboardClientSubStatuses(status.getStatusId(), status);
				break;
			case RESOURCE:
				sidebar.putToDashboardClientSubStatuses(status.getStatusId(), status);
				break;
		}
	}

	public WorkSearchRequest copyWorkSearchRequest(WorkSearchRequest request) {
		WorkSearchRequest resourceWorkSearchRequest = new WorkSearchRequest();
		BeanUtilities.copyProperties(resourceWorkSearchRequest, request);
		resourceWorkSearchRequest
			.setUserNumber(request.getUserNumber())
			.setStartRow(request.getStartRow())
			.setPageSize(request.getPageSize())
			.setIncludeLabelDrilldownFacet(request.isIncludeLabelDrilldownFacet())
			.setSortBy(request.getSortBy())
			.setSortDirection(request.getSortDirection())
			.setStatusFilter(request.getStatusFilter())
			.setSubStatusFilter(request.getSubStatusFilter())
			.setAssignedResources(request.getAssignedResources())
			.setDispatchers(request.getDispatchers())
			.setClients(request.getClients())
			.setProjects(request.getProjects())
			.setInternalOwners(request.getInternalOwners())
			.setWorkMilestoneFilter(request.getWorkMilestoneFilter())
			.setDateRange(request.getDateRange())
			.setFollowerIds(request.getFollowerIds());
		return resourceWorkSearchRequest;
	}

	private Set<String> getDoableDecisions(final String userUuid) {
		final GetDoableDecisionsRequest request = GetDoableDecisionsRequest.newBuilder()
			.setDeciderUuid(userUuid)
			.build();
		final ImmutableSet.Builder<String> decisionFlowUuids = new ImmutableSet.Builder<>();
		final List<Decision> decisions = decisionFlowService.getDoableDecisions(request);
		for (Decision decision : decisions) {
			decisionFlowUuids.add(decision.getFlowUuid());
		}
		return decisionFlowUuids.build();
	}
}
