package com.workmarket.dao.integration;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.integration.IntegrationEventType;

import java.util.List;

public interface IntegrationEventTypeDAO extends DAOInterface<IntegrationEventType> {
	List<IntegrationEventType> findIntegrationEventTypes();
}
