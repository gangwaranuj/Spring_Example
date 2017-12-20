package com.workmarket.data.solr.model;

public class SolrRatingData {
	private int rating;
	private int ratingCount;
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public int getRatingCount() {
		return ratingCount;
	}
	public void setRatingCount(int ratingCount) {
		this.ratingCount = ratingCount;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rating;
		result = prime * result + ratingCount;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SolrRatingData other = (SolrRatingData) obj;
		if (rating != other.rating)
			return false;
		if (ratingCount != other.ratingCount)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "SolrRatingData [rating=" + rating + ", ratingCount="
				+ ratingCount + "]";
	}
	
}

