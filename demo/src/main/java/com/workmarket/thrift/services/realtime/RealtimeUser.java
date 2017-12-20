package com.workmarket.thrift.services.realtime;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class RealtimeUser implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private String userNumber;
	private String firstName;
	private String lastName;

	public RealtimeUser() {
	}

	public RealtimeUser(long userId, String userNumber) {
		this();
		this.userId = userId;
		this.userNumber = userNumber;
	}

	public long getUserId() {
		return this.userId;
	}

	public RealtimeUser setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return (userId > 0L);
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public RealtimeUser setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public RealtimeUser setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public boolean isSetFirstName() {
		return this.firstName != null;
	}

	public String getLastName() {
		return this.lastName;
	}

	public RealtimeUser setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public boolean isSetLastName() {
		return this.lastName != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RealtimeUser)
			return this.equals((RealtimeUser) that);
		return false;
	}

	private boolean equals(RealtimeUser that) {
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

		boolean this_present_userNumber = true && this.isSetUserNumber();
		boolean that_present_userNumber = true && that.isSetUserNumber();
		if (this_present_userNumber || that_present_userNumber) {
			if (!(this_present_userNumber && that_present_userNumber))
				return false;
			if (!this.userNumber.equals(that.userNumber))
				return false;
		}

		boolean this_present_firstName = true && this.isSetFirstName();
		boolean that_present_firstName = true && that.isSetFirstName();
		if (this_present_firstName || that_present_firstName) {
			if (!(this_present_firstName && that_present_firstName))
				return false;
			if (!this.firstName.equals(that.firstName))
				return false;
		}

		boolean this_present_lastName = true && this.isSetLastName();
		boolean that_present_lastName = true && that.isSetLastName();
		if (this_present_lastName || that_present_lastName) {
			if (!(this_present_lastName && that_present_lastName))
				return false;
			if (!this.lastName.equals(that.lastName))
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

		boolean present_userNumber = true && (isSetUserNumber());
		builder.append(present_userNumber);
		if (present_userNumber)
			builder.append(userNumber);

		boolean present_firstName = true && (isSetFirstName());
		builder.append(present_firstName);
		if (present_firstName)
			builder.append(firstName);

		boolean present_lastName = true && (isSetLastName());
		builder.append(present_lastName);
		if (present_lastName)
			builder.append(lastName);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RealtimeUser(");
		boolean first = true;

		sb.append("userId:");
		sb.append(this.userId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("userNumber:");
		if (this.userNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.userNumber);
		}
		first = false;
		if (isSetFirstName()) {
			if (!first) sb.append(", ");
			sb.append("firstName:");
			if (this.firstName == null) {
				sb.append("null");
			} else {
				sb.append(this.firstName);
			}
			first = false;
		}
		if (isSetLastName()) {
			if (!first) sb.append(", ");
			sb.append("lastName:");
			if (this.lastName == null) {
				sb.append("null");
			} else {
				sb.append(this.lastName);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}