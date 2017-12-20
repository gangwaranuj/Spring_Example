package com.workmarket.service.business.event.work;

import java.util.Calendar;

public class ValidateResourceCheckInScheduledEvent extends WorkScheduledEvent {

	private static final long serialVersionUID = -886528615149671956L;

	public ValidateResourceCheckInScheduledEvent(Long workId, Calendar scheduledDate) {
		super(workId, scheduledDate);
	}
}
