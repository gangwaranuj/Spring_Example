package com.workmarket.domains.work.model;

import java.util.Calendar;

public class WorkResourceAggregateFilter {
	private Calendar fromDate;
	private String workStatusTypeCode;
	private String resourceLabelTypeCode;
	private Boolean lessThan24Hours;
	private Long companyId;

	public Calendar getFromDate() {
		return fromDate;
	}

	public WorkResourceAggregateFilter setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public String getWorkStatusTypeCode() {
		return workStatusTypeCode;
	}

	public WorkResourceAggregateFilter setWorkStatusTypeCode(String workStatusTypeCode) {
		this.workStatusTypeCode = workStatusTypeCode;
		return this;
	}

	public String getResourceLabelTypeCode() {
		return resourceLabelTypeCode;
	}

	public WorkResourceAggregateFilter setResourceLabelTypeCode(String resourceLabelTypeCode) {
		this.resourceLabelTypeCode = resourceLabelTypeCode;
		return this;
	}

	public Boolean isLessThan24Hours() {
		return lessThan24Hours;
	}

	public WorkResourceAggregateFilter setLessThan24Hours(Boolean lessThan24Hours) {
		this.lessThan24Hours = lessThan24Hours;
		return this;
	}

	public boolean isSetLessThan24Hours() {
		return lessThan24Hours != null;
	}

	public boolean isSetFromDate() {
		return fromDate != null;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public WorkResourceAggregateFilter setCompanyId(Long companyId) {
		this.companyId = companyId;
		return this;
	}

	public boolean isScopedToCompany() {
		return companyId != null;
	}
}
