package com.workmarket.search.response;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class FacetResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private String facetName;
	private String facetId;
	private long facetCount;
	private boolean active;

	public FacetResult() {
	}

	public FacetResult(String facetName, String facetId, long facetCount, boolean active) {
		this();
		this.facetName = facetName;
		this.facetId = facetId;
		this.facetCount = facetCount;
		this.active = active;
	}

	public String getFacetName() {
		return this.facetName;
	}

	public FacetResult setFacetName(String facetName) {
		this.facetName = facetName;
		return this;
	}

	public boolean isSetFacetName() {
		return this.facetName != null;
	}

	public String getFacetId() {
		return this.facetId;
	}

	public FacetResult setFacetId(String facetId) {
		this.facetId = facetId;
		return this;
	}

	public boolean isSetFacetId() {
		return this.facetId != null;
	}

	public long getFacetCount() {
		return this.facetCount;
	}

	public FacetResult setFacetCount(long facetCount) {
		this.facetCount = facetCount;
		return this;
	}

	public boolean isSetFacetCount() {
		return (facetCount > 0L);
	}

	public boolean isActive() {
		return this.active;
	}

	public FacetResult setActive(boolean active) {
		this.active = active;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof FacetResult)
			return this.equals((FacetResult) that);
		return false;
	}

	private boolean equals(FacetResult that) {
		if (that == null)
			return false;

		boolean this_present_facetName = true && this.isSetFacetName();
		boolean that_present_facetName = true && that.isSetFacetName();
		if (this_present_facetName || that_present_facetName) {
			if (!(this_present_facetName && that_present_facetName))
				return false;
			if (!this.facetName.equals(that.facetName))
				return false;
		}

		boolean this_present_facetId = true && this.isSetFacetId();
		boolean that_present_facetId = true && that.isSetFacetId();
		if (this_present_facetId || that_present_facetId) {
			if (!(this_present_facetId && that_present_facetId))
				return false;
			if (!this.facetId.equals(that.facetId))
				return false;
		}

		boolean this_present_facetCount = true;
		boolean that_present_facetCount = true;
		if (this_present_facetCount || that_present_facetCount) {
			if (!(this_present_facetCount && that_present_facetCount))
				return false;
			if (this.facetCount != that.facetCount)
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

		boolean present_facetName = true && (isSetFacetName());
		builder.append(present_facetName);
		if (present_facetName)
			builder.append(facetName);

		boolean present_facetId = true && (isSetFacetId());
		builder.append(present_facetId);
		if (present_facetId)
			builder.append(facetId);

		boolean present_facetCount = true;
		builder.append(present_facetCount);
		if (present_facetCount)
			builder.append(facetCount);

		boolean present_active = true;
		builder.append(present_active);
		if (present_active)
			builder.append(active);

		return builder.toHashCode();
	}

	@Override public String toString() {
		return "FacetResult{" +
				"active=" + active +
				", facetCount=" + facetCount +
				", facetId='" + facetId + '\'' +
				'}';
	}
}