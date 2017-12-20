package com.workmarket.domains.compliance.service;

import com.workmarket.domains.compliance.model.AssignmentCountComplianceRule;
import com.workmarket.domains.compliance.model.WorkBundleComplianceCriterion;
import com.workmarket.domains.compliance.model.WorkComplianceCriterion;

public interface CompliantVisitor {
	void visit(WorkBundleComplianceCriterion complianceCriterion, AssignmentCountComplianceRule assignmentCountComplianceRule);

	void visit(WorkComplianceCriterion complianceCriterion, AssignmentCountComplianceRule assignmentCountComplianceRule);
}
