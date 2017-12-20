package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class ReportingTypeResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String displayName;
	private ReportingReportType reportingReportType;

	public ReportingTypeResponse() {
	}

	public ReportingTypeResponse(String displayName, ReportingReportType reportingReportType) {
		this();
		this.displayName = displayName;
		this.reportingReportType = reportingReportType;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public ReportingTypeResponse setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public boolean isSetDisplayName() {
		return this.displayName != null;
	}

	public ReportingReportType getReportingReportType() {
		return this.reportingReportType;
	}

	public ReportingTypeResponse setReportingReportType(ReportingReportType reportingReportType) {
		this.reportingReportType = reportingReportType;
		return this;
	}

	public boolean isSetReportingReportType() {
		return this.reportingReportType != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ReportingTypeResponse)
			return this.equals((ReportingTypeResponse) that);
		return false;
	}

	private boolean equals(ReportingTypeResponse that) {
		if (that == null)
			return false;

		boolean this_present_displayName = true && this.isSetDisplayName();
		boolean that_present_displayName = true && that.isSetDisplayName();
		if (this_present_displayName || that_present_displayName) {
			if (!(this_present_displayName && that_present_displayName))
				return false;
			if (!this.displayName.equals(that.displayName))
				return false;
		}

		boolean this_present_reportingReportType = true && this.isSetReportingReportType();
		boolean that_present_reportingReportType = true && that.isSetReportingReportType();
		if (this_present_reportingReportType || that_present_reportingReportType) {
			if (!(this_present_reportingReportType && that_present_reportingReportType))
				return false;
			if (!this.reportingReportType.equals(that.reportingReportType))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_displayName = true && (isSetDisplayName());
		builder.append(present_displayName);
		if (present_displayName)
			builder.append(displayName);

		boolean present_reportingReportType = true && (isSetReportingReportType());
		builder.append(present_reportingReportType);
		if (present_reportingReportType)
			builder.append(reportingReportType.getValue());

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ReportingTypeResponse(");
		boolean first = true;

		sb.append("displayName:");
		if (this.displayName == null) {
			sb.append("null");
		} else {
			sb.append(this.displayName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("reportingReportType:");
		if (this.reportingReportType == null) {
			sb.append("null");
		} else {
			sb.append(this.reportingReportType);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}