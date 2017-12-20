package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.Event;

import java.util.List;

public class WorkResendInvitationsEvent extends Event {

	private static final long serialVersionUID = -4116996838381961336L;

	private Long workId;
	private List<Long> resourcesIds;

	public WorkResendInvitationsEvent() {
	}

	public WorkResendInvitationsEvent(Long workId) {
		this.workId = workId;
	}

	public WorkResendInvitationsEvent(Long workId, List<Long> resourcesIds) {
		this.resourcesIds = resourcesIds;
		this.workId = workId;
	}

	public List<Long> getResourcesIds() {
		return resourcesIds;
	}

	public Long getWorkId() {
		return workId;
	}
}
