package com.workmarket.api.v2.employer.assignments.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Set;

/**
 * Assignment search request parameters
 * Extracted from {@link com.workmarket.web.forms.work.WorkDashboardForm}
 */
public class AssignmentSearchRequestDTO {
    // Pagination
    Integer start = 0;
    Integer pageSize = 10;
    String sort;
    String dir = "desc";
    String type;
    String status;
    String subStatus;

    // Filters
    Set<Long> clientCompanies;
    Set<Long> projects;
    Set<String> internalOwners;
    Set<Integer> assignedResources;
    Set<Integer> assignedVendors;
    Set<Integer> bundles;
    Integer workMilestone;
    Integer workDateRange;
    Boolean assignedToMe;
    Boolean dispatchedByMe;
    boolean includeTime;
    boolean includeCustomFields;
    Boolean following;
    String keyword;
    boolean filterless;
    boolean fast;
    String title;
    String assignedResourceName;
    String clientCompanyName;
    String buyerFullName;
    String projectName;
    boolean filterPendingMultiApprovals = false;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    Calendar scheduleFrom;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    Calendar scheduleThrough;

    @DateTimeFormat(pattern = "h:mm aa")
    Calendar timeFrom;

    @DateTimeFormat(pattern = "h:mm aa")
    Calendar timeThrough;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(String subStatus) {
        this.subStatus = subStatus;
    }

    public Set<Long> getClientCompanies() {
        return clientCompanies;
    }

    public void setClientCompanies(Set<Long> clientCompanies) {
        this.clientCompanies = clientCompanies;
    }

    public Set<Long> getProjects() {
        return projects;
    }

    public void setProjects(Set<Long> projects) {
        this.projects = projects;
    }

    public Set<String> getInternalOwners() {
        return internalOwners;
    }

    public void setInternalOwners(Set<String> internalOwners) {
        this.internalOwners = internalOwners;
    }

    public Set<Integer> getAssignedResources() {
        return assignedResources;
    }

    public void setAssignedResources(Set<Integer> assignedResources) {
        this.assignedResources = assignedResources;
    }

    public Set<Integer> getAssignedVendors() {
        return assignedVendors;
    }

    public void setAssignedVendors(Set<Integer> assignedVendors) {
        this.assignedVendors = assignedVendors;
    }

    public Set<Integer> getBundles() {
        return bundles;
    }

    public void setBundles(Set<Integer> bundles) {
        this.bundles = bundles;
    }

    public Integer getWorkMilestone() {
        return workMilestone;
    }

    public void setWorkMilestone(Integer workMilestone) {
        this.workMilestone = workMilestone;
    }

    public Integer getWorkDateRange() {
        return workDateRange;
    }

    public void setWorkDateRange(Integer workDateRange) {
        this.workDateRange = workDateRange;
    }

    public Boolean getAssignedToMe() {
        return assignedToMe;
    }

    public void setAssignedToMe(Boolean assignedToMe) {
        this.assignedToMe = assignedToMe;
    }

    public Boolean getDispatchedByMe() {
        return dispatchedByMe;
    }

    public void setDispatchedByMe(Boolean dispatchedByMe) {
        this.dispatchedByMe = dispatchedByMe;
    }

    public boolean isIncludeTime() {
        return includeTime;
    }

    public boolean isIncludeCustomFields() {
        return includeCustomFields;
    }

    public void setIncludeCustomFields(boolean includeCustomFields) {
        this.includeCustomFields = includeCustomFields;
    }

    public void setIncludeTime(boolean includeTime) {
        this.includeTime = includeTime;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean isFilterless() {
        return filterless;
    }

    public void setFilterless(boolean filterless) {
        this.filterless = filterless;
    }

    public boolean isFast() {
        return fast;
    }

    public void setFast(boolean fast) {
        this.fast = fast;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssignedResourceName() {
        return assignedResourceName;
    }

    public void setAssignedResourceName(String assignedResourceName) {
        this.assignedResourceName = assignedResourceName;
    }

    public String getClientCompanyName() {
        return clientCompanyName;
    }

    public void setClientCompanyName(String clientCompanyName) {
        this.clientCompanyName = clientCompanyName;
    }

    public String getBuyerFullName() {
        return buyerFullName;
    }

    public void setBuyerFullName(String buyerFullName) {
        this.buyerFullName = buyerFullName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean isFilterPendingMultiApprovals() {
        return filterPendingMultiApprovals;
    }

    public void setFilterPendingMultiApprovals(boolean filterPendingMultiApprovals) {
        this.filterPendingMultiApprovals = filterPendingMultiApprovals;
    }

    public Calendar getScheduleFrom() {
        return scheduleFrom;
    }

    public void setScheduleFrom(Calendar scheduleFrom) {
        this.scheduleFrom = scheduleFrom;
    }

    public Calendar getScheduleThrough() {
        return scheduleThrough;
    }

    public void setScheduleThrough(Calendar scheduleThrough) {
        this.scheduleThrough = scheduleThrough;
    }

    public Calendar getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Calendar timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Calendar getTimeThrough() {
        return timeThrough;
    }

    public void setTimeThrough(Calendar timeThrough) {
        this.timeThrough = timeThrough;
    }
}