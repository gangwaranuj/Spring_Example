package com.workmarket.service.business.event;

import java.util.Calendar;

public class ScheduledEvent extends Event {

	private static final long serialVersionUID = 740498283040403274L;

	public Calendar scheduledDate;

	public ScheduledEvent() {}

	public ScheduledEvent(Calendar scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public Calendar getScheduledDate() {
		return scheduledDate;
	}

	public ScheduledEvent setScheduledDate(Calendar scheduledDate) {
		this.scheduledDate = scheduledDate;
		return this;
	}


}
