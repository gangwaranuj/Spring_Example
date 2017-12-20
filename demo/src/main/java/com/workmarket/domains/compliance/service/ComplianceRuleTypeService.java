package com.workmarket.domains.compliance.service;

import com.workmarket.domains.compliance.model.ComplianceRuleType;

import java.util.List;

public interface ComplianceRuleTypeService {
	List<ComplianceRuleType> findAll();

	ComplianceRuleType findByName(String name);
}
