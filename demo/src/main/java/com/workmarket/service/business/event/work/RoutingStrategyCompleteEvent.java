package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.ScheduledEvent;

/**
 * Event fired when a work send routing strategy completes
 */
public class RoutingStrategyCompleteEvent extends ScheduledEvent {

    private static final long serialVersionUID = -3715103266237916101L;

    private long routingStrategyId;

    public RoutingStrategyCompleteEvent(long routingStrategyId) {
        super();
        this.routingStrategyId = routingStrategyId;
    }

    public long getRoutingStrategyId() {
        return routingStrategyId;
    }

}
