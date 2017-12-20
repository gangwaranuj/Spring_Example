package com.workmarket.domains.compliance.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.compliance.model.ComplianceRuleSet;

public interface ComplianceRuleSetDAO extends DAOInterface<ComplianceRuleSet> {
	void merge(ComplianceRuleSet complianceRuleSet);
	void save(ComplianceRuleSet complianceRuleSet);
}
