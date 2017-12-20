package com.workmarket.thrift.search.cart;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userNumber;
	private List<CartUser> users;

	public Cart() {
	}

	public Cart(String userNumber, List<CartUser> users) {
		this();
		this.userNumber = userNumber;
		this.users = users;
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public Cart setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public int getUsersSize() {
		return (this.users == null) ? 0 : this.users.size();
	}

	public java.util.Iterator<CartUser> getUsersIterator() {
		return (this.users == null) ? null : this.users.iterator();
	}

	public void addToUsers(CartUser elem) {
		if (this.users == null) {
			this.users = new ArrayList<CartUser>();
		}
		this.users.add(elem);
	}

	public List<CartUser> getUsers() {
		return this.users;
	}

	public Cart setUsers(List<CartUser> users) {
		this.users = users;
		return this;
	}

	public boolean isSetUsers() {
		return this.users != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Cart)
			return this.equals((Cart) that);
		return false;
	}

	private boolean equals(Cart that) {
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

		boolean this_present_users = true && this.isSetUsers();
		boolean that_present_users = true && that.isSetUsers();
		if (this_present_users || that_present_users) {
			if (!(this_present_users && that_present_users))
				return false;
			if (!this.users.equals(that.users))
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

		boolean present_users = true && (isSetUsers());
		builder.append(present_users);
		if (present_users)
			builder.append(users);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Cart(");
		boolean first = true;

		sb.append("userNumber:");
		if (this.userNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.userNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("users:");
		if (this.users == null) {
			sb.append("null");
		} else {
			sb.append(this.users);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

