package com.workmarket.domains.work.dao;

import com.workmarket.domains.work.model.route.RoutingStrategyGroup;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;

@Repository
public class RoutingStrategyGroupDAOImpl extends AbstractDAO<RoutingStrategyGroup> implements RoutingStrategyGroupDAO {

	@Override
	protected Class<?> getEntityClass() {
		return RoutingStrategyGroup.class;
	}

	@Override
	public RoutingStrategyGroup findById(Long id) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("id", id));
		return (RoutingStrategyGroup) criteria.uniqueResult();
	}
}
