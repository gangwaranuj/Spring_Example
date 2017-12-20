package com.workmarket.thrift.search.cart;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchCartActionResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<String> usersSucceeded;
	private List<String> usersFailed;

	public SearchCartActionResponse() {
	}

	public SearchCartActionResponse(List<String> usersSucceeded, List<String> usersFailed) {
		this();
		this.usersSucceeded = usersSucceeded;
		this.usersFailed = usersFailed;
	}

	public int getUsersSucceededSize() {
		return (this.usersSucceeded == null) ? 0 : this.usersSucceeded.size();
	}

	public java.util.Iterator<String> getUsersSucceededIterator() {
		return (this.usersSucceeded == null) ? null : this.usersSucceeded.iterator();
	}

	public void addToUsersSucceeded(String elem) {
		if (this.usersSucceeded == null) {
			this.usersSucceeded = new ArrayList<String>();
		}
		this.usersSucceeded.add(elem);
	}

	public List<String> getUsersSucceeded() {
		return this.usersSucceeded;
	}

	public SearchCartActionResponse setUsersSucceeded(List<String> usersSucceeded) {
		this.usersSucceeded = usersSucceeded;
		return this;
	}

	public boolean isSetUsersSucceeded() {
		return this.usersSucceeded != null;
	}

	public int getUsersFailedSize() {
		return (this.usersFailed == null) ? 0 : this.usersFailed.size();
	}

	public java.util.Iterator<String> getUsersFailedIterator() {
		return (this.usersFailed == null) ? null : this.usersFailed.iterator();
	}

	public void addToUsersFailed(String elem) {
		if (this.usersFailed == null) {
			this.usersFailed = new ArrayList<String>();
		}
		this.usersFailed.add(elem);
	}

	public List<String> getUsersFailed() {
		return this.usersFailed;
	}

	public SearchCartActionResponse setUsersFailed(List<String> usersFailed) {
		this.usersFailed = usersFailed;
		return this;
	}

	public boolean isSetUsersFailed() {
		return this.usersFailed != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof SearchCartActionResponse)
			return this.equals((SearchCartActionResponse) that);
		return false;
	}

	private boolean equals(SearchCartActionResponse that) {
		if (that == null)
			return false;

		boolean this_present_usersSucceeded = true && this.isSetUsersSucceeded();
		boolean that_present_usersSucceeded = true && that.isSetUsersSucceeded();
		if (this_present_usersSucceeded || that_present_usersSucceeded) {
			if (!(this_present_usersSucceeded && that_present_usersSucceeded))
				return false;
			if (!this.usersSucceeded.equals(that.usersSucceeded))
				return false;
		}

		boolean this_present_usersFailed = true && this.isSetUsersFailed();
		boolean that_present_usersFailed = true && that.isSetUsersFailed();
		if (this_present_usersFailed || that_present_usersFailed) {
			if (!(this_present_usersFailed && that_present_usersFailed))
				return false;
			if (!this.usersFailed.equals(that.usersFailed))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_usersSucceeded = true && (isSetUsersSucceeded());
		builder.append(present_usersSucceeded);
		if (present_usersSucceeded)
			builder.append(usersSucceeded);

		boolean present_usersFailed = true && (isSetUsersFailed());
		builder.append(present_usersFailed);
		if (present_usersFailed)
			builder.append(usersFailed);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SearchCartActionResponse(");
		boolean first = true;

		sb.append("usersSucceeded:");
		if (this.usersSucceeded == null) {
			sb.append("null");
		} else {
			sb.append(this.usersSucceeded);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("usersFailed:");
		if (this.usersFailed == null) {
			sb.append("null");
		} else {
			sb.append(this.usersFailed);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
