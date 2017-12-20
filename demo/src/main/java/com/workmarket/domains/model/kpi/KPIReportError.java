package com.workmarket.domains.model.kpi;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class KPIReportError implements Serializable {
	private static final long serialVersionUID = 1L;

	private String why;
	private KPIReportErrorType errorType;

	public KPIReportError() {
	}

	public KPIReportError(String why, KPIReportErrorType errorType) {
		this();
		this.why = why;
		this.errorType = errorType;
	}

	public String getWhy() {
		return this.why;
	}

	public KPIReportError setWhy(String why) {
		this.why = why;
		return this;
	}

	public boolean isSetWhy() {
		return this.why != null;
	}

	public KPIReportErrorType getErrorType() {
		return this.errorType;
	}

	public KPIReportError setErrorType(KPIReportErrorType errorType) {
		this.errorType = errorType;
		return this;
	}

	public boolean isSetErrorType() {
		return this.errorType != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof KPIReportError)
			return this.equals((KPIReportError) that);
		return false;
	}

	private boolean equals(KPIReportError that) {
		if (that == null)
			return false;

		boolean this_present_why = true && this.isSetWhy();
		boolean that_present_why = true && that.isSetWhy();
		if (this_present_why || that_present_why) {
			if (!(this_present_why && that_present_why))
				return false;
			if (!this.why.equals(that.why))
				return false;
		}

		boolean this_present_errorType = true && this.isSetErrorType();
		boolean that_present_errorType = true && that.isSetErrorType();
		if (this_present_errorType || that_present_errorType) {
			if (!(this_present_errorType && that_present_errorType))
				return false;
			if (!this.errorType.equals(that.errorType))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_why = true && (isSetWhy());
		builder.append(present_why);
		if (present_why)
			builder.append(why);

		boolean present_errorType = true && (isSetErrorType());
		builder.append(present_errorType);
		if (present_errorType)
			builder.append(errorType.getValue());

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("KPIReportError(");
		boolean first = true;

		sb.append("why:");
		if (this.why == null) {
			sb.append("null");
		} else {
			sb.append(this.why);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("errorType:");
		if (this.errorType == null) {
			sb.append("null");
		} else {
			sb.append(this.errorType);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}