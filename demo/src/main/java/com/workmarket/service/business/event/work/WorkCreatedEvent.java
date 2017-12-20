package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.Event;

public class WorkCreatedEvent extends Event {
	public WorkCreatedEvent(Long workId) {
		this.workId = workId;
	}

	private static final long serialVersionUID = 3126652919919698779L;

	private Long workId;

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}
}
