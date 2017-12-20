package com.workmarket.thrift.search.cart;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class SearchCartResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userNumber;
	private SearchCartActionType actionResult;

	public SearchCartResult() {
	}

	public SearchCartResult(String userNumber, SearchCartActionType actionResult) {
		this();
		this.userNumber = userNumber;
		this.actionResult = actionResult;
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public SearchCartResult setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public SearchCartActionType getActionResult() {
		return this.actionResult;
	}

	public SearchCartResult setActionResult(SearchCartActionType actionResult) {
		this.actionResult = actionResult;
		return this;
	}

	public boolean isSetActionResult() {
		return this.actionResult != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof SearchCartResult)
			return this.equals((SearchCartResult) that);
		return false;
	}

	private boolean equals(SearchCartResult that) {
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

		boolean this_present_actionResult = true && this.isSetActionResult();
		boolean that_present_actionResult = true && that.isSetActionResult();
		if (this_present_actionResult || that_present_actionResult) {
			if (!(this_present_actionResult && that_present_actionResult))
				return false;
			if (!this.actionResult.equals(that.actionResult))
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

		boolean present_actionResult = true && (isSetActionResult());
		builder.append(present_actionResult);
		if (present_actionResult)
			builder.append(actionResult.getValue());

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SearchCartResult(");
		boolean first = true;

		sb.append("userNumber:");
		if (this.userNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.userNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("actionResult:");
		if (this.actionResult == null) {
			sb.append("null");
		} else {
			sb.append(this.actionResult);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

