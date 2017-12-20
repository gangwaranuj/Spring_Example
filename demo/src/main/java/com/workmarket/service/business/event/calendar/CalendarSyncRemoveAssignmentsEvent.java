package com.workmarket.service.business.event.calendar;

import com.workmarket.domains.model.google.RefreshToken;
import com.workmarket.service.business.event.Event;

public class CalendarSyncRemoveAssignmentsEvent extends Event {
	private Long userId;
	private String refreshToken;

	public CalendarSyncRemoveAssignmentsEvent(Long userId, String refreshToken) {
		this.refreshToken = refreshToken;
		this.userId = userId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
}
