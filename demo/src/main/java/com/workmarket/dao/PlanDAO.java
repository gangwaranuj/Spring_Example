package com.workmarket.dao;

import com.workmarket.domains.model.Plan;

public interface PlanDAO extends DAOInterface<Plan> {
	void merge(Plan plan);
}
