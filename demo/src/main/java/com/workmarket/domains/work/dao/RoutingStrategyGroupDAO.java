package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.route.RoutingStrategyGroup;

public interface RoutingStrategyGroupDAO extends DAOInterface<RoutingStrategyGroup> {


	RoutingStrategyGroup findById(Long id);
}
