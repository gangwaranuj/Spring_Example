package com.workmarket.thrift.work.display;

import com.workmarket.domains.model.reporting.ReportRecurrence;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class SavedCustomReportResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String reportName;
	private long reportKey;
	private ReportRecurrence recurrence;
	private String creator;

	public SavedCustomReportResponse() {
	}

	public SavedCustomReportResponse(String reportName, long reportKey, ReportRecurrence recurrence) {
		this.reportName = reportName;
		this.reportKey = reportKey;
		this.recurrence = recurrence;
	}

	public String getReportName() {
		return this.reportName;
	}

	public SavedCustomReportResponse setReportName(String reportName) {
		this.reportName = reportName;
		return this;
	}

	public boolean isSetReportName() {
		return this.reportName != null;
	}

	public long getReportKey() {
		return this.reportKey;
	}

	public SavedCustomReportResponse setReportKey(long reportKey) {
		this.reportKey = reportKey;
		return this;
	}

	public boolean isSetReportKey() {
		return (reportKey > 0L);
	}

	public ReportRecurrence getRecurrence() {
		return recurrence;
	}

	public void setRecurrence(ReportRecurrence recurrence) {
		this.recurrence = recurrence;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	private boolean equals(SavedCustomReportResponse that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SavedCustomReportResponse(");
		boolean first = true;

		sb.append("reportName:");
		if (this.reportName == null) {
			sb.append("null");
		} else {
			sb.append(this.reportName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("reportKey:");
		sb.append(this.reportKey);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}