package com.workmarket.search.request;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class LocationFilter implements Serializable {
	private static final long serialVersionUID = 1L;

	private String willingToTravelTo;
	private int maxMileFromResourceToLocation;
	private boolean anywhere;

	public LocationFilter() {
	}

	public LocationFilter(String willingToTravelTo) {
		this();
		this.willingToTravelTo = willingToTravelTo;
	}

	public String getWillingToTravelTo() {
		return this.willingToTravelTo;
	}

	public LocationFilter setWillingToTravelTo(String willingToTravelTo) {
		this.willingToTravelTo = willingToTravelTo;
		return this;
	}

	public boolean isSetWillingToTravelTo() {
		return this.willingToTravelTo != null;
	}

	public int getMaxMileFromResourceToLocation() {
		return this.maxMileFromResourceToLocation;
	}

	public LocationFilter setMaxMileFromResourceToLocation(int maxMileFromResourceToLocation) {
		this.maxMileFromResourceToLocation = maxMileFromResourceToLocation;
		return this;
	}

	public boolean isSetMaxMileFromResourceToLocation() {
		return (maxMileFromResourceToLocation > 0);
	}

	public boolean isAnywhere() {
		return this.anywhere;
	}

	public LocationFilter setAnywhere(boolean anywhere) {
		this.anywhere = anywhere;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof LocationFilter)
			return this.equals((LocationFilter) that);
		return false;
	}

	private boolean equals(LocationFilter that) {
		if (that == null)
			return false;

		boolean this_present_willingToTravelTo = true && this.isSetWillingToTravelTo();
		boolean that_present_willingToTravelTo = true && that.isSetWillingToTravelTo();
		if (this_present_willingToTravelTo || that_present_willingToTravelTo) {
			if (!(this_present_willingToTravelTo && that_present_willingToTravelTo))
				return false;
			if (!this.willingToTravelTo.equals(that.willingToTravelTo))
				return false;
		}

		boolean this_present_maxMileFromResourceToLocation = true && this.isSetMaxMileFromResourceToLocation();
		boolean that_present_maxMileFromResourceToLocation = true && that.isSetMaxMileFromResourceToLocation();
		if (this_present_maxMileFromResourceToLocation || that_present_maxMileFromResourceToLocation) {
			if (!(this_present_maxMileFromResourceToLocation && that_present_maxMileFromResourceToLocation))
				return false;
			if (this.maxMileFromResourceToLocation != that.maxMileFromResourceToLocation)
				return false;
		}

		if (this.anywhere != that.anywhere)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_willingToTravelTo = true && (isSetWillingToTravelTo());
		builder.append(present_willingToTravelTo);
		if (present_willingToTravelTo)
			builder.append(willingToTravelTo);

		boolean present_maxMileFromResourceToLocation = true && (isSetMaxMileFromResourceToLocation());
		builder.append(present_maxMileFromResourceToLocation);
		if (present_maxMileFromResourceToLocation)
			builder.append(maxMileFromResourceToLocation);

		builder.append(true);
		builder.append(anywhere);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("LocationFilter(");
		boolean first = true;

		sb.append("willingToTravelTo:");
		if (this.willingToTravelTo == null) {
			sb.append("null");
		} else {
			sb.append(this.willingToTravelTo);
		}
		first = false;
		if (isSetMaxMileFromResourceToLocation()) {
			if (!first) sb.append(", ");
			sb.append("maxMileFromResourceToLocation:");
			sb.append(this.maxMileFromResourceToLocation);
			first = false;
		}

		if (!first) sb.append(", ");
		sb.append("anywhere:");
		sb.append(this.anywhere);
		first = false;

		sb.append(")");
		return sb.toString();
	}
}

