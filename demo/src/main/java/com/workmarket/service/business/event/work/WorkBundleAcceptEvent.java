package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.Event;

public class WorkBundleAcceptEvent extends Event {
	private static final long serialVersionUID = -4088103192743956833L;
	private Long workId;
	private Long userId;

	public WorkBundleAcceptEvent(Long userId, Long workId) {
		this.workId = workId;
		this.userId = userId;
	}

	public WorkBundleAcceptEvent() {
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}
}
