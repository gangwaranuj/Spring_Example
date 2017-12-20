package com.workmarket.domains.model.summary.user;

import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.summary.HistorySummaryEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "userRatingChangeLogSummary")
@Table(name = "user_rating_changelog_summary")
public class UserRatingChangeLogSummary extends HistorySummaryEntity {

	private static final long serialVersionUID = -911123779484377200L;

	private Long ratingId;
	private Long raterUserId;
	private Long raterCompanyId;
	private Long ratedUserId;
	private Long ratedCompanyId;
	private Long ratedIndustryId;

	private Integer ratingValue;
	private Integer qualityValue;
	private Integer professionalismValue;
	private Integer communicationValue;
	private String review;
	private Long workId;
	private WorkStatusType workStatusType;
	private boolean ratingSharedFlag = true;
	private boolean reviewSharedFlag = true;
	private boolean buyerRating = false;

	@Column(name = "rating_id", nullable = false)
	public Long getRatingId() {
		return ratingId;
	}

	public void setRatingId(Long ratingId) {
		this.ratingId = ratingId;
	}

	@Column(name = "review_shared_flag", nullable = false)
	public boolean isReviewSharedFlag() {
		return reviewSharedFlag;
	}

	public void setReviewSharedFlag(boolean reviewSharedFlag) {
		this.reviewSharedFlag = reviewSharedFlag;
	}

	@Column(name = "buyer_rating", nullable = false)
	public boolean isBuyerRating() {
		return buyerRating;
	}

	public void setBuyerRating(boolean buyerRating) {
		this.buyerRating = buyerRating;
	}

	@Column(name = "rated_company_id", nullable = false)
	public Long getRatedCompanyId() {
		return ratedCompanyId;
	}

	public void setRatedCompanyId(Long ratedCompanyId) {
		this.ratedCompanyId = ratedCompanyId;
	}

	@Column(name = "rated_industry_id", nullable = false)
	public Long getRatedIndustryId() {
		return ratedIndustryId;
	}

	public void setRatedIndustryId(Long ratedIndustryId) {
		this.ratedIndustryId = ratedIndustryId;
	}

	@Column(name = "rated_user_id", nullable = false)
	public Long getRatedUserId() {
		return ratedUserId;
	}

	public void setRatedUserId(Long ratedUserId) {
		this.ratedUserId = ratedUserId;
	}

	@Column(name = "rater_company_id", nullable = false)
	public Long getRaterCompanyId() {
		return raterCompanyId;
	}

	public void setRaterCompanyId(Long raterCompanyId) {
		this.raterCompanyId = raterCompanyId;
	}

	@Column(name = "rater_user_id", nullable = false)
	public Long getRaterUserId() {
		return raterUserId;
	}

	public void setRaterUserId(Long raterUserId) {
		this.raterUserId = raterUserId;
	}

	@Column(name = "rating_shared_flag", nullable = false)
	public boolean isRatingSharedFlag() {
		return ratingSharedFlag;
	}

	public void setRatingSharedFlag(boolean ratingSharedFlag) {
		this.ratingSharedFlag = ratingSharedFlag;
	}

	@Column(name = "rating_value", nullable = false)
	public Integer getRatingValue() {
		return ratingValue;
	}

	public void setRatingValue(Integer ratingValue) {
		this.ratingValue = ratingValue;
	}

	@Column(name = "quality_value", nullable = false)
	public Integer getQualityValue() {
		return qualityValue;
	}

	public void setQualityValue(Integer qualityValue) {
		this.qualityValue = qualityValue;
	}

	@Column(name = "professionalism_value", nullable = false)
	public Integer getProfessionalismValue() {
		return professionalismValue;
	}

	public void setProfessionalismValue(Integer professionalismValue) {
		this.professionalismValue = professionalismValue;
	}

	@Column(name = "communication_value", nullable = false)
	public Integer getCommunicationValue() {
		return communicationValue;
	}

	public void setCommunicationValue(Integer communicationValue) {
		this.communicationValue = communicationValue;
	}

	@Column(name = "review", nullable = false)
	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	@Column(name = "work_id", nullable = false)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_status_type_code", referencedColumnName = "code", nullable = true)
	public WorkStatusType getWorkStatusType() {
		return workStatusType;
	}

	public void setWorkStatusType(WorkStatusType workStatusType) {
		this.workStatusType = workStatusType;
	}

}
