package com.workmarket.domains.model.assessment;

import org.springframework.util.Assert;

public class RequiredAssessmentAttemptStatus {
	private Long id;
	private AttemptStatusType attemptStatus;
	private boolean reattemptAllowed = true;

	public Long getId() {
		return id;
	}
	public RequiredAssessmentAttemptStatus setId(Long id) {
		this.id = id;
		return this;
	}

	public AttemptStatusType getAttemptStatus() {
		return attemptStatus;
	}
	public RequiredAssessmentAttemptStatus setAttemptStatus(AttemptStatusType attemptStatus) {
		this.attemptStatus = attemptStatus;
		return this;
	}

	public boolean isReattemptAllowed() {
		return reattemptAllowed;
	}
	public RequiredAssessmentAttemptStatus setReattemptAllowed(boolean reattemptAllowed) {
		this.reattemptAllowed = reattemptAllowed;
		return this;
	}

	public boolean isInProgress() {
		return attemptStatus != null && attemptStatus.isInProgress();
	}

	public boolean isGradePending() {
		return attemptStatus != null && attemptStatus.isGradePending();
	}

	public boolean isGraded() {
		return attemptStatus != null && attemptStatus.isGraded();
	}

	public static RequiredAssessmentAttemptStatus newInstance(AssessmentUserAssociation a) {
		Assert.notNull(a);
		return new RequiredAssessmentAttemptStatus()
			.setId(a.getAssessment().getId())
			.setAttemptStatus(AttemptStatusType.valueOf(a.getStatus().getCode()))
			.setReattemptAllowed(a.getReattemptAllowedFlag());
	}

	public static RequiredAssessmentAttemptStatus newInstance(Long id) {
		return new RequiredAssessmentAttemptStatus().setId(id);
	}
}