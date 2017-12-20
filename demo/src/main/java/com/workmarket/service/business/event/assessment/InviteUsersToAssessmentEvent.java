package com.workmarket.service.business.event.assessment;

import com.workmarket.service.business.event.Event;

import java.util.HashSet;
import java.util.Set;

public final class InviteUsersToAssessmentEvent extends Event {

	private final Long userId;
	private final Set<String> inviteeUserNumbers;
	private final Long assessmentId;

	private static final long serialVersionUID = -8092814949567116389L;

	public InviteUsersToAssessmentEvent(Long userId, Set<String> inviteeUserNumbers, Long assessmentId) {
		this.userId = userId;
		this.inviteeUserNumbers = new HashSet<>(inviteeUserNumbers);
		this.assessmentId = assessmentId;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getAssessmentId() {
		return assessmentId;
	}

	public Set<String> getInviteeUserNumbers() {
		return inviteeUserNumbers;
	}
}
