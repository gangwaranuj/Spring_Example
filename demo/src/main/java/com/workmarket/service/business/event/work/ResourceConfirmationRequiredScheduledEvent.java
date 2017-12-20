package com.workmarket.service.business.event.work;

import java.util.Calendar;

public class ResourceConfirmationRequiredScheduledEvent extends WorkScheduledEvent {

	private static final long serialVersionUID = -104174127809871308L;

	public ResourceConfirmationRequiredScheduledEvent(Long workId, Calendar scheduledDate) {
		super(workId, scheduledDate);
	}
}