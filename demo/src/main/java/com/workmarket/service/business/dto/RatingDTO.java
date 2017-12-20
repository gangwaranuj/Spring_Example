package com.workmarket.service.business.dto;

import java.io.Serializable;

public class RatingDTO implements Serializable {
	private static final long serialVersionUID = 7580385051457636813L;

	private Integer value;
	private Integer quality;
	private Integer professionalism;
	private Integer communication;
	private String review;
	private Boolean ratingSharedFlag = Boolean.TRUE;
	private Boolean reviewSharedFlag = Boolean.TRUE;
	
	public RatingDTO() {}
	public RatingDTO(Integer value) {
		this.value = value;
	}
	public RatingDTO(Integer value, String review) {
		this.value = value;
		this.review = review;
	}

	public RatingDTO(Integer value, Integer quality, Integer professionalism, Integer communication, String review) {
		this.value = value;
		this.quality = quality;
		this.professionalism = professionalism;
		this.communication = communication;
		this.review = review;
	}

    
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer getQuality() {
		return quality;
	}

	public void setQuality(Integer quality) {
		this.quality = quality;
	}

	public Integer getProfessionalism() {
		return professionalism;
	}

	public void setProfessionalism(Integer professionalism) {
		this.professionalism = professionalism;
	}

	public Integer getCommunication() {
		return communication;
	}

	public void setCommunication(Integer communication) {
		this.communication = communication;
	}

	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
	
	public Boolean getRatingSharedFlag() {
		return ratingSharedFlag;
	}
	public void setRatingSharedFlag(Boolean ratingSharedFlag) {
		this.ratingSharedFlag = ratingSharedFlag;
	}
	
	public Boolean getReviewSharedFlag() {
		return reviewSharedFlag;
	}
	public void setReviewSharedFlag(Boolean reviewSharedFlag) {
		this.reviewSharedFlag = reviewSharedFlag;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RatingDTO)) return false;

		RatingDTO ratingDTO = (RatingDTO) o;

		if (communication != null ? !communication.equals(ratingDTO.communication) : ratingDTO.communication != null)
			return false;
		if (professionalism != null ? !professionalism.equals(ratingDTO.professionalism) : ratingDTO.professionalism != null)
			return false;
		if (quality != null ? !quality.equals(ratingDTO.quality) : ratingDTO.quality != null) return false;
		if (ratingSharedFlag != null ? !ratingSharedFlag.equals(ratingDTO.ratingSharedFlag) : ratingDTO.ratingSharedFlag != null)
			return false;
		if (review != null ? !review.equals(ratingDTO.review) : ratingDTO.review != null) return false;
		if (reviewSharedFlag != null ? !reviewSharedFlag.equals(ratingDTO.reviewSharedFlag) : ratingDTO.reviewSharedFlag != null)
			return false;
		if (value != null ? !value.equals(ratingDTO.value) : ratingDTO.value != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = value != null ? value.hashCode() : 0;
		result = 31 * result + (quality != null ? quality.hashCode() : 0);
		result = 31 * result + (professionalism != null ? professionalism.hashCode() : 0);
		result = 31 * result + (communication != null ? communication.hashCode() : 0);
		result = 31 * result + (review != null ? review.hashCode() : 0);
		result = 31 * result + (ratingSharedFlag != null ? ratingSharedFlag.hashCode() : 0);
		result = 31 * result + (reviewSharedFlag != null ? reviewSharedFlag.hashCode() : 0);
		return result;
	}
}