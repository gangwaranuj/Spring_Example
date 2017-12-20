package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.ScheduledEvent;

import java.util.Set;

public class WorkResourceInvitation extends ScheduledEvent {

	private static final long serialVersionUID = 5978784408837592612L;
	private final Long workId;
	private final Set<Long> userResourceIds;
	private final boolean voiceDelivery;

	public WorkResourceInvitation(Set<Long> userResourceIds, boolean voiceDelivery, Long workId) {
		this.userResourceIds = userResourceIds;
		this.voiceDelivery = voiceDelivery;
		this.workId = workId;
	}

	public Long getWorkId() {
		return workId;
	}

	public Set<Long> getUserResourceIds() {
		return userResourceIds;
	}

	public boolean isVoiceDelivery() {
		return voiceDelivery;
	}
}
