package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class ReportingTypeRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private ReportingReportType reportingReportType;
	private ReportingTypesInitialRequest reportingTypesInitialRequest;

	public ReportingTypeRequest() {
	}

	public ReportingTypeRequest(
			ReportingReportType reportingReportType,
			ReportingTypesInitialRequest reportingTypesInitialRequest) {
		this();
		this.reportingReportType = reportingReportType;
		this.reportingTypesInitialRequest = reportingTypesInitialRequest;
	}

	public ReportingReportType getReportingReportType() {
		return this.reportingReportType;
	}

	public ReportingTypeRequest setReportingReportType(ReportingReportType reportingReportType) {
		this.reportingReportType = reportingReportType;
		return this;
	}

	public boolean isSetReportingReportType() {
		return this.reportingReportType != null;
	}

	public ReportingTypesInitialRequest getReportingTypesInitialRequest() {
		return this.reportingTypesInitialRequest;
	}

	public ReportingTypeRequest setReportingTypesInitialRequest(ReportingTypesInitialRequest reportingTypesInitialRequest) {
		this.reportingTypesInitialRequest = reportingTypesInitialRequest;
		return this;
	}

	public boolean isSetReportingTypesInitialRequest() {
		return this.reportingTypesInitialRequest != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ReportingTypeRequest)
			return this.equals((ReportingTypeRequest) that);
		return false;
	}

	private boolean equals(ReportingTypeRequest that) {
		if (that == null)
			return false;

		boolean this_present_reportingReportType = true && this.isSetReportingReportType();
		boolean that_present_reportingReportType = true && that.isSetReportingReportType();
		if (this_present_reportingReportType || that_present_reportingReportType) {
			if (!(this_present_reportingReportType && that_present_reportingReportType))
				return false;
			if (!this.reportingReportType.equals(that.reportingReportType))
				return false;
		}

		boolean this_present_reportingTypesInitialRequest = true && this.isSetReportingTypesInitialRequest();
		boolean that_present_reportingTypesInitialRequest = true && that.isSetReportingTypesInitialRequest();
		if (this_present_reportingTypesInitialRequest || that_present_reportingTypesInitialRequest) {
			if (!(this_present_reportingTypesInitialRequest && that_present_reportingTypesInitialRequest))
				return false;
			if (!this.reportingTypesInitialRequest.equals(that.reportingTypesInitialRequest))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_reportingReportType = true && (isSetReportingReportType());
		builder.append(present_reportingReportType);
		if (present_reportingReportType)
			builder.append(reportingReportType.getValue());

		boolean present_reportingTypesInitialRequest = true && (isSetReportingTypesInitialRequest());
		builder.append(present_reportingTypesInitialRequest);
		if (present_reportingTypesInitialRequest)
			builder.append(reportingTypesInitialRequest);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ReportingTypeRequest(");
		boolean first = true;

		sb.append("reportingReportType:");
		if (this.reportingReportType == null) {
			sb.append("null");
		} else {
			sb.append(this.reportingReportType);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("reportingTypesInitialRequest:");
		if (this.reportingTypesInitialRequest == null) {
			sb.append("null");
		} else {
			sb.append(this.reportingTypesInitialRequest);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}