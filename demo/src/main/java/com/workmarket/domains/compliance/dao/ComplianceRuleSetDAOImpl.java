package com.workmarket.domains.compliance.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.compliance.model.ComplianceRuleSet;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ComplianceRuleSetDAOImpl extends AbstractDAO<ComplianceRuleSet> implements ComplianceRuleSetDAO {

	@Override
	protected Class<ComplianceRuleSet> getEntityClass() { return ComplianceRuleSet.class; }

	@Override
	public void merge(ComplianceRuleSet complianceRuleSet) {
		getFactory().getCurrentSession().merge(complianceRuleSet);
	}

	@Override
	public void save(ComplianceRuleSet complianceRuleSet) {
		getFactory().getCurrentSession().save(complianceRuleSet);
	}

	@Override
	public List<ComplianceRuleSet> findAllBy(Object... objects) {
		List<ComplianceRuleSet> complianceRuleSets = super.findAllBy(objects);
		// TODO micah
		// may need sort here
		return complianceRuleSets;
	}
}
