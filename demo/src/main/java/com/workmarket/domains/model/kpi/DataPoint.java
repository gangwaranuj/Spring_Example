package com.workmarket.domains.model.kpi;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public final class DataPoint implements Serializable {

	private static final long serialVersionUID = -8783186062430423206L;

	private long x;
	private double y;
	private boolean inProgressPeriod;
	private boolean trendingUp;

	public DataPoint() {
		this.inProgressPeriod = false;
		this.trendingUp = false;
	}

	public DataPoint(
			long x,
			double y,
			boolean inProgressPeriod,
			boolean trendingUp) {
		this();
		this.x = x;
		this.y = y;
		this.inProgressPeriod = inProgressPeriod;
		this.trendingUp = trendingUp;
	}

	public DataPoint(
			long x,
			double y) {
		this();
		this.x = x;
		this.y = y;
	}

	public long getX() {
		return this.x;
	}

	public DataPoint setX(long x) {
		this.x = x;
		return this;
	}

	public boolean isSetX() {
		return (x > 0L);
	}

	public double getY() {
		return this.y;
	}

	public DataPoint setY(double y) {
		this.y = y;
		return this;
	}

	public boolean isSetY() {
		return (y > 0L);
	}

	public boolean isInProgressPeriod() {
		return this.inProgressPeriod;
	}

	public DataPoint setInProgressPeriod(boolean inProgressPeriod) {
		this.inProgressPeriod = inProgressPeriod;
		return this;
	}

	public boolean isTrendingUp() {
		return this.trendingUp;
	}

	public DataPoint setTrendingUp(boolean trendingUp) {
		this.trendingUp = trendingUp;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DataPoint(");
		boolean first = true;

		sb.append("x:");
		sb.append(this.x);
		first = false;
		if (!first) sb.append(", ");
		sb.append("y:");
		sb.append(this.y);
		first = false;
		if (!first) sb.append(", ");
		sb.append("inProgressPeriod:");
		sb.append(this.inProgressPeriod);
		first = false;
		if (!first) sb.append(", ");
		sb.append("trendingUp:");
		sb.append(this.trendingUp);
		first = false;
		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof DataPoint)) {
			return false;
		}

		DataPoint dataPoint = (DataPoint) o;

		if (inProgressPeriod != dataPoint.inProgressPeriod) return false;
		if (trendingUp != dataPoint.trendingUp) return false;
		if (x != dataPoint.x) return false;
		if (Double.compare(dataPoint.y, y) != 0) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(inProgressPeriod)
			.append(trendingUp)
			.append(x)
			.append(y)
			.toHashCode();
	}
}