package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.ScheduledEvent;

import java.util.Calendar;

public class WorkResourceLateLabelScheduledEvent extends ScheduledEvent {

	private static final long serialVersionUID = 1;

	private Long workResourceId;

	public WorkResourceLateLabelScheduledEvent() {}

	public WorkResourceLateLabelScheduledEvent(Calendar scheduledDate, Long workResourceId) {
		super(scheduledDate);
		this.workResourceId = workResourceId;
	}

	public Long getWorkResourceId() {
		return workResourceId;
	}

}
