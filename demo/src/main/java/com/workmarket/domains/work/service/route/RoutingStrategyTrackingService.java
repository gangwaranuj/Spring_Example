package com.workmarket.domains.work.service.route;

import com.workmarket.domains.model.RoutingStrategyTracking;

import java.util.List;

public interface RoutingStrategyTrackingService {
	void saveRoutingStrategyTracking(RoutingStrategyTracking routingStrategyTracking);
	void saveAll(List<RoutingStrategyTracking> routingStrategyTrackings);
}
