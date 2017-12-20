package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.model.route.GroupRoutingStrategy;
import com.workmarket.domains.work.model.route.UserRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorRoutingStrategy;

import java.util.List;
import java.util.Map;

public interface RoutingStrategyDAO extends DAOInterface<AbstractRoutingStrategy> {

	List<Long> findAllScheduled();

	List<Long> findAllScheduledWithoutGroup();

	List<Long> findAllScheduledByGroup(long routingStrategyGroupId);

	List<Long> findAllGroupsRoutedByWork(long workId);

	Map<Long, List<Long>> findAllGroupsRoutedByWork(List<Long> workIds);

	List<UserRoutingStrategy> findUserRoutingStrategiesByGroup(long routingGroupId);

	List<GroupRoutingStrategy> findGroupRoutingStrategiesByGroup(long routingGroupId);

	List<VendorRoutingStrategy> findVendorRoutingStrategiesByGroup(long routingGroupId);

	List<AbstractRoutingStrategy> findAllRoutingStrategiesByWork(long workId);
}