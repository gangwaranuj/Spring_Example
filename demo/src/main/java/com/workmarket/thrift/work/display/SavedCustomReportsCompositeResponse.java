package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SavedCustomReportsCompositeResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<SavedCustomReportResponse> savedCustomReportResponses;

	public SavedCustomReportsCompositeResponse() {
	}

	public SavedCustomReportsCompositeResponse(List<SavedCustomReportResponse> savedCustomReportResponses) {
		this();
		this.savedCustomReportResponses = savedCustomReportResponses;
	}

	public int getSavedCustomReportResponsesSize() {
		return (this.savedCustomReportResponses == null) ? 0 : this.savedCustomReportResponses.size();
	}

	public java.util.Iterator<SavedCustomReportResponse> getSavedCustomReportResponsesIterator() {
		return (this.savedCustomReportResponses == null) ? null : this.savedCustomReportResponses.iterator();
	}

	public void addToSavedCustomReportResponses(SavedCustomReportResponse elem) {
		if (this.savedCustomReportResponses == null) {
			this.savedCustomReportResponses = new ArrayList<SavedCustomReportResponse>();
		}
		this.savedCustomReportResponses.add(elem);
	}

	public List<SavedCustomReportResponse> getSavedCustomReportResponses() {
		return this.savedCustomReportResponses;
	}

	public SavedCustomReportsCompositeResponse setSavedCustomReportResponses(List<SavedCustomReportResponse> savedCustomReportResponses) {
		this.savedCustomReportResponses = savedCustomReportResponses;
		return this;
	}

	public boolean isSetSavedCustomReportResponses() {
		return this.savedCustomReportResponses != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof SavedCustomReportsCompositeResponse)
			return this.equals((SavedCustomReportsCompositeResponse) that);
		return false;
	}

	private boolean equals(SavedCustomReportsCompositeResponse that) {
		if (that == null)
			return false;

		boolean this_present_savedCustomReportResponses = true && this.isSetSavedCustomReportResponses();
		boolean that_present_savedCustomReportResponses = true && that.isSetSavedCustomReportResponses();
		if (this_present_savedCustomReportResponses || that_present_savedCustomReportResponses) {
			if (!(this_present_savedCustomReportResponses && that_present_savedCustomReportResponses))
				return false;
			if (!this.savedCustomReportResponses.equals(that.savedCustomReportResponses))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_savedCustomReportResponses = true && (isSetSavedCustomReportResponses());
		builder.append(present_savedCustomReportResponses);
		if (present_savedCustomReportResponses)
			builder.append(savedCustomReportResponses);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SavedCustomReportsCompositeResponse(");
		boolean first = true;

		sb.append("savedCustomReportResponses:");
		if (this.savedCustomReportResponses == null) {
			sb.append("null");
		} else {
			sb.append(this.savedCustomReportResponses);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}