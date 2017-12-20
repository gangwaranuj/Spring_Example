package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkReportEntityBucketResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String displayName;
	private String keyName;
	private List<FilteringEntityResponse> filteringEntityResponses;
	private LocationOrderResponse locationOrderResponse;

	public WorkReportEntityBucketResponse() {
	}

	public WorkReportEntityBucketResponse(
			String displayName,
			String keyName,
			List<FilteringEntityResponse> filteringEntityResponses,
			LocationOrderResponse locationOrderResponse) {
		this();
		this.displayName = displayName;
		this.keyName = keyName;
		this.filteringEntityResponses = filteringEntityResponses;
		this.locationOrderResponse = locationOrderResponse;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public WorkReportEntityBucketResponse setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public boolean isSetDisplayName() {
		return this.displayName != null;
	}

	public String getKeyName() {
		return this.keyName;
	}

	public WorkReportEntityBucketResponse setKeyName(String keyName) {
		this.keyName = keyName;
		return this;
	}

	public boolean isSetKeyName() {
		return this.keyName != null;
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

	public WorkReportEntityBucketResponse setFilteringEntityResponses(List<FilteringEntityResponse> filteringEntityResponses) {
		this.filteringEntityResponses = filteringEntityResponses;
		return this;
	}

	public boolean isSetFilteringEntityResponses() {
		return this.filteringEntityResponses != null;
	}

	public LocationOrderResponse getLocationOrderResponse() {
		return this.locationOrderResponse;
	}

	public WorkReportEntityBucketResponse setLocationOrderResponse(LocationOrderResponse locationOrderResponse) {
		this.locationOrderResponse = locationOrderResponse;
		return this;
	}

	public boolean isSetLocationOrderResponse() {
		return this.locationOrderResponse != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkReportEntityBucketResponse)
			return this.equals((WorkReportEntityBucketResponse) that);
		return false;
	}

	private boolean equals(WorkReportEntityBucketResponse that) {
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

		boolean this_present_keyName = true && this.isSetKeyName();
		boolean that_present_keyName = true && that.isSetKeyName();
		if (this_present_keyName || that_present_keyName) {
			if (!(this_present_keyName && that_present_keyName))
				return false;
			if (!this.keyName.equals(that.keyName))
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

		boolean this_present_locationOrderResponse = true && this.isSetLocationOrderResponse();
		boolean that_present_locationOrderResponse = true && that.isSetLocationOrderResponse();
		if (this_present_locationOrderResponse || that_present_locationOrderResponse) {
			if (!(this_present_locationOrderResponse && that_present_locationOrderResponse))
				return false;
			if (!this.locationOrderResponse.equals(that.locationOrderResponse))
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

		boolean present_keyName = true && (isSetKeyName());
		builder.append(present_keyName);
		if (present_keyName)
			builder.append(keyName);

		boolean present_filteringEntityResponses = true && (isSetFilteringEntityResponses());
		builder.append(present_filteringEntityResponses);
		if (present_filteringEntityResponses)
			builder.append(filteringEntityResponses);

		boolean present_locationOrderResponse = true && (isSetLocationOrderResponse());
		builder.append(present_locationOrderResponse);
		if (present_locationOrderResponse)
			builder.append(locationOrderResponse);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkReportEntityBucketResponse(");
		boolean first = true;

		sb.append("displayName:");
		if (this.displayName == null) {
			sb.append("null");
		} else {
			sb.append(this.displayName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("keyName:");
		if (this.keyName == null) {
			sb.append("null");
		} else {
			sb.append(this.keyName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("filteringEntityResponses:");
		if (this.filteringEntityResponses == null) {
			sb.append("null");
		} else {
			sb.append(this.filteringEntityResponses);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("locationOrderResponse:");
		if (this.locationOrderResponse == null) {
			sb.append("null");
		} else {
			sb.append(this.locationOrderResponse);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}