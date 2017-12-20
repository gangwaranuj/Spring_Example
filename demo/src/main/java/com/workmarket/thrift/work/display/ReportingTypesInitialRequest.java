package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class ReportingTypesInitialRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userNumber;
	private long companyId;
	private String locale;

	public ReportingTypesInitialRequest() {
	}

	public ReportingTypesInitialRequest(String userNumber, long companyId, String locale) {
		this();
		this.userNumber = userNumber;
		this.companyId = companyId;
		this.locale = locale;
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public ReportingTypesInitialRequest setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public long getCompanyId() {
		return this.companyId;
	}

	public ReportingTypesInitialRequest setCompanyId(long companyId) {
		this.companyId = companyId;
		return this;
	}

	public boolean isSetCompanyId() {
		return (companyId > 0L);
	}

	public String getLocale() {
		return this.locale;
	}

	public ReportingTypesInitialRequest setLocale(String locale) {
		this.locale = locale;
		return this;
	}

	public boolean isSetLocale() {
		return this.locale != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ReportingTypesInitialRequest)
			return this.equals((ReportingTypesInitialRequest) that);
		return false;
	}

	private boolean equals(ReportingTypesInitialRequest that) {
		if (that == null)
			return false;

		boolean this_present_userNumber = true && this.isSetUserNumber();
		boolean that_present_userNumber = true && that.isSetUserNumber();
		if (this_present_userNumber || that_present_userNumber) {
			if (!(this_present_userNumber && that_present_userNumber))
				return false;
			if (!this.userNumber.equals(that.userNumber))
				return false;
		}

		boolean this_present_companyId = true;
		boolean that_present_companyId = true;
		if (this_present_companyId || that_present_companyId) {
			if (!(this_present_companyId && that_present_companyId))
				return false;
			if (this.companyId != that.companyId)
				return false;
		}

		boolean this_present_locale = true && this.isSetLocale();
		boolean that_present_locale = true && that.isSetLocale();
		if (this_present_locale || that_present_locale) {
			if (!(this_present_locale && that_present_locale))
				return false;
			if (!this.locale.equals(that.locale))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_userNumber = true && (isSetUserNumber());
		builder.append(present_userNumber);
		if (present_userNumber)
			builder.append(userNumber);

		boolean present_companyId = true;
		builder.append(present_companyId);
		if (present_companyId)
			builder.append(companyId);

		boolean present_locale = true && (isSetLocale());
		builder.append(present_locale);
		if (present_locale)
			builder.append(locale);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ReportingTypesInitialRequest(");
		boolean first = true;

		sb.append("userNumber:");
		if (this.userNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.userNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("companyId:");
		sb.append(this.companyId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("locale:");
		if (this.locale == null) {
			sb.append("null");
		} else {
			sb.append(this.locale);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}