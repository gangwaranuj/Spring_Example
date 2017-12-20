package com.workmarket.domains.reports.model;

/**
 * Created by ianha on 2/2/15.
 */
public class CustomReportCustomFieldDTO {
    long id;
    String name;
    boolean deleted;
    boolean reportingCriteriaFilter;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isReportingCriteriaFilter() {
        return reportingCriteriaFilter;
    }

    public void setReportingCriteriaFilter(boolean reportingCriteriaFilter) {
        this.reportingCriteriaFilter = reportingCriteriaFilter;
    }
}
