package com.workmarket.thrift.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class RatingSummary implements Serializable {
	private static final long serialVersionUID = 1L;

	private short rating;
	private int numberOfRatings;

	public RatingSummary() {
	}

	public RatingSummary(short rating, int numberOfRatings) {
		this();
		this.rating = rating;
		this.numberOfRatings = numberOfRatings;
	}

	public short getRating() {
		return this.rating;
	}

	public RatingSummary setRating(short rating) {
		this.rating = rating;
		return this;
	}

	public int getNumberOfRatings() {
		return this.numberOfRatings;
	}

	public RatingSummary setNumberOfRatings(int numberOfRatings) {
		this.numberOfRatings = numberOfRatings;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RatingSummary)
			return this.equals((RatingSummary) that);
		return false;
	}

	private boolean equals(RatingSummary that) {
		if (that == null)
			return false;

		boolean this_present_rating = true;
		boolean that_present_rating = true;
		if (this_present_rating || that_present_rating) {
			if (!(this_present_rating && that_present_rating))
				return false;
			if (this.rating != that.rating)
				return false;
		}

		boolean this_present_numberOfRatings = true;
		boolean that_present_numberOfRatings = true;
		if (this_present_numberOfRatings || that_present_numberOfRatings) {
			if (!(this_present_numberOfRatings && that_present_numberOfRatings))
				return false;
			if (this.numberOfRatings != that.numberOfRatings)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_rating = true;
		builder.append(present_rating);
		if (present_rating)
			builder.append(rating);

		boolean present_numberOfRatings = true;
		builder.append(present_numberOfRatings);
		if (present_numberOfRatings)
			builder.append(numberOfRatings);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RatingSummary(");
		boolean first = true;

		sb.append("rating:");
		sb.append(this.rating);
		first = false;
		if (!first) sb.append(", ");
		sb.append("numberOfRatings:");
		sb.append(this.numberOfRatings);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}