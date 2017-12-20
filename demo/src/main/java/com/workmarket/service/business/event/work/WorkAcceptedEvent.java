package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.Event;

public class WorkAcceptedEvent extends Event {

	private static final long serialVersionUID = 1596908687628743878L;

	private Long resourceUserId;
	private Long workId;
	private String assignmentHTML;

	public WorkAcceptedEvent(Long resourceUserId, Long workId, String assignmentHTML) {
		this.resourceUserId = resourceUserId;
		this.workId = workId;
		this.assignmentHTML = assignmentHTML;
	}

	public WorkAcceptedEvent() {
		super();
	}

	public Long getResourceUserId() {
		return resourceUserId;
	}

	public Long getWorkId() {
		return workId;
	}

	public String getAssignmentHTML() {
		return assignmentHTML;
	}
}
