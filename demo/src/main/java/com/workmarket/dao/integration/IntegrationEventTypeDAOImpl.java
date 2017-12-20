package com.workmarket.dao.integration;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.integration.IntegrationEventType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IntegrationEventTypeDAOImpl extends AbstractDAO<IntegrationEventType> implements IntegrationEventTypeDAO {
	@Override
	protected Class<?> getEntityClass() {
		return IntegrationEventType.class;
	}

	@Override
	public List<IntegrationEventType> findIntegrationEventTypes() {
		return getFactory().getCurrentSession().createCriteria(getEntityClass()).list();
	}
}
