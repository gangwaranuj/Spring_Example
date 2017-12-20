package com.workmarket.search.request.user;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Filter implements Serializable {
	private static final long serialVersionUID = 1L;

	private String filterId;
	private String filterName;
	private boolean active;

	public Filter() {
		this.active = false;
	}

	public Filter(String filterId, String filterName, boolean active) {
		this();
		this.filterId = filterId;
		this.filterName = filterName;
		this.active = active;
	}

	public String getFilterId() {
		return this.filterId;
	}

	public Filter setFilterId(String filterId) {
		this.filterId = filterId;
		return this;
	}

	public boolean isSetFilterId() {
		return this.filterId != null;
	}

	public String getFilterName() {
		return this.filterName;
	}

	public Filter setFilterName(String filterName) {
		this.filterName = filterName;
		return this;
	}

	public boolean isSetFilterName() {
		return this.filterName != null;
	}

	public boolean isActive() {
		return this.active;
	}

	public Filter setActive(boolean active) {
		this.active = active;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Filter)
			return this.equals((Filter) that);
		return false;
	}

	private boolean equals(Filter that) {
		if (that == null)
			return false;

		boolean this_present_filterId = true && this.isSetFilterId();
		boolean that_present_filterId = true && that.isSetFilterId();
		if (this_present_filterId || that_present_filterId) {
			if (!(this_present_filterId && that_present_filterId))
				return false;
			if (!this.filterId.equals(that.filterId))
				return false;
		}

		boolean this_present_filterName = true && this.isSetFilterName();
		boolean that_present_filterName = true && that.isSetFilterName();
		if (this_present_filterName || that_present_filterName) {
			if (!(this_present_filterName && that_present_filterName))
				return false;
			if (!this.filterName.equals(that.filterName))
				return false;
		}

		boolean this_present_active = true;
		boolean that_present_active = true;
		if (this_present_active || that_present_active) {
			if (!(this_present_active && that_present_active))
				return false;
			if (this.active != that.active)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_filterId = true && (isSetFilterId());
		builder.append(present_filterId);
		if (present_filterId)
			builder.append(filterId);

		boolean present_filterName = true && (isSetFilterName());
		builder.append(present_filterName);
		if (present_filterName)
			builder.append(filterName);

		boolean present_active = true;
		builder.append(present_active);
		if (present_active)
			builder.append(active);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Filter(");
		boolean first = true;

		sb.append("filterId:");
		if (this.filterId == null) {
			sb.append("null");
		} else {
			sb.append(this.filterId);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("filterName:");
		if (this.filterName == null) {
			sb.append("null");
		} else {
			sb.append(this.filterName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("active:");
		sb.append(this.active);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

