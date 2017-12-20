package com.workmarket.service.business.dto;

public class WorkRatingDTO extends RatingDTO {
	private static final long serialVersionUID = -6541540972208524314L;
	private Long raterUserId;
	private Long ratedUserId;
	private Long workId;

	public WorkRatingDTO(Long raterUserId, Long ratedUserId, Long workId, Integer value, Integer quality, Integer professionalism, Integer communication, String review) {
		super(value, quality, professionalism, communication, review);
		this.raterUserId = raterUserId;
		this.ratedUserId = ratedUserId;
		this.workId = workId;
	}

	public Long getRaterUserId() {
		return this.raterUserId;
	}
	public void setRaterUserId(Long value) {
		this.raterUserId = value;
	}

	public Long getRatedUserId() {
		return this.ratedUserId;
	}

	public void setRatedUserId(Long value) {
		this.ratedUserId = value;
	}

	public Long getWorkId() {
		return this.workId;
	}

	public void setWorkId(Long value) {
		this.workId = value;
	}

}