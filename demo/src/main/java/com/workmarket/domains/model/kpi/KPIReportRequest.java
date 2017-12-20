package com.workmarket.domains.model.kpi;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KPIReportRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private KPIReportType reportType;
	private long from;
	private long to;
	private List<Filter> filters;
	private KPIReportAggregateInterval aggregateInterval;

	public KPIReportRequest() {
	}

	public KPIReportRequest(KPIReportType reportType, long to, KPIReportAggregateInterval aggregateInterval) {
		this();
		this.reportType = reportType;
		this.to = to;
		this.aggregateInterval = aggregateInterval;
	}

	public KPIReportType getReportType() {
		return this.reportType;
	}

	public KPIReportRequest setReportType(KPIReportType reportType) {
		this.reportType = reportType;
		return this;
	}

	public boolean isSetReportType() {
		return this.reportType != null;
	}

	public long getFrom() {
		return this.from;
	}

	public KPIReportRequest setFrom(long from) {
		this.from = from;
		return this;
	}

	public boolean isSetFrom() {
		return (from > 0L);
	}

	public long getTo() {
		return this.to;
	}

	public KPIReportRequest setTo(long to) {
		this.to = to;
		return this;
	}

	public boolean isSetTo() {
		return (to > 0L);
	}

	public int getFiltersSize() {
		return (this.filters == null) ? 0 : this.filters.size();
	}

	public java.util.Iterator<Filter> getFiltersIterator() {
		return (this.filters == null) ? null : this.filters.iterator();
	}

	public void addToFilters(Filter elem) {
		if (this.filters == null) {
			this.filters = new ArrayList<Filter>();
		}
		this.filters.add(elem);
	}

	public List<Filter> getFilters() {
		return this.filters;
	}

	public KPIReportRequest setFilters(List<Filter> filters) {
		this.filters = filters;
		return this;
	}

	public boolean isSetFilters() {
		return this.filters != null;
	}

	public KPIReportAggregateInterval getAggregateInterval() {
		return this.aggregateInterval;
	}

	public KPIReportRequest setAggregateInterval(KPIReportAggregateInterval aggregateInterval) {
		this.aggregateInterval = aggregateInterval;
		return this;
	}

	public boolean isSetAggregateInterval() {
		return this.aggregateInterval != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof KPIReportRequest)
			return this.equals((KPIReportRequest) that);
		return false;
	}

	private boolean equals(KPIReportRequest that) {
		if (that == null)
			return false;

		boolean this_present_reportType = true && this.isSetReportType();
		boolean that_present_reportType = true && that.isSetReportType();
		if (this_present_reportType || that_present_reportType) {
			if (!(this_present_reportType && that_present_reportType))
				return false;
			if (!this.reportType.equals(that.reportType))
				return false;
		}

		boolean this_present_from = true && this.isSetFrom();
		boolean that_present_from = true && that.isSetFrom();
		if (this_present_from || that_present_from) {
			if (!(this_present_from && that_present_from))
				return false;
			if (this.from != that.from)
				return false;
		}

		boolean this_present_to = true;
		boolean that_present_to = true;
		if (this_present_to || that_present_to) {
			if (!(this_present_to && that_present_to))
				return false;
			if (this.to != that.to)
				return false;
		}

		boolean this_present_filters = true && this.isSetFilters();
		boolean that_present_filters = true && that.isSetFilters();
		if (this_present_filters || that_present_filters) {
			if (!(this_present_filters && that_present_filters))
				return false;
			if (!this.filters.equals(that.filters))
				return false;
		}

		boolean this_present_aggregateInterval = true && this.isSetAggregateInterval();
		boolean that_present_aggregateInterval = true && that.isSetAggregateInterval();
		if (this_present_aggregateInterval || that_present_aggregateInterval) {
			if (!(this_present_aggregateInterval && that_present_aggregateInterval))
				return false;
			if (!this.aggregateInterval.equals(that.aggregateInterval))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_reportType = true && (isSetReportType());
		builder.append(present_reportType);
		if (present_reportType)
			builder.append(reportType.getValue());

		boolean present_from = true && (isSetFrom());
		builder.append(present_from);
		if (present_from)
			builder.append(from);

		boolean present_to = true;
		builder.append(present_to);
		if (present_to)
			builder.append(to);

		boolean present_filters = true && (isSetFilters());
		builder.append(present_filters);
		if (present_filters)
			builder.append(filters);

		boolean present_aggregateInterval = true && (isSetAggregateInterval());
		builder.append(present_aggregateInterval);
		if (present_aggregateInterval)
			builder.append(aggregateInterval.getValue());

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("KPIReportRequest(");
		boolean first = true;

		sb.append("reportType:");
		if (this.reportType == null) {
			sb.append("null");
		} else {
			sb.append(this.reportType);
		}
		first = false;
		if (isSetFrom()) {
			if (!first) sb.append(", ");
			sb.append("from:");
			sb.append(this.from);
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("to:");
		sb.append(this.to);
		first = false;
		if (isSetFilters()) {
			if (!first) sb.append(", ");
			sb.append("filters:");
			if (this.filters == null) {
				sb.append("null");
			} else {
				sb.append(this.filters);
			}
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("aggregateInterval:");
		if (this.aggregateInterval == null) {
			sb.append("null");
		} else {
			sb.append(this.aggregateInterval);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}