package com.workmarket.service.business;

import com.workmarket.domains.model.Plan;

import java.util.List;

public interface PlanService {
	public List<Plan> getAllPlans();
	public Plan find(String code);
	public Plan find(Long id);
	public Plan save(Plan plan);
	public void update(Plan plan);
	public void destroy(Long id);

	void applyPlanConfigs(Long companyId, String planCode);
}
