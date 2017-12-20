package com.workmarket.service.business.queue;

import com.workmarket.service.business.integration.event.IntegrationEvent;

public interface WorkMarketEventService {
	public void sendEvent(IntegrationEvent eventData);
}
