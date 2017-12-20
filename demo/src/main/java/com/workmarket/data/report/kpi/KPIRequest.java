package com.workmarket.data.report.kpi;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIReportAggregateInterval;
import com.workmarket.domains.model.kpi.KPIReportType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class KPIRequest {

	private KPIReportType reportType;
	private KPIReportAggregateInterval aggregateInterval;
	private Calendar from;
	private Calendar to;
	private List<Filter> filters = Lists.newArrayList();
	private WorkStatusType workStatusType;

	public KPIReportType getReportType() {
		return reportType;
	}

	public void setReportType(KPIReportType reportType) {
		this.reportType = reportType;
	}

	public KPIReportAggregateInterval getAggregateInterval() {
		return aggregateInterval;
	}

	public void setAggregateInterval(KPIReportAggregateInterval interval) {
		this.aggregateInterval = interval;
	}

	public Calendar getFrom() {
		return from;
	}

	public void setFrom(Calendar from) {
		this.from = from;
	}

	public Calendar getTo() {
		return to;
	}

	public void setTo(Calendar to) {
		this.to = to;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public WorkStatusType getWorkStatusType() {
		return workStatusType;
	}

	public void setWorkStatusType(WorkStatusType workStatusType) {
		this.workStatusType = workStatusType;
	}

	public void addToFilters(Filter elem) {
		if (this.filters == null) {
			this.filters = new ArrayList<>();
		}
		this.filters.add(elem);
	}

	public boolean isSetFrom() {
		return getFrom() != null;
	}

	public boolean isSetFilters() {
		return getFilters() != null;
	}
}
