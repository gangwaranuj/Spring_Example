package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.ScheduledEvent;

import java.util.Calendar;

public class RoutingStrategyScheduledEvent extends ScheduledEvent {
	private static final long serialVersionUID = -407840798309332252L;

	private long routingStrategyId;

	public RoutingStrategyScheduledEvent(Calendar scheduledDate, long routingStrategyId) {
		super(scheduledDate);
		this.routingStrategyId = routingStrategyId;
	}

	public long getRoutingStrategyId() {
		return routingStrategyId;
	}
}