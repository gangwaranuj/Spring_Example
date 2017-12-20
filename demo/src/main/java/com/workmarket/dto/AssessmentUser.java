package com.workmarket.dto;

import java.util.Calendar;

public class AssessmentUser extends AbstractCustomUserEntity {

	private Calendar dateAdded;
	private String invitationStatus;

	private Long attemptId;
	private String attemptStatus;
	private Calendar createdOn;
	private Calendar invitedOn;
	private Calendar completedOn;
	private Calendar gradedOn;
	private Boolean passedFlag;
	private Double score;
	private Long workId;
	private String workNumber;
	private String workTitle;
	private Long assessmentId;

	public Calendar getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Calendar dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getInvitationStatus() {
		return invitationStatus;
	}

	public void setInvitationStatus(String invitationStatus) {
		this.invitationStatus = invitationStatus;
	}

	public Long getAttemptId() {
		return attemptId;
	}

	public void setAttemptId(Long attemptId) {
		this.attemptId = attemptId;
	}

	public String getAttemptStatus() {
		return attemptStatus;
	}

	public void setAttemptStatus(String attemptStatus) {
		this.attemptStatus = attemptStatus;
	}

	public Calendar getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
	}

	public Calendar getInvitedOn() {
		return invitedOn;
	}

	public void setInvitedOn(Calendar invitedOn) {
		this.invitedOn = invitedOn;
	}

	public Calendar getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(Calendar completedOn) {
		this.completedOn = completedOn;
	}

	public Calendar getGradedOn() {
		return gradedOn;
	}

	public void setGradedOn(Calendar gradedOn) {
		this.gradedOn = gradedOn;
	}

	public Boolean getPassedFlag() {
		return passedFlag;
	}

	public void setPassedFlag(Boolean passedFlag) {
		this.passedFlag = passedFlag;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public Long getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(Long assessmentId) {
		this.assessmentId = assessmentId;
	}

}
