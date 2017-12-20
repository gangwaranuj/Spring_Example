package com.workmarket.service.business.dto;

import java.io.Serializable;

public class CloseWorkDTO implements Serializable {

	private static final long serialVersionUID = -2730615847346484852L;

	private RatingDTO rating;
	private boolean shareRating = true;
	private boolean arrivedOnTime = true;
	private boolean completedOnTime = true;
	private boolean blockResource = false;

	public RatingDTO getRating() {
		return rating;
	}

	public void setRating(RatingDTO rating) {
		this.rating = rating;
	}

	public boolean hasRating() {
		return rating != null && rating.getValue() != null;
	}

	public boolean isShareRating() {
		return shareRating;
	}

	public void setShareRating(boolean shareRating) {
		this.shareRating = shareRating;
	}

	public boolean isArrivedOnTime() {
		return arrivedOnTime;
	}

	public void setArrivedOnTime(boolean arrivedOnTime) {
		this.arrivedOnTime = arrivedOnTime;
	}

	public boolean isCompletedOnTime() {
		return completedOnTime;
	}

	public void setCompletedOnTime(boolean completedOnTime) {
		this.completedOnTime = completedOnTime;
	}

	public boolean isBlockResource() {
		return blockResource;
	}

	public void setBlockResource(boolean blockResource) {
		this.blockResource = blockResource;
	}
}