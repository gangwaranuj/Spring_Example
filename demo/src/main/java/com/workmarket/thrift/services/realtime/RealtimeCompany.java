package com.workmarket.thrift.services.realtime;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class RealtimeCompany implements Serializable {
	private static final long serialVersionUID = 1L;

	private long companyId;
	private String companyName;

	public RealtimeCompany() {
	}

	public RealtimeCompany(long companyId, String companyName) {
		this();
		this.companyId = companyId;
		this.companyName = companyName;
	}

	public long getCompanyId() {
		return this.companyId;
	}

	public RealtimeCompany setCompanyId(long companyId) {
		this.companyId = companyId;
		return this;
	}

	public boolean isSetCompanyId() {
		return (companyId > 0L);
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public RealtimeCompany setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}

	public boolean isSetCompanyName() {
		return this.companyName != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RealtimeCompany)
			return this.equals((RealtimeCompany) that);
		return false;
	}

	private boolean equals(RealtimeCompany that) {
		if (that == null)
			return false;

		boolean this_present_companyId = true;
		boolean that_present_companyId = true;
		if (this_present_companyId || that_present_companyId) {
			if (!(this_present_companyId && that_present_companyId))
				return false;
			if (this.companyId != that.companyId)
				return false;
		}

		boolean this_present_companyName = true && this.isSetCompanyName();
		boolean that_present_companyName = true && that.isSetCompanyName();
		if (this_present_companyName || that_present_companyName) {
			if (!(this_present_companyName && that_present_companyName))
				return false;
			if (!this.companyName.equals(that.companyName))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_companyId = true;
		builder.append(present_companyId);
		if (present_companyId)
			builder.append(companyId);

		boolean present_companyName = true && (isSetCompanyName());
		builder.append(present_companyName);
		if (present_companyName)
			builder.append(companyName);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RealtimeCompany(");
		boolean first = true;

		sb.append("companyId:");
		sb.append(this.companyId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("companyName:");
		if (this.companyName == null) {
			sb.append("null");
		} else {
			sb.append(this.companyName);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

