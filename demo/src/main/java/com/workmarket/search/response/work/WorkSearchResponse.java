package com.workmarket.search.response.work;

import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.search.response.FacetResult;
import com.workmarket.search.response.SearchResponse;
import com.workmarket.service.business.dto.WorkAggregatesDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WorkSearchResponse extends SearchResponse<SolrWorkData, WorkFacetResultType> {

	private static final long serialVersionUID = 1L;

	private WorkAggregatesDTO aggregates;
	private Map<String, WorkAggregatesDTO> aggregatesBuyerWorkStatusDrillDownBySubStatus;
	private DashboardResultList dashboardResultList;
	private DashboardResponseSidebar sidebar;

	public WorkSearchResponse() {
		super(new ArrayList<SolrWorkData>(), new LinkedHashMap<Enum<WorkFacetResultType>, List<FacetResult>>());
		this.aggregatesBuyerWorkStatusDrillDownBySubStatus = new LinkedHashMap<>();
	}

	public WorkAggregatesDTO getAggregates() {
		return aggregates;
	}

	public void setAggregates(WorkAggregatesDTO aggregates) {
		this.aggregates = aggregates;
	}

	public Map<String, WorkAggregatesDTO> getAggregatesBuyerWorkStatusDrillDownBySubStatus() {
		return aggregatesBuyerWorkStatusDrillDownBySubStatus;
	}

	public void setAggregatesBuyerWorkStatusDrillDownBySubStatus(Map<String, WorkAggregatesDTO> aggregatesBuyerWorkStatusDrillDownBySubStatus) {
		this.aggregatesBuyerWorkStatusDrillDownBySubStatus = aggregatesBuyerWorkStatusDrillDownBySubStatus;
	}

	public DashboardResultList getDashboardResultList() {
		return dashboardResultList;
	}

	public void setDashboardResultList(DashboardResultList dashboardResultList) {
		this.dashboardResultList = dashboardResultList;
	}

	public boolean isSetDashboardResultList() {
		return this.dashboardResultList != null;
	}

	public DashboardResponseSidebar getSidebar() {
		return this.sidebar;
	}

	public void setSidebar(DashboardResponseSidebar sidebar) {
		this.sidebar = sidebar;
	}

	public boolean isSetSidebar() {
		return this.sidebar != null;
	}

	@Override
	public String toString() {
		return "WorkSearchResponse{" +
			"aggregates=" + aggregates +
			", aggregatesBuyerWorkStatusDrillDownBySubStatus=" + aggregatesBuyerWorkStatusDrillDownBySubStatus +
			", dashboardResultList=" + dashboardResultList +
			", sidebar=" + sidebar +
			'}';
	}
}
