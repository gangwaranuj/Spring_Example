package com.workmarket.service.business.event;

public class WorkResourceCacheEvent extends Event {

	private static final long serialVersionUID = -2134423173773893453L;
	private long workId;

	public WorkResourceCacheEvent(Long workId) {
		this.workId = workId;
	}

	public long getWorkId() {
		return workId;
	}
}
