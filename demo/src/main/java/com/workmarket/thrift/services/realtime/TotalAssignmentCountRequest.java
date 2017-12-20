package com.workmarket.thrift.services.realtime;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class TotalAssignmentCountRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private long companyId;
	private String timeZone;

	public TotalAssignmentCountRequest() {
	}

	public TotalAssignmentCountRequest(String timeZone) {
		this();
		this.timeZone = timeZone;
	}

	public long getCompanyId() {
		return this.companyId;
	}

	public TotalAssignmentCountRequest setCompanyId(long companyId) {
		this.companyId = companyId;
		return this;
	}

	public boolean isSetCompanyId() {
		return (companyId > 0L);
	}

	public String getTimeZone() {
		return this.timeZone;
	}

	public TotalAssignmentCountRequest setTimeZone(String timeZone) {
		this.timeZone = timeZone;
		return this;
	}

	public boolean isSetTimeZone() {
		return this.timeZone != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof TotalAssignmentCountRequest)
			return this.equals((TotalAssignmentCountRequest) that);
		return false;
	}

	private boolean equals(TotalAssignmentCountRequest that) {
		if (that == null)
			return false;

		boolean this_present_companyId = true && this.isSetCompanyId();
		boolean that_present_companyId = true && that.isSetCompanyId();
		if (this_present_companyId || that_present_companyId) {
			if (!(this_present_companyId && that_present_companyId))
				return false;
			if (this.companyId != that.companyId)
				return false;
		}

		boolean this_present_timeZone = true && this.isSetTimeZone();
		boolean that_present_timeZone = true && that.isSetTimeZone();
		if (this_present_timeZone || that_present_timeZone) {
			if (!(this_present_timeZone && that_present_timeZone))
				return false;
			if (!this.timeZone.equals(that.timeZone))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_companyId = true && (isSetCompanyId());
		builder.append(present_companyId);
		if (present_companyId)
			builder.append(companyId);

		boolean present_timeZone = true && (isSetTimeZone());
		builder.append(present_timeZone);
		if (present_timeZone)
			builder.append(timeZone);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TotalAssignmentCountRequest(");
		boolean first = true;

		if (isSetCompanyId()) {
			sb.append("companyId:");
			sb.append(this.companyId);
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("timeZone:");
		if (this.timeZone == null) {
			sb.append("null");
		} else {
			sb.append(this.timeZone);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

