package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.Event;

public class WorkCompletedEvent extends Event {

	private static final long serialVersionUID = 2543224834236730017L;
	private long workId;
	private boolean completeOnbehalf = false;

	public WorkCompletedEvent(long workId) {
		super();
		this.workId = workId;
	}

	public WorkCompletedEvent(long workId, boolean completeOnbehalf) {
		super();
		this.workId = workId;
		this.completeOnbehalf = completeOnbehalf;
	}

	public long getWorkId() {
		return workId;
	}

	public boolean isCompleteOnbehalf() {
		return completeOnbehalf;
	}
}
