package com.workmarket.service.business.event.calendar;

import com.workmarket.service.business.event.Event;

public class CalendarSyncAddAssignmentsEvent extends Event{
	private Long userId;

	public CalendarSyncAddAssignmentsEvent(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return this.userId;
	}
}
