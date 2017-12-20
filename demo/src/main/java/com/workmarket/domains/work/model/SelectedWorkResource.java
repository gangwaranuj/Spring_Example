package com.workmarket.domains.work.model;

/**
 * Author: rocio
 */
public class SelectedWorkResource implements Comparable<SelectedWorkResource> {

	long userId;
	double score;

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Override
	public int compareTo(SelectedWorkResource o) {
		if(this.score != o.score)
			return Double.compare(o.score, this.score); //sorting in desc order.
		if(this.userId != o.userId)
			return Long.valueOf(this.userId).intValue() - Long.valueOf(o.userId).intValue(); //asc
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SelectedWorkResource)) return false;

		SelectedWorkResource that = (SelectedWorkResource) o;

		if (Double.compare(that.score, score) != 0) return false;
		if (userId != that.userId) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = (int) (userId ^ (userId >>> 32));
		temp = Double.doubleToLongBits(score);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
