package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.ScheduledEvent;

import java.util.Calendar;

public abstract class WorkScheduledEvent extends ScheduledEvent {

	private static final long serialVersionUID = 1262882348277569429L;
	private Long workId;
	
	protected WorkScheduledEvent(Long workId, Calendar scheduledDate) {
		super(scheduledDate);
		this.workId = workId;
	}

	public Long getWorkId() {
		return workId;
	}
}
