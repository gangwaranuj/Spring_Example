package com.workmarket.search.model;

import com.workmarket.search.response.work.DashboardResource;

import java.util.List;
import java.util.Map;

/**
 * Work Search Response
 * Extracted from original Map {@see com.workmarket.web.helpers.WorkDashboardHelper#getDashboard}
 */
public class WorkSearchResponse {
    private Integer resultsCount;
    private Integer totalResultsCount;

    private boolean showBulkOps;
    private List<String> resultIds;
    private List<Map<String, Object>> data;
    private Map<String, Object> statusCounts;
    private Map<String, Object> subStatusCounts;
    private Map<Long, DashboardResource> assignedResources;

    public Integer getResultsCount() {
        return resultsCount;
    }

    public void setResultsCount(Integer resultsCount) {
        this.resultsCount = resultsCount;
    }

    public Integer getTotalResultsCount() {
        return totalResultsCount;
    }

    public void setTotalResultsCount(Integer totalResultsCount) {
        this.totalResultsCount = totalResultsCount;
    }

    public boolean isShowBulkOps() {
        return showBulkOps;
    }

    public void setShowBulkOps(boolean showBulkOps) {
        this.showBulkOps = showBulkOps;
    }

    public List<String> getResultIds() {
        return resultIds;
    }

    public void setResultIds(List<String> resultIds) {
        this.resultIds = resultIds;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public Map<String, Object> getStatusCounts() {
        return statusCounts;
    }

    public void setStatusCounts(Map<String, Object> statusCounts) {
        this.statusCounts = statusCounts;
    }

    public Map<String, Object> getSubStatusCounts() {
        return subStatusCounts;
    }

    public void setSubStatusCounts(Map<String, Object> subStatusCounts) {
        this.subStatusCounts = subStatusCounts;
    }

    public Map<Long, DashboardResource> getAssignedResources() {
        return assignedResources;
    }

    public void setAssignedResources(Map<Long, DashboardResource> assignedResources) {
        this.assignedResources = assignedResources;
    }
}
