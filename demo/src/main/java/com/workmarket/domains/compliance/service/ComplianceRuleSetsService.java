package com.workmarket.domains.compliance.service;

import com.workmarket.domains.compliance.model.ComplianceRuleSet;

import java.util.List;

public interface ComplianceRuleSetsService {
	List<ComplianceRuleSet> findAll();

	List<ComplianceRuleSet> findAll(Long companyId);

	List<ComplianceRuleSet> findAllActive();

	// TODO: Delete this once we support more than once ComplianceRuleSet
	ComplianceRuleSet findOrInitializeDefault();

	ComplianceRuleSet find(Long id);

	void update(ComplianceRuleSet complianceRuleSet);

	void save(ComplianceRuleSet complianceRuleSet);

	void saveOrUpdate(ComplianceRuleSet complianceRuleSet);
}
