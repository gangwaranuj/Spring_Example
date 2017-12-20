package com.workmarket.domains.compliance.service;

import com.workmarket.domains.compliance.dao.ComplianceRuleSetDAO;
import com.workmarket.domains.compliance.model.ComplianceRuleSet;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class ComplianceRuleSetsServiceImpl implements ComplianceRuleSetsService {
	@Autowired private ComplianceRuleSetDAO dao;
	@Autowired private AuthenticationService auth;
	@Autowired private CompanyService companyService;

	@Override
	public List<ComplianceRuleSet> findAll() {
		List<ComplianceRuleSet> ruleSets =  dao.findAllBy("company", auth.getCurrentUser().getCompany());
		for (ComplianceRuleSet ruleSet : ruleSets) {
			Hibernate.initialize(ruleSet.getComplianceRules());
		}
		return ruleSets;
	}

	@Override
	public List<ComplianceRuleSet> findAll(Long companyId) {
		Assert.notNull(companyId);
		List<ComplianceRuleSet> ruleSets =  dao.findAllBy("company", companyService.findById(companyId));
		for (ComplianceRuleSet ruleSet : ruleSets) {
			Hibernate.initialize(ruleSet.getComplianceRules());
		}
		return ruleSets;
	}

	@Override
	public List<ComplianceRuleSet> findAllActive() {
		List<ComplianceRuleSet> ruleSets =   dao.findAllBy(
			"company", auth.getCurrentUser().getCompany(),
			"active", true
		);
		for (ComplianceRuleSet ruleSet : ruleSets) {
			Hibernate.initialize(ruleSet.getComplianceRules());
		}
		return ruleSets;
	}

	// TODO: Delete this once we support more than once ComplianceRuleSet
	@Override
	public ComplianceRuleSet findOrInitializeDefault() {
		ComplianceRuleSet ruleSet = dao.getOrInitializeBy(
				"name",    "default",
				"company", auth.getCurrentUser().getCompany(),
				"active",  true
		);
		Hibernate.initialize(ruleSet.getComplianceRules());

		if (ruleSet.getId() == null) {
			dao.save(ruleSet);
		}
		return ruleSet;
	}

	@Override
	public ComplianceRuleSet find(Long id) {
		return dao.findBy(
			"id",      id,
			"company", auth.getCurrentUser().getCompany()
		);
	}

	@Override
	public void update(ComplianceRuleSet complianceRuleSet) {
		dao.merge(complianceRuleSet);
	}

	@Override
	public void save(ComplianceRuleSet complianceRuleSet) {
		complianceRuleSet.setCompany(auth.getCurrentUser().getCompany());
		dao.save(complianceRuleSet);
	}

	@Override
	public void saveOrUpdate(ComplianceRuleSet complianceRuleSet) {
		complianceRuleSet.setCompany(auth.getCurrentUser().getCompany());
		dao.saveOrUpdate(complianceRuleSet);
	}
}
