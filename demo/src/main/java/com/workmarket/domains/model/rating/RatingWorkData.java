package com.workmarket.domains.model.rating;

import java.util.Date;

/**
 * User: iloveopt
 * Date: 3/24/14
 */
public class RatingWorkData {

	private Integer ratingValue;
	private Integer ratingQuality;
	private Integer ratingProfessionalism;
	private Integer ratingCommunication;
	private String ratingReview;
	private String raterUserName;
	private String ratedUserName;
	private Long workId;
	private Long ratedUserId;
	private Date modifiedOn;

	public String getRaterUserName() {
		return raterUserName;
	}

	public void setRaterUserName(String raterUserName) {
		this.raterUserName = raterUserName;
	}

	public Integer getRatingValue() {
		return ratingValue;
	}

	public void setRatingValue(Integer ratingValue) {
		this.ratingValue = ratingValue;
	}

	public Integer getRatingQuality() {
		return ratingQuality;
	}

	public void setRatingQuality(Integer ratingQuality) {
		this.ratingQuality = ratingQuality;
	}

	public Integer getRatingProfessionalism() {
		return ratingProfessionalism;
	}

	public void setRatingProfessionalism(Integer ratingProfessionalism) {
		this.ratingProfessionalism = ratingProfessionalism;
	}

	public Integer getRatingCommunication() {
		return ratingCommunication;
	}

	public void setRatingCommunication(Integer ratingCommunication) {
		this.ratingCommunication = ratingCommunication;
	}

	public String getRatingReview() {
		return ratingReview;
	}

	public void setRatingReview(String ratingReview) {
		this.ratingReview = ratingReview;
	}

	public String getRatedUserName() {
		return ratedUserName;
	}

	public void setRatedUserName(String ratedUserName) {
		this.ratedUserName = ratedUserName;
	}

	public Long getRatedUserId() {
		return ratedUserId;
	}

	public void setRatedUserId(Long ratedUserId) {
		this.ratedUserId = ratedUserId;
	}

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}


}
