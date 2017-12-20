package com.workmarket.service.business.event;

public class WorkSubStatusTypeUpdatedEvent extends Event {

	private static final long serialVersionUID = 1L;

	private long userId;
	private long workSubStatusTypeId;

	public WorkSubStatusTypeUpdatedEvent() {}

	public WorkSubStatusTypeUpdatedEvent(long userId, long workSubStatusTypeId) {
		this.userId = userId;
		this.workSubStatusTypeId = workSubStatusTypeId;
	}

	public long getUserId() {
		return userId;
	}

	public long getWorkSubStatusTypeId() {
		return workSubStatusTypeId;
	}
}
