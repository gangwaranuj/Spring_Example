package com.workmarket.service.business.queue.integration;

import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.service.business.queue.WorkMarketEventService;

import java.util.List;

/**
 * Created by nick on 2012-12-29 11:07 AM
 */
public interface IntegrationEventService extends WorkMarketEventService {
	List<IntegrationEventType> findIntegrationEventTypes();
}
