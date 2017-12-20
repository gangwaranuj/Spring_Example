package com.workmarket.domains.work.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.RoutingStrategyTracking;
import org.springframework.stereotype.Repository;

@Repository
public class RoutingStrategyTrackingDAOImpl extends AbstractDAO<RoutingStrategyTracking> implements RoutingStrategyTrackingDAO {
	@Override
	protected Class<?> getEntityClass() {
		return RoutingStrategyTracking.class;
	}
}
