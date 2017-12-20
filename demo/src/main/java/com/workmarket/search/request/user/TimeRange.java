package com.workmarket.search.request.user;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class TimeRange implements Serializable {
	private static final long serialVersionUID = 1L;

	private short from;
	private short to;

	public TimeRange() {
	}

	public short getFrom() {
		return this.from;
	}

	public TimeRange setFrom(short from) {
		this.from = from;
		return this;
	}

	public boolean isSetFrom() {
		return (from > 0);
	}

	public short getTo() {
		return this.to;
	}

	public TimeRange setTo(short to) {
		this.to = to;
		return this;
	}

	public boolean isSetTo() {
		return (to > 0);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof TimeRange)
			return this.equals((TimeRange) that);
		return false;
	}

	private boolean equals(TimeRange that) {
		if (that == null)
			return false;

		boolean this_present_from = true && this.isSetFrom();
		boolean that_present_from = true && that.isSetFrom();
		if (this_present_from || that_present_from) {
			if (!(this_present_from && that_present_from))
				return false;
			if (this.from != that.from)
				return false;
		}

		boolean this_present_to = true && this.isSetTo();
		boolean that_present_to = true && that.isSetTo();
		if (this_present_to || that_present_to) {
			if (!(this_present_to && that_present_to))
				return false;
			if (this.to != that.to)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_from = true && (isSetFrom());
		builder.append(present_from);
		if (present_from)
			builder.append(from);

		boolean present_to = true && (isSetTo());
		builder.append(present_to);
		if (present_to)
			builder.append(to);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TimeRange(");
		boolean first = true;

		if (isSetFrom()) {
			sb.append("from:");
			sb.append(this.from);
			first = false;
		}
		if (isSetTo()) {
			if (!first) sb.append(", ");
			sb.append("to:");
			sb.append(this.to);
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}