package com.workmarket.api.internal;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class ApiAuthorizationResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private long companyId;

	public ApiAuthorizationResponse() {
	}

	public ApiAuthorizationResponse(long userId, long companyId) {
		this();
		this.userId = userId;
		this.companyId = companyId;
	}

	public long getUserId() {
		return this.userId;
	}

	public ApiAuthorizationResponse setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return (userId > 0L);
	}

	public long getCompanyId() {
		return this.companyId;
	}

	public ApiAuthorizationResponse setCompanyId(long companyId) {
		this.companyId = companyId;
		return this;
	}

	public boolean isSetCompanyId() {
		return (companyId > 0L);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ApiAuthorizationResponse)
			return this.equals((ApiAuthorizationResponse) that);
		return false;
	}

	private boolean equals(ApiAuthorizationResponse that) {
		if (that == null)
			return false;

		boolean this_present_userId = true;
		boolean that_present_userId = true;
		if (this_present_userId || that_present_userId) {
			if (!(this_present_userId && that_present_userId))
				return false;
			if (this.userId != that.userId)
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

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_userId = true;
		builder.append(present_userId);
		if (present_userId)
			builder.append(userId);

		boolean present_companyId = true;
		builder.append(present_companyId);
		if (present_companyId)
			builder.append(companyId);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ApiAuthorizationResponse(");
		boolean first = true;

		sb.append("userId:");
		sb.append(this.userId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("companyId:");
		sb.append(this.companyId);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

