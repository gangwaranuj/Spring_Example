package com.workmarket.domains.work.service.route;

import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.model.route.AutoRoutingStrategy;
import com.workmarket.domains.work.model.route.GroupRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeGroupsAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeWorkAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.PolymathAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.RoutingVisitor;
import com.workmarket.domains.work.model.route.UserRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorRoutingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;

// Non-transactional routing strategy executor
@Service
public class RoutingStrategyFacadeImpl implements RoutingStrategyFacade {

	private static final Log logger = LogFactory.getLog(RoutingStrategyFacadeImpl.class);

	@Autowired private RoutingStrategyService routingStrategyService;
	@Autowired private RoutingVisitor workRoutingVisitor;

	@Override
	public void executeRoutingStrategy(long routingStrategyId) {
		AbstractRoutingStrategy strategy = routingStrategyService.findRoutingStrategy(routingStrategyId);
		Assert.notNull(strategy);
		Assert.notNull(strategy.getWork());
		logger.info(String.format("[routing] Execute routing strategy [%d] of type [%s] for work [%d]", strategy.getId(), strategy.getType(), strategy.getWork().getId()));

		strategy.execute(workRoutingVisitor);
	}

	@Override
	public void executeRoutingStrategyGroup(long routingStrategyGroupId) {
		List<UserRoutingStrategy> userStrategies = routingStrategyService.findUserRoutingStrategiesByGroup(routingStrategyGroupId);
		List<GroupRoutingStrategy> groupStrategies = routingStrategyService.findGroupRoutingStrategiesByGroup(routingStrategyGroupId);
		List<VendorRoutingStrategy> vendorStrategies = routingStrategyService.findVendorRoutingStrategiesByGroup(routingStrategyGroupId);

		List<UserRoutingStrategy> firstToAcceptUserStrategies = select(userStrategies, having(on(UserRoutingStrategy.class).isAssignToFirstToAccept()));
		List<UserRoutingStrategy> notFirstToAcceptUserStrategies = select(userStrategies, Matchers.not(having(on(UserRoutingStrategy.class).isAssignToFirstToAccept())));
		List<GroupRoutingStrategy> firstToAcceptGroupStrategies = select(groupStrategies, having(on(GroupRoutingStrategy.class).isAssignToFirstToAccept()));
		List<GroupRoutingStrategy> notFirstToAcceptGroupStrategies = select(groupStrategies, Matchers.not(having(on(GroupRoutingStrategy.class).isAssignToFirstToAccept())));
		List<VendorRoutingStrategy> firstToAcceptVendorStrategies = select(vendorStrategies, having(on(VendorRoutingStrategy.class).isAssignToFirstToAccept()));
		List<VendorRoutingStrategy> notFirstToAcceptVendorStrategies = select(vendorStrategies, Matchers.not(having(on(VendorRoutingStrategy.class).isAssignToFirstToAccept())));

		for (AbstractRoutingStrategy strategy : firstToAcceptUserStrategies) {
			strategy.execute(workRoutingVisitor);
		}

		for (AbstractRoutingStrategy strategy : firstToAcceptGroupStrategies) {
			strategy.execute(workRoutingVisitor);
		}

		for (AbstractRoutingStrategy strategy : firstToAcceptVendorStrategies) {
			strategy.execute(workRoutingVisitor);
		}

		for (AbstractRoutingStrategy strategy : notFirstToAcceptUserStrategies) {
			strategy.execute(workRoutingVisitor);
		}

		for (AbstractRoutingStrategy strategy : notFirstToAcceptGroupStrategies) {
			strategy.execute(workRoutingVisitor);
		}

		for (AbstractRoutingStrategy strategy : notFirstToAcceptVendorStrategies) {
			strategy.execute(workRoutingVisitor);
		}
	}

	/**
	 * This is the orchestrator for running multiple routing strategies. This orchestration
	 * is done by listening for a done event from one strategy type and then deciding what
	 * should be run next. This could get complicated if we are running multiple routing types
	 * by company but should work given the structure of the code we have.
	 *
	 * @param routingStrategyId The routing strategy that completed.
     */
	public void executeRoutingStrategyComplete(long routingStrategyId) {
		AbstractRoutingStrategy strategy = routingStrategyService.findRoutingStrategy(routingStrategyId);
		Assert.notNull(strategy, "Strategy with id [" + routingStrategyId + "] is null");
		Assert.notNull(strategy.getWork(), "Work of strategy with id [" + routingStrategyId + "] is null");

		if (strategy instanceof AutoRoutingStrategy) {
			logger.info("AutoRoutingStrategy complete - executing LikeGroupsAutoRoutingStrategy");
			// this will create the new routing strategy in the db and fire the routing event
			// JMS message
			LikeGroupsAutoRoutingStrategy likeGroupsAutoRoutingStrategy = routingStrategyService.addLikeGroupsAutoRoutingStrategy(strategy.getWork().getId());
		} else if (strategy instanceof LikeGroupsAutoRoutingStrategy) {
			logger.info("LikeGroupsAutoRoutingStrategy complete - executing LikeWorkRoutingAutoRoutingStrategy");
			// this will create the new routing strategy in the db and fire the routing event
			// JMS message
			LikeWorkAutoRoutingStrategy likeWorkAutoRoutingStrategy = routingStrategyService.addLikeWorkAutoRoutingStrategy(strategy.getWork().getId());
		} else if (strategy instanceof LikeWorkAutoRoutingStrategy) {
			logger.info("LikeWorkAutoRoutingStrategy complete -- executing PolymathAutoRoutingStrategy");
			// this will create the new routing strategy in the db and fire the routing event
			// JMS message
			PolymathAutoRoutingStrategy polymathAutoRoutingStrategy = routingStrategyService.addPolymathAutoRoutingStrategy(strategy.getWork().getId());
	    } else {
			logger.info("No additional routing required for routing event " + strategy.getType() + " with id " + routingStrategyId);
		}
	}

}
