package com.workmarket.dao;

import com.workmarket.domains.model.Plan;
import org.springframework.stereotype.Repository;

@Repository
public class PlanDAOImpl extends AbstractDAO<Plan> implements PlanDAO {
	@Override
	protected Class<?> getEntityClass() {
		return Plan.class;
	}

	@Override
	public void merge(Plan plan) {
		getFactory().getCurrentSession().merge(plan);
	}
}
