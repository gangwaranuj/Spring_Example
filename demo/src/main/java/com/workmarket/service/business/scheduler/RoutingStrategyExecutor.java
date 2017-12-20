package com.workmarket.service.business.scheduler;

import com.workmarket.domains.work.service.route.RoutingStrategyFacade;
import com.workmarket.domains.work.service.route.RoutingStrategyService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ManagedResource(objectName = "bean:name=routingStrategyExecutor", description = "Execute routing strategies")
public class RoutingStrategyExecutor implements ScheduledExecutor {
	private static final Log logger = LogFactory.getLog(RoutingStrategyExecutor.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private RoutingStrategyService workRoutingService;
	@Autowired private RoutingStrategyFacade routingStrategyFacade;

	@ManagedOperation(description = "Execute routing strategies by Group ID")
	public void execute(long routingGroupId) {
		logger.info("[routing] Executing scheduled routing strategy group " + routingGroupId);
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		routingStrategyFacade.executeRoutingStrategyGroup(routingGroupId);
	}

	@Override
	@ManagedOperation(description = "Execute routing strategies")
	public void execute() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		List<Long> routing = workRoutingService.findScheduledRoutingStrategiesWithoutGroup();
		for (Long routingId: routing) {
			logger.info("[routing] Executing scheduled routing strategy " + routingId);
			try {
				routingStrategyFacade.executeRoutingStrategy(routingId);
			} catch (Exception e) {
				logger.error("[routing] Error executing routing strategy " + routingId, e);
			}
		}
	}
}
