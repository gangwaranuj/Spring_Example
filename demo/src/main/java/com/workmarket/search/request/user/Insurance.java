package com.workmarket.search.request.user;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Insurance implements Serializable {
	private static final long serialVersionUID = 1L;

	private long insuranceId;
	private String insuranceName;

	public Insurance() {
	}

	public Insurance(long insuranceId, String insuranceName) {
		this();
		this.insuranceId = insuranceId;
		this.insuranceName = insuranceName;
	}

	public long getInsuranceId() {
		return this.insuranceId;
	}

	public Insurance setInsuranceId(long insuranceId) {
		this.insuranceId = insuranceId;
		return this;
	}

	public boolean isSetInsuranceId() {
		return (insuranceId > 0L);
	}

	public String getInsuranceName() {
		return this.insuranceName;
	}

	public Insurance setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
		return this;
	}

	public boolean isSetInsuranceName() {
		return this.insuranceName != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Insurance)
			return this.equals((Insurance) that);
		return false;
	}

	private boolean equals(Insurance that) {
		if (that == null)
			return false;

		boolean this_present_insuranceId = true;
		boolean that_present_insuranceId = true;
		if (this_present_insuranceId || that_present_insuranceId) {
			if (!(this_present_insuranceId && that_present_insuranceId))
				return false;
			if (this.insuranceId != that.insuranceId)
				return false;
		}

		boolean this_present_insuranceName = true && this.isSetInsuranceName();
		boolean that_present_insuranceName = true && that.isSetInsuranceName();
		if (this_present_insuranceName || that_present_insuranceName) {
			if (!(this_present_insuranceName && that_present_insuranceName))
				return false;
			if (!this.insuranceName.equals(that.insuranceName))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_insuranceId = true;
		builder.append(present_insuranceId);
		if (present_insuranceId)
			builder.append(insuranceId);

		boolean present_insuranceName = true && (isSetInsuranceName());
		builder.append(present_insuranceName);
		if (present_insuranceName)
			builder.append(insuranceName);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Insurance(");
		boolean first = true;

		sb.append("insuranceId:");
		sb.append(this.insuranceId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("insuranceName:");
		if (this.insuranceName == null) {
			sb.append("null");
		} else {
			sb.append(this.insuranceName);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

