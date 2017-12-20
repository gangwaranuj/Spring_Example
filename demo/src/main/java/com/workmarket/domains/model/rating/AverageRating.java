package com.workmarket.domains.model.rating;

import java.io.Serializable;

public class AverageRating implements Serializable {

	private static final long serialVersionUID = -7753035317189255789L;
	private Double average;
	private Long count;

	public AverageRating() {
	}

	public AverageRating(Double average, Long count) {
		this.average = average;
		this.count = count;
	}

	public Double getAverage() {
		return average;
	}

	public void setAverage(Double average) {
		this.average = average;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AverageRating)) return false;

		AverageRating that = (AverageRating) o;

		if (!average.equals(that.average)) return false;
		if (!count.equals(that.count)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = average.hashCode();
		result = 31 * result + count.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "AverageRating [average=" + average + ", count=" + count + ", ratedUserId=" + "]";
	}
}
