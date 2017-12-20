package com.workmarket.domains.work.model;

import com.workmarket.domains.model.DateRange;

import java.util.List;

public class WorkResourceFeedbackRow {
	private Long workId;
	private String workNumber;
	private String workTitle;
	private DateRange workSchedule;
	private Long companyId;
	private String companyName;

	private Long workResourceUserId;
	private Long workResourceId;
	private Integer ratingValue;
	private Integer qualityValue;
	private Integer professionalismValue;
	private Integer communicationValue;
	private String ratingReview;
	private List<WorkResourceLabel> resourceLabels;

	public Long getWorkId() {
		return workId;
	}

	public WorkResourceFeedbackRow setWorkId(Long workId) {
		this.workId = workId;
		return this;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public WorkResourceFeedbackRow setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public WorkResourceFeedbackRow setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
		return this;
	}

	public DateRange getWorkSchedule() {
		return workSchedule;
	}

	public WorkResourceFeedbackRow setWorkSchedule(DateRange workSchedule) {
		this.workSchedule = workSchedule;
		return this;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public WorkResourceFeedbackRow setCompanyId(Long companyId) {
		this.companyId = companyId;
		return this;
	}

	public String getCompanyName() {
		return companyName;
	}

	public WorkResourceFeedbackRow setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}

	public Long getWorkResourceUserId() {
		return workResourceUserId;
	}

	public WorkResourceFeedbackRow setWorkResourceUserId(Long workResourceUserId) {
		this.workResourceUserId = workResourceUserId;
		return this;
	}

	public Long getWorkResourceId() {
		return workResourceId;
	}

	public WorkResourceFeedbackRow setWorkResourceId(Long workResourceId) {
		this.workResourceId = workResourceId;
		return this;
	}

	public Integer getRatingValue() {
		return ratingValue;
	}

	public WorkResourceFeedbackRow setRatingValue(Integer ratingValue) {
		this.ratingValue = ratingValue;
		return this;
	}

	public Integer getQualityValue() {
		return qualityValue;
	}

	public void setQualityValue(Integer qualityValue) {
		this.qualityValue = qualityValue;
	}

	public Integer getProfessionalismValue() {
		return professionalismValue;
	}

	public void setProfessionalismValue(Integer professionalismValue) {
		this.professionalismValue = professionalismValue;
	}

	public Integer getCommunicationValue() {
		return communicationValue;
	}

	public void setCommunicationValue(Integer communicationValue) {
		this.communicationValue = communicationValue;
	}

	public String getRatingReview() {
		return ratingReview;
	}

	public WorkResourceFeedbackRow setRatingReview(String ratingReview) {
		this.ratingReview = ratingReview;
		return this;
	}

	public List<WorkResourceLabel> getResourceLabels() {
		return resourceLabels;
	}

	public WorkResourceFeedbackRow setResourceLabels(List<WorkResourceLabel> resourceLabels) {
		this.resourceLabels = resourceLabels;
		return this;
	}
}
