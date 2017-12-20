package com.workmarket.thrift.services.realtime;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class TimeFilter implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean greaterThan;
	private long numberOfSeconds;

	public TimeFilter() {
		this.greaterThan = true;
	}

	public TimeFilter(boolean greaterThan, long numberOfSeconds) {
		this();
		this.greaterThan = greaterThan;
		this.numberOfSeconds = numberOfSeconds;
	}

	public boolean isGreaterThan() {
		return this.greaterThan;
	}

	public TimeFilter setGreaterThan(boolean greaterThan) {
		this.greaterThan = greaterThan;
		return this;
	}

	public long getNumberOfSeconds() {
		return this.numberOfSeconds;
	}

	public TimeFilter setNumberOfSeconds(long numberOfSeconds) {
		this.numberOfSeconds = numberOfSeconds;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof TimeFilter)
			return this.equals((TimeFilter) that);
		return false;
	}

	private boolean equals(TimeFilter that) {
		if (that == null)
			return false;

		boolean this_present_greaterThan = true;
		boolean that_present_greaterThan = true;
		if (this_present_greaterThan || that_present_greaterThan) {
			if (!(this_present_greaterThan && that_present_greaterThan))
				return false;
			if (this.greaterThan != that.greaterThan)
				return false;
		}

		boolean this_present_numberOfSeconds = true;
		boolean that_present_numberOfSeconds = true;
		if (this_present_numberOfSeconds || that_present_numberOfSeconds) {
			if (!(this_present_numberOfSeconds && that_present_numberOfSeconds))
				return false;
			if (this.numberOfSeconds != that.numberOfSeconds)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_greaterThan = true;
		builder.append(present_greaterThan);
		if (present_greaterThan)
			builder.append(greaterThan);

		boolean present_numberOfSeconds = true;
		builder.append(present_numberOfSeconds);
		if (present_numberOfSeconds)
			builder.append(numberOfSeconds);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TimeFilter(");
		boolean first = true;

		sb.append("greaterThan:");
		sb.append(this.greaterThan);
		first = false;
		if (!first) sb.append(", ");
		sb.append("numberOfSeconds:");
		sb.append(this.numberOfSeconds);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}