package com.workmarket.domains.model.assessment;

import java.util.Calendar;

import javax.persistence.Transient;

public class ManagedAssessment {

	private Long assessmentId;
	private String assessmentName;
	private String description;
	private Integer approximateMinutesDuration;
	private Double passingScore;
	private Long companyId;
	private String companyName;
	private Calendar completedOn;
	/* If the resource completed the test or survey is true, else is false */
	private boolean completed;
	/* If the resource approved the test is true, else is false */
	private boolean passed;
	private Double score;
	private boolean reattemptAllowed;
	private String invitationStatus;
	/* Status attemptStatusTypes.INPROGRESS, attemptStatusTypes.GRADED, attemptStatusTypes.GRADE_PENDING, attemptStatusTypes.COMPLETE */
	private String attemptStatusTypeCode;
	private Calendar invitationDate;
	private Long passedCount;
	private Long gradePendingCount;
	private String status;
	private Double progress;
	private int featured;
	private Long industryId;

	@Transient
	private String companyLogo;

	public Long getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(Long assessmentId) {
		this.assessmentId = assessmentId;
	}

	public String getAssessmentName() {
		return assessmentName;
	}

	public void setAssessmentName(String assessmentName) {
		this.assessmentName = assessmentName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getApproximateMinutesDuration() {
		return approximateMinutesDuration;
	}

	public void setApproximateMinutesDuration(Integer approximateMinutesDuration) {
		this.approximateMinutesDuration = approximateMinutesDuration;
	}

	public Double getPassingScore() {
		return passingScore;
	}

	public void setPassingScore(Double passingScore) {
		this.passingScore = passingScore;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Calendar getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(Calendar completedOn) {
		this.completedOn = completedOn;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public boolean isReattemptAllowed() {
		return reattemptAllowed;
	}

	public void setReattemptAllowed(boolean reattemptAllowed) {
		this.reattemptAllowed = reattemptAllowed;
	}

	public String getInvitationStatus() {
		return invitationStatus;
	}

	public void setInvitationStatus(String invitationStatus) {
		this.invitationStatus = invitationStatus;
	}

	public void setAttemptStatusTypeCode(String attemptStatusTypeCode) {
		this.attemptStatusTypeCode = attemptStatusTypeCode;
	}

	public String getAttemptStatusTypeCode() {
		return attemptStatusTypeCode;
	}

	public void setInvitationDate(Calendar invitationDate) {
		this.invitationDate = invitationDate;
	}

	public Calendar getInvitationDate() {
		return invitationDate;
	}

	public Long getPassedCount() {
		return passedCount;
	}

	public void setPassedCount(Long passedCount) {
		this.passedCount = passedCount;
	}

	public String getCompanyLogo() {
	    return companyLogo;
    }

	public void setCompanyLogo(String companyLogo) {
	    this.companyLogo = companyLogo;
    }

	public Long getGradePendingCount() {
	    return gradePendingCount;
    }

	public void setGradePendingCount(Long gradePendingCount) {
	    this.gradePendingCount = gradePendingCount;
    }

	public Double getProgress() {
	    return progress;
    }

	public void setProgress(Double progress) {
	    this.progress = progress;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    public int getFeatured() {
        return featured;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
    }

	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}
}
