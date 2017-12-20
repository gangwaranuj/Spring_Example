package com.workmarket.domains.work.service.actions;

import com.workmarket.domains.model.audit.ViewType;
import com.workmarket.service.business.event.Event;

public class WorkViewedEvent extends Event {

	private static final long serialVersionUID = 2593460317911862483L;
	private long workId;
	private long userId;
	private ViewType viewType;

	public WorkViewedEvent(long workId, long userId, ViewType viewType) {
		this.workId = workId;
		this.userId = userId;
		this.viewType = viewType;
	}

	public long getWorkId() {
		return workId;
	}

	public long getUserId() {
		return userId;
	}

	public ViewType getViewType() {
		return viewType;
	}
}
