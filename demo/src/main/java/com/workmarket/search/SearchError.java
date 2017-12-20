package com.workmarket.search;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class SearchError implements Serializable {
	private static final long serialVersionUID = 1L;

	private SearchErrorType error;
	private String why;

	public SearchError() {
	}

	public SearchError(SearchErrorType error, String why) {
		this();
		this.error = error;
		this.why = why;
	}

	public SearchErrorType getError() {
		return this.error;
	}

	public SearchError setError(SearchErrorType error) {
		this.error = error;
		return this;
	}

	public boolean isSetError() {
		return this.error != null;
	}

	public String getWhy() {
		return this.why;
	}

	public SearchError setWhy(String why) {
		this.why = why;
		return this;
	}

	public boolean isSetWhy() {
		return this.why != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof SearchError)
			return this.equals((SearchError) that);
		return false;
	}

	private boolean equals(SearchError that) {
		if (that == null)
			return false;

		boolean this_present_error = true && this.isSetError();
		boolean that_present_error = true && that.isSetError();
		if (this_present_error || that_present_error) {
			if (!(this_present_error && that_present_error))
				return false;
			if (!this.error.equals(that.error))
				return false;
		}

		boolean this_present_why = true && this.isSetWhy();
		boolean that_present_why = true && that.isSetWhy();
		if (this_present_why || that_present_why) {
			if (!(this_present_why && that_present_why))
				return false;
			if (!this.why.equals(that.why))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_error = true && (isSetError());
		builder.append(present_error);
		if (present_error)
			builder.append(error.getValue());

		boolean present_why = true && (isSetWhy());
		builder.append(present_why);
		if (present_why)
			builder.append(why);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SearchError(");
		boolean first = true;

		sb.append("error:");
		if (this.error == null) {
			sb.append("null");
		} else {
			sb.append(this.error);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("why:");
		if (this.why == null) {
			sb.append("null");
		} else {
			sb.append(this.why);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}