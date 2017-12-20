package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderColumnsRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<ColumnValuesRequest> columnValuesRequests;
	private ReportingTypeRequest reportingTypeRequest;
	private String reportName;

	public OrderColumnsRequest() {
	}

	public OrderColumnsRequest(
			List<ColumnValuesRequest> columnValuesRequests,
			ReportingTypeRequest reportingTypeRequest,
			String reportName) {
		this();
		this.columnValuesRequests = columnValuesRequests;
		this.reportingTypeRequest = reportingTypeRequest;
		this.reportName = reportName;
	}

	public int getColumnValuesRequestsSize() {
		return (this.columnValuesRequests == null) ? 0 : this.columnValuesRequests.size();
	}

	public java.util.Iterator<ColumnValuesRequest> getColumnValuesRequestsIterator() {
		return (this.columnValuesRequests == null) ? null : this.columnValuesRequests.iterator();
	}

	public void addToColumnValuesRequests(ColumnValuesRequest elem) {
		if (this.columnValuesRequests == null) {
			this.columnValuesRequests = new ArrayList<ColumnValuesRequest>();
		}
		this.columnValuesRequests.add(elem);
	}

	public List<ColumnValuesRequest> getColumnValuesRequests() {
		return this.columnValuesRequests;
	}

	public OrderColumnsRequest setColumnValuesRequests(List<ColumnValuesRequest> columnValuesRequests) {
		this.columnValuesRequests = columnValuesRequests;
		return this;
	}

	public boolean isSetColumnValuesRequests() {
		return this.columnValuesRequests != null;
	}

	public ReportingTypeRequest getReportingTypeRequest() {
		return this.reportingTypeRequest;
	}

	public OrderColumnsRequest setReportingTypeRequest(ReportingTypeRequest reportingTypeRequest) {
		this.reportingTypeRequest = reportingTypeRequest;
		return this;
	}

	public boolean isSetReportingTypeRequest() {
		return this.reportingTypeRequest != null;
	}

	public String getReportName() {
		return this.reportName;
	}

	public OrderColumnsRequest setReportName(String reportName) {
		this.reportName = reportName;
		return this;
	}

	public boolean isSetReportName() {
		return this.reportName != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof OrderColumnsRequest)
			return this.equals((OrderColumnsRequest) that);
		return false;
	}

	private boolean equals(OrderColumnsRequest that) {
		if (that == null)
			return false;

		boolean this_present_columnValuesRequests = true && this.isSetColumnValuesRequests();
		boolean that_present_columnValuesRequests = true && that.isSetColumnValuesRequests();
		if (this_present_columnValuesRequests || that_present_columnValuesRequests) {
			if (!(this_present_columnValuesRequests && that_present_columnValuesRequests))
				return false;
			if (!this.columnValuesRequests.equals(that.columnValuesRequests))
				return false;
		}

		boolean this_present_reportingTypeRequest = true && this.isSetReportingTypeRequest();
		boolean that_present_reportingTypeRequest = true && that.isSetReportingTypeRequest();
		if (this_present_reportingTypeRequest || that_present_reportingTypeRequest) {
			if (!(this_present_reportingTypeRequest && that_present_reportingTypeRequest))
				return false;
			if (!this.reportingTypeRequest.equals(that.reportingTypeRequest))
				return false;
		}

		boolean this_present_reportName = true && this.isSetReportName();
		boolean that_present_reportName = true && that.isSetReportName();
		if (this_present_reportName || that_present_reportName) {
			if (!(this_present_reportName && that_present_reportName))
				return false;
			if (!this.reportName.equals(that.reportName))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_columnValuesRequests = true && (isSetColumnValuesRequests());
		builder.append(present_columnValuesRequests);
		if (present_columnValuesRequests)
			builder.append(columnValuesRequests);

		boolean present_reportingTypeRequest = true && (isSetReportingTypeRequest());
		builder.append(present_reportingTypeRequest);
		if (present_reportingTypeRequest)
			builder.append(reportingTypeRequest);

		boolean present_reportName = true && (isSetReportName());
		builder.append(present_reportName);
		if (present_reportName)
			builder.append(reportName);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("OrderColumnsRequest(");
		boolean first = true;

		sb.append("columnValuesRequests:");
		if (this.columnValuesRequests == null) {
			sb.append("null");
		} else {
			sb.append(this.columnValuesRequests);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("reportingTypeRequest:");
		if (this.reportingTypeRequest == null) {
			sb.append("null");
		} else {
			sb.append(this.reportingTypeRequest);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("reportName:");
		if (this.reportName == null) {
			sb.append("null");
		} else {
			sb.append(this.reportName);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}