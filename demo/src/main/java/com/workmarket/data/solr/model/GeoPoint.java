package com.workmarket.data.solr.model;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class GeoPoint implements Serializable {
	private static final long serialVersionUID = 1L;

	private double latitude;
	private double longitude;

	public GeoPoint() {
	}

	public GeoPoint(double latitude, double longitude) {
		this();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public GeoPoint setLatitude(double latitude) {
		this.latitude = latitude;
		return this;
	}

	public boolean isSetLatitude() {
		return (latitude > 0D);
	}

	public double getLongitude() {
		return this.longitude;
	}

	public GeoPoint setLongitude(double longitude) {
		this.longitude = longitude;
		return this;
	}

	public boolean isSetLongitude() {
		return (longitude > 0D);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null) {
			return false;
		}
		return that instanceof GeoPoint && this.equals((GeoPoint) that);
	}

	private boolean equals(GeoPoint that) {
		if (that == null)
			return false;

		boolean this_present_latitude = true;
		boolean that_present_latitude = true;
		if (this_present_latitude || that_present_latitude) {
			if (!(this_present_latitude && that_present_latitude))
				return false;
			if (this.latitude != that.latitude)
				return false;
		}

		boolean this_present_longitude = true;
		boolean that_present_longitude = true;
		if (this_present_longitude || that_present_longitude) {
			if (!(this_present_longitude && that_present_longitude))
				return false;
			if (this.longitude != that.longitude)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_latitude = true;
		builder.append(present_latitude);
		if (present_latitude)
			builder.append(latitude);

		boolean present_longitude = true;
		builder.append(present_longitude);
		if (present_longitude)
			builder.append(longitude);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("GeoPoint(");
		boolean first = true;

		sb.append("latitude:");
		sb.append(this.latitude);
		first = false;
		if (!first) sb.append(", ");
		sb.append("longitude:");
		sb.append(this.longitude);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

