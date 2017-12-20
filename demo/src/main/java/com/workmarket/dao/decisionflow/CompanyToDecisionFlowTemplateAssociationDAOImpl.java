package com.workmarket.dao.decisionflow;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.decisionflow.CompanyToDecisionFlowTemplateAssociation;
import com.workmarket.utility.sql.SQLBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

@Repository
public class CompanyToDecisionFlowTemplateAssociationDAOImpl
	extends AbstractDAO<CompanyToDecisionFlowTemplateAssociation> implements CompanyToDecisionFlowTemplateAssociationDAO {
	@Autowired @Qualifier("readOnlyJdbcTemplate") private NamedParameterJdbcTemplate jdbcTemplate;

	@Override protected Class<?> getEntityClass() {
		return CompanyToDecisionFlowTemplateAssociation.class;
	}

	@Override
	public void updateDecisionFlowTemplateAssociation(Company company, String oldUuid, String uuid) {
		Assert.notNull(company);
		Assert.notNull(oldUuid);
		Assert.notNull(uuid);

		CompanyToDecisionFlowTemplateAssociation association = findDecisionFlowTemplateAssociation(company.getId(), oldUuid);

		if (association != null) {
			association.setDecisionFlowTemplateUuid(uuid);
			association.setDeleted(false);
		}
	}

	@Override
	public void addDecisionFlowTemplateAssociation(Company company, String uuid) {
		Assert.notNull(company);
		Assert.notNull(uuid);

		List<CompanyToDecisionFlowTemplateAssociation> associations = findDecisionFlowTemplateAssociations(company.getId());

		for (CompanyToDecisionFlowTemplateAssociation association : associations) {
			if (association.getDecisionFlowTemplateUuid().equals(uuid)) {
				association.setDeleted(false);
				return;
			}
		}

		CompanyToDecisionFlowTemplateAssociation association =
			new CompanyToDecisionFlowTemplateAssociation(company, uuid);
		saveOrUpdate(association);
	}

	@Override
	public List<CompanyToDecisionFlowTemplateAssociation> findDecisionFlowTemplateAssociations(Long companyId) {
		Criteria criteria = getFactory()
			.getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.eq("deleted", Boolean.FALSE));

		return (List<CompanyToDecisionFlowTemplateAssociation>) criteria.list();
	}

	@Override
	public List<String> findDecisionFlowTemplateUuids(Long companyId) {
		Criteria criteria = getFactory()
			.getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.eq("deleted", Boolean.FALSE))
			.setProjection(Projections.projectionList()
				.add(Projections.property("decisionFlowTemplateUuid")));

		return (List<String>) criteria.list();
	}

	@Override
	public CompanyToDecisionFlowTemplateAssociation findDecisionFlowTemplateAssociation(Long companyId, String uuid) {
		Criteria criteria = getFactory()
			.getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.eq("decisionFlowTemplateUuid", uuid))
			.add(Restrictions.eq("deleted", Boolean.FALSE));

		return (CompanyToDecisionFlowTemplateAssociation) criteria.uniqueResult();

	}
}
