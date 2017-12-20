package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilteringEntityCompositeResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String displayName;
	private List<FilteringEntityResponse> filteringEntityResponses;

	public FilteringEntityCompositeResponse() {
	}

	public FilteringEntityCompositeResponse(String displayName, List<FilteringEntityResponse> filteringEntityResponses) {
		this();
		this.displayName = displayName;
		this.filteringEntityResponses = filteringEntityResponses;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public FilteringEntityCompositeResponse setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public boolean isSetDisplayName() {
		return this.displayName != null;
	}

	public int getFilteringEntityResponsesSize() {
		return (this.filteringEntityResponses == null) ? 0 : this.filteringEntityResponses.size();
	}

	public java.util.Iterator<FilteringEntityResponse> getFilteringEntityResponsesIterator() {
		return (this.filteringEntityResponses == null) ? null : this.filteringEntityResponses.iterator();
	}

	public void addToFilteringEntityResponses(FilteringEntityResponse elem) {
		if (this.filteringEntityResponses == null) {
			this.filteringEntityResponses = new ArrayList<FilteringEntityResponse>();
		}
		this.filteringEntityResponses.add(elem);
	}

	public List<FilteringEntityResponse> getFilteringEntityResponses() {
		return this.filteringEntityResponses;
	}

	public FilteringEntityCompositeResponse setFilteringEntityResponses(List<FilteringEntityResponse> filteringEntityResponses) {
		this.filteringEntityResponses = filteringEntityResponses;
		return this;
	}

	public boolean isSetFilteringEntityResponses() {
		return this.filteringEntityResponses != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof FilteringEntityCompositeResponse)
			return this.equals((FilteringEntityCompositeResponse) that);
		return false;
	}

	private boolean equals(FilteringEntityCompositeResponse that) {
		if (that == null)
			return false;

		boolean this_present_displayName = true && this.isSetDisplayName();
		boolean that_present_displayName = true && that.isSetDisplayName();
		if (this_present_displayName || that_present_displayName) {
			if (!(this_present_displayName && that_present_displayName))
				return false;
			if (!this.displayName.equals(that.displayName))
				return false;
		}

		boolean this_present_filteringEntityResponses = true && this.isSetFilteringEntityResponses();
		boolean that_present_filteringEntityResponses = true && that.isSetFilteringEntityResponses();
		if (this_present_filteringEntityResponses || that_present_filteringEntityResponses) {
			if (!(this_present_filteringEntityResponses && that_present_filteringEntityResponses))
				return false;
			if (!this.filteringEntityResponses.equals(that.filteringEntityResponses))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_displayName = true && (isSetDisplayName());
		builder.append(present_displayName);
		if (present_displayName)
			builder.append(displayName);

		boolean present_filteringEntityResponses = true && (isSetFilteringEntityResponses());
		builder.append(present_filteringEntityResponses);
		if (present_filteringEntityResponses)
			builder.append(filteringEntityResponses);

		return builder.toHashCode();
	}
}