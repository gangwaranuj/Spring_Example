package com.workmarket.service.business.event.work;

import java.util.Calendar;

public class WorkAutoCloseScheduledEvent extends WorkScheduledEvent {

	private static final long serialVersionUID = 5166871275151791907L;

	public WorkAutoCloseScheduledEvent(Long workId, Calendar scheduledDate) {
		super(workId, scheduledDate);
	}
}
