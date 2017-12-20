package com.workmarket.domains.work.service.route;

import com.workmarket.domains.model.RoutingStrategyTracking;
import com.workmarket.domains.work.dao.RoutingStrategyTrackingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutingStrategyTrackingServiceImpl implements RoutingStrategyTrackingService {

	@Autowired
	private RoutingStrategyTrackingDAO routingStrategyTrackingDAO;

	@Override
	public void saveRoutingStrategyTracking(RoutingStrategyTracking routingStrategyTracking) {
		routingStrategyTrackingDAO.saveOrUpdate(routingStrategyTracking);
	}

	@Override
	public void saveAll(List<RoutingStrategyTracking> routingStrategyTrackings) {
		routingStrategyTrackingDAO.saveAll(routingStrategyTrackings);
	}
}
