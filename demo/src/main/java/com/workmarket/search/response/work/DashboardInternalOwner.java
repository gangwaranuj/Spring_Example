package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class DashboardInternalOwner implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long userId;
	private String userName;

	public DashboardInternalOwner() {
	}

	public DashboardInternalOwner(Long userId, String userName) {
		this();
		this.userId = userId;
		this.userName = userName;
	}

	public Long getUserId() {
		return this.userId;
	}

	public DashboardInternalOwner setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return this.userId != null;
	}

	public String getUserName() {
		return this.userName;
	}

	public DashboardInternalOwner setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public boolean isSetUserName() {
		return this.userName != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof DashboardInternalOwner)
			return this.equals((DashboardInternalOwner) that);
		return false;
	}

	private boolean equals(DashboardInternalOwner that) {
		if (that == null)
			return false;

		boolean this_present_userId = true && this.isSetUserId();
		boolean that_present_userId = true && that.isSetUserId();
		if (this_present_userId || that_present_userId) {
			if (!(this_present_userId && that_present_userId))
				return false;
			if (!this.userId.equals(that.userId))
				return false;
		}

		boolean this_present_userName = true && this.isSetUserName();
		boolean that_present_userName = true && that.isSetUserName();
		if (this_present_userName || that_present_userName) {
			if (!(this_present_userName && that_present_userName))
				return false;
			if (!this.userName.equals(that.userName))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_userId = true && (isSetUserId());
		builder.append(present_userId);
		if (present_userId)
			builder.append(userId);

		boolean present_userName = true && (isSetUserName());
		builder.append(present_userName);
		if (present_userName)
			builder.append(userName);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DashboardInternalOwner(");
		boolean first = true;

		sb.append("userId:");
		if (this.userId == null) {
			sb.append("null");
		} else {
			sb.append(this.userId);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("userName:");
		if (this.userName == null) {
			sb.append("null");
		} else {
			sb.append(this.userName);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}