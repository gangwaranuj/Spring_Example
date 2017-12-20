package com.workmarket.service.business;

import com.workmarket.dao.PlanDAO;
import com.workmarket.domains.model.Plan;
import com.workmarket.domains.model.planconfig.AbstractPlanConfig;
import com.workmarket.domains.model.planconfig.PlanConfigVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
	@Autowired private PlanDAO dao;
	@Autowired private PlanConfigVisitor visitor;

	@Override
	public List<Plan> getAllPlans() {
		return dao.getAll();
	}

	@Override
	public Plan find(String code) {
		return dao.findBy("code", code);
	}

	@Override
	public Plan find(Long id) {
		return dao.findBy("id", id);
	}

	@Override
	public Plan save(Plan plan) {
		dao.saveOrUpdate(plan);
		return plan;
	}

	@Override
	public void update(Plan plan) {
		dao.merge(plan);
	}

	@Override
	public void destroy(Long id) {
		dao.delete(dao.findBy("id", id));
	}

	@Override
	public void applyPlanConfigs(Long companyId, String planCode) {
		if (planCode == null) { return; }
		Plan plan = find(planCode);
		if (plan == null) { return; }
		for (AbstractPlanConfig planConfig : plan.getPlanConfigs()) {
			planConfig.accept(visitor, companyId);
		}
	}
}
