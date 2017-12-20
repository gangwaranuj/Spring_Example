package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.ScheduledEvent;

import java.util.Calendar;

public class ExecuteRoutingStrategyGroupEvent extends ScheduledEvent {
	private static final long serialVersionUID = 6929234293168666893L;
	private long routingStrategyGroupId;

	public ExecuteRoutingStrategyGroupEvent(Calendar scheduledDate, long routingStrategyGroupId) {
		super(scheduledDate);
		this.routingStrategyGroupId = routingStrategyGroupId;
	}


	public long getRoutingStrategyGroupId() {
		return routingStrategyGroupId;
	}
}
