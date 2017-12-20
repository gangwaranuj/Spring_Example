package com.workmarket.domains.compliance.dao;

import com.workmarket.domains.compliance.model.ComplianceRuleType;

import java.util.List;

public interface ComplianceRuleTypeDAO {
	ComplianceRuleType findByName(String name);

	List<ComplianceRuleType> findAll();
}
