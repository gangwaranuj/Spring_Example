package com.workmarket.domains.work.service.route;

public interface RoutingStrategyFacade {
	void executeRoutingStrategy(long routingStrategyId);

	void executeRoutingStrategyGroup(long routingStrategyGroupId);

	void executeRoutingStrategyComplete(long routingStrategyId);
}
