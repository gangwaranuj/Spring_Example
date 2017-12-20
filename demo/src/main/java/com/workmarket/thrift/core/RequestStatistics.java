package com.workmarket.thrift.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class RequestStatistics implements Serializable {
	private static final long serialVersionUID = 1L;

	private long timeToRun;
	private long beginTime;
	private long endTime;

	public RequestStatistics() {
	}

	public RequestStatistics(long timeToRun, long beginTime, long endTime) {
		this();
		this.timeToRun = timeToRun;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}

	public long getTimeToRun() {
		return this.timeToRun;
	}

	public RequestStatistics setTimeToRun(long timeToRun) {
		this.timeToRun = timeToRun;
		return this;
	}

	public boolean isSetTimeToRun() {
		return (timeToRun > 0L);
	}

	public long getBeginTime() {
		return this.beginTime;
	}

	public RequestStatistics setBeginTime(long beginTime) {
		this.beginTime = beginTime;
		return this;
	}

	public boolean isSetBeginTime() {
		return (beginTime > 0L);
	}

	public long getEndTime() {
		return this.endTime;
	}

	public RequestStatistics setEndTime(long endTime) {
		this.endTime = endTime;
		return this;
	}

	public boolean isSetEndTime() {
		return (endTime > 0L);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RequestStatistics)
			return this.equals((RequestStatistics) that);
		return false;
	}

	private boolean equals(RequestStatistics that) {
		if (that == null)
			return false;

		boolean this_present_timeToRun = true;
		boolean that_present_timeToRun = true;
		if (this_present_timeToRun || that_present_timeToRun) {
			if (!(this_present_timeToRun && that_present_timeToRun))
				return false;
			if (this.timeToRun != that.timeToRun)
				return false;
		}

		boolean this_present_beginTime = true;
		boolean that_present_beginTime = true;
		if (this_present_beginTime || that_present_beginTime) {
			if (!(this_present_beginTime && that_present_beginTime))
				return false;
			if (this.beginTime != that.beginTime)
				return false;
		}

		boolean this_present_endTime = true;
		boolean that_present_endTime = true;
		if (this_present_endTime || that_present_endTime) {
			if (!(this_present_endTime && that_present_endTime))
				return false;
			if (this.endTime != that.endTime)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_timeToRun = true;
		builder.append(present_timeToRun);
		if (present_timeToRun)
			builder.append(timeToRun);

		boolean present_beginTime = true;
		builder.append(present_beginTime);
		if (present_beginTime)
			builder.append(beginTime);

		boolean present_endTime = true;
		builder.append(present_endTime);
		if (present_endTime)
			builder.append(endTime);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RequestStatistics(");
		boolean first = true;

		sb.append("timeToRun:");
		sb.append(this.timeToRun);
		first = false;
		if (!first) sb.append(", ");
		sb.append("beginTime:");
		sb.append(this.beginTime);
		first = false;
		if (!first) sb.append(", ");
		sb.append("endTime:");
		sb.append(this.endTime);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}