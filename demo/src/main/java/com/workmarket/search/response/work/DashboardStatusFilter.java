package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class DashboardStatusFilter implements Serializable {
	private static final long serialVersionUID = 1L;

	private String statusCode;
	private String statusName;

	public DashboardStatusFilter() {
	}

	public DashboardStatusFilter(String statusCode, String statusName) {
		this();
		this.statusCode = statusCode;
		this.statusName = statusName;
	}

	public String getStatusCode() {
		return this.statusCode;
	}

	public DashboardStatusFilter setStatusCode(String statusCode) {
		this.statusCode = statusCode;
		return this;
	}

	public boolean isSetStatusCode() {
		return this.statusCode != null;
	}

	public String getStatusName() {
		return this.statusName;
	}

	public DashboardStatusFilter setStatusName(String statusName) {
		this.statusName = statusName;
		return this;
	}

	public boolean isSetStatusName() {
		return this.statusName != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof DashboardStatusFilter)
			return this.equals((DashboardStatusFilter) that);
		return false;
	}

	private boolean equals(DashboardStatusFilter that) {
		if (that == null)
			return false;

		boolean this_present_statusId = true && this.isSetStatusCode();
		boolean that_present_statusId = true && that.isSetStatusCode();
		if (this_present_statusId || that_present_statusId) {
			if (!(this_present_statusId && that_present_statusId))
				return false;
			if (!this.statusCode.equals(that.statusCode))
				return false;
		}

		boolean this_present_statusName = true && this.isSetStatusName();
		boolean that_present_statusName = true && that.isSetStatusName();
		if (this_present_statusName || that_present_statusName) {
			if (!(this_present_statusName && that_present_statusName))
				return false;
			if (!this.statusName.equals(that.statusName))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_statusId = true && (isSetStatusCode());
		builder.append(present_statusId);
		if (present_statusId)
			builder.append(statusCode);

		boolean present_statusName = true && (isSetStatusName());
		builder.append(present_statusName);
		if (present_statusName)
			builder.append(statusName);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DashboardStatusFilter(");
		boolean first = true;

		sb.append("statusCode:");
		if (this.statusCode == null) {
			sb.append("null");
		} else {
			sb.append(this.statusCode);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("statusName:");
		if (this.statusName == null) {
			sb.append("null");
		} else {
			sb.append(this.statusName);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

