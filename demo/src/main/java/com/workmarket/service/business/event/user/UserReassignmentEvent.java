package com.workmarket.service.business.event.user;

import com.workmarket.service.business.event.Event;

public class UserReassignmentEvent extends Event {
	private static final long serialVersionUID = -8817532561028282829L;

	private Long currentUserId;
	private Long nextWorkOwnerId;
	private Long nextGroupOwnerId;
	private Long nextAssessmentOwnerId;
	
	public Long getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Long currentUserId) {
		this.currentUserId = currentUserId;
	}

	public Long getNextWorkOwnerId() {
		return nextWorkOwnerId;
	}

	public void setNextWorkOwnerId(Long nextWorkOwnerId) {
		this.nextWorkOwnerId = nextWorkOwnerId;
	}

	public Long getNextGroupOwnerId() {
		return nextGroupOwnerId;
	}

	public void setNextGroupOwnerId(Long nextGroupOwnerId) {
		this.nextGroupOwnerId = nextGroupOwnerId;
	}

	public Long getNextAssessmentOwnerId() {
		return nextAssessmentOwnerId;
	}

	public void setNextAssessmentOwnerId(Long nextAssessmentOwnerId) {
		this.nextAssessmentOwnerId = nextAssessmentOwnerId;
	}
	
}
