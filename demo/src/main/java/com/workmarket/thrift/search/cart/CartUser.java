package com.workmarket.thrift.search.cart;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class CartUser implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userNumber;
	private String userName;

	public CartUser() {
	}

	public CartUser(String userNumber, String userName) {
		this();
		this.userNumber = userNumber;
		this.userName = userName;
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public CartUser setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public String getUserName() {
		return this.userName;
	}

	public CartUser setUserName(String userName) {
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
		if (that instanceof CartUser)
			return this.equals((CartUser) that);
		return false;
	}

	private boolean equals(CartUser that) {
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

		boolean present_userNumber = true && (isSetUserNumber());
		builder.append(present_userNumber);
		if (present_userNumber)
			builder.append(userNumber);

		boolean present_userName = true && (isSetUserName());
		builder.append(present_userName);
		if (present_userName)
			builder.append(userName);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("CartUser(");
		boolean first = true;

		sb.append("userNumber:");
		if (this.userNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.userNumber);
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
