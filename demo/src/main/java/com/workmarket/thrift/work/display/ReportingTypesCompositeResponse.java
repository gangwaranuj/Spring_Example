package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportingTypesCompositeResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<ReportingTypeResponse> reportingTypeResponses;

	public ReportingTypesCompositeResponse() {
	}

	public ReportingTypesCompositeResponse(List<ReportingTypeResponse> reportingTypeResponses) {
		this();
		this.reportingTypeResponses = reportingTypeResponses;
	}

	public int getReportingTypeResponsesSize() {
		return (this.reportingTypeResponses == null) ? 0 : this.reportingTypeResponses.size();
	}

	public java.util.Iterator<ReportingTypeResponse> getReportingTypeResponsesIterator() {
		return (this.reportingTypeResponses == null) ? null : this.reportingTypeResponses.iterator();
	}

	public void addToReportingTypeResponses(ReportingTypeResponse elem) {
		if (this.reportingTypeResponses == null) {
			this.reportingTypeResponses = new ArrayList<ReportingTypeResponse>();
		}
		this.reportingTypeResponses.add(elem);
	}

	public List<ReportingTypeResponse> getReportingTypeResponses() {
		return this.reportingTypeResponses;
	}

	public ReportingTypesCompositeResponse setReportingTypeResponses(List<ReportingTypeResponse> reportingTypeResponses) {
		this.reportingTypeResponses = reportingTypeResponses;
		return this;
	}

	public boolean isSetReportingTypeResponses() {
		return this.reportingTypeResponses != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ReportingTypesCompositeResponse)
			return this.equals((ReportingTypesCompositeResponse) that);
		return false;
	}

	private boolean equals(ReportingTypesCompositeResponse that) {
		if (that == null)
			return false;

		boolean this_present_reportingTypeResponses = true && this.isSetReportingTypeResponses();
		boolean that_present_reportingTypeResponses = true && that.isSetReportingTypeResponses();
		if (this_present_reportingTypeResponses || that_present_reportingTypeResponses) {
			if (!(this_present_reportingTypeResponses && that_present_reportingTypeResponses))
				return false;
			if (!this.reportingTypeResponses.equals(that.reportingTypeResponses))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_reportingTypeResponses = true && (isSetReportingTypeResponses());
		builder.append(present_reportingTypeResponses);
		if (present_reportingTypeResponses)
			builder.append(reportingTypeResponses);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ReportingTypesCompositeResponse(");
		boolean first = true;

		sb.append("reportingTypeResponses:");
		if (this.reportingTypeResponses == null) {
			sb.append("null");
		} else {
			sb.append(this.reportingTypeResponses);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}