package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.ScheduledEvent;

import java.util.Calendar;

public class WorkNegotiationExpiredScheduledEvent extends ScheduledEvent {

	private static final long serialVersionUID = 1;
	private long workNegotiationId;

	public WorkNegotiationExpiredScheduledEvent(Long workNegotiationId, Calendar scheduleDate) {
		super(scheduleDate);
		this.workNegotiationId = workNegotiationId;
	}

	public long getWorkNegotiationId() {
		return workNegotiationId;
	}
}
