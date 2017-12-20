package com.workmarket.search;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class SearchWarning implements Serializable {
	private static final long serialVersionUID = 1L;

	private SearchWarningType searchWarning;
	private String warningMessage;

	public SearchWarning() {
	}

	public SearchWarning(SearchWarningType searchWarning, String warningMessage) {
		this();
		this.searchWarning = searchWarning;
		this.warningMessage = warningMessage;
	}

	public SearchWarningType getSearchWarning() {
		return this.searchWarning;
	}

	public SearchWarning setSearchWarning(SearchWarningType searchWarning) {
		this.searchWarning = searchWarning;
		return this;
	}

	public boolean isSetSearchWarning() {
		return this.searchWarning != null;
	}

	public String getWarningMessage() {
		return this.warningMessage;
	}

	public SearchWarning setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
		return this;
	}

	public boolean isSetWarningMessage() {
		return this.warningMessage != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof SearchWarning)
			return this.equals((SearchWarning) that);
		return false;
	}

	private boolean equals(SearchWarning that) {
		if (that == null)
			return false;

		boolean this_present_searchWarning = true && this.isSetSearchWarning();
		boolean that_present_searchWarning = true && that.isSetSearchWarning();
		if (this_present_searchWarning || that_present_searchWarning) {
			if (!(this_present_searchWarning && that_present_searchWarning))
				return false;
			if (!this.searchWarning.equals(that.searchWarning))
				return false;
		}

		boolean this_present_warningMessage = true && this.isSetWarningMessage();
		boolean that_present_warningMessage = true && that.isSetWarningMessage();
		if (this_present_warningMessage || that_present_warningMessage) {
			if (!(this_present_warningMessage && that_present_warningMessage))
				return false;
			if (!this.warningMessage.equals(that.warningMessage))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_searchWarning = true && (isSetSearchWarning());
		builder.append(present_searchWarning);
		if (present_searchWarning)
			builder.append(searchWarning.getValue());

		boolean present_warningMessage = true && (isSetWarningMessage());
		builder.append(present_warningMessage);
		if (present_warningMessage)
			builder.append(warningMessage);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SearchWarning(");
		boolean first = true;

		sb.append("searchWarning:");
		if (this.searchWarning == null) {
			sb.append("null");
		} else {
			sb.append(this.searchWarning);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("warningMessage:");
		if (this.warningMessage == null) {
			sb.append("null");
		} else {
			sb.append(this.warningMessage);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}