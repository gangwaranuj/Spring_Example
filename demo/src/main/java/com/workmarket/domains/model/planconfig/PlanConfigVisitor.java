package com.workmarket.domains.model.planconfig;

/**
 * User: micah
 * Date: 9/2/14
 * Time: 9:30 PM
 */
public interface PlanConfigVisitor {
	void visit(TransactionFeePlanConfig planConfig, Long companyId);
}
