package com.workmarket.dao.decisionflow;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.decisionflow.WorkToDecisionFlowAssociation;
import com.workmarket.domains.work.model.AbstractWork;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class WorkToDecisionFlowAssociationDAOImpl
	extends AbstractDAO<WorkToDecisionFlowAssociation> implements WorkToDecisionFlowAssociationDAO {

	@Override protected Class<?> getEntityClass() {
		return WorkToDecisionFlowAssociation.class;
	}

	@Override
	public void addDecisionFlowAssociation(AbstractWork work, String uuid) {
		Assert.notNull(work);
		Assert.notNull(uuid);

		WorkToDecisionFlowAssociation association = findDecisionFlowAssociation(work.getId());

		if (association == null) {
			association = new WorkToDecisionFlowAssociation(work, uuid);
			saveOrUpdate(association);
		} else {
			association.setDecisionFlowUuid(uuid);
			association.setDeleted(false);
		}
	}

	@Override
	public WorkToDecisionFlowAssociation findDecisionFlowAssociation(Long workId) {
		Criteria criteria = getFactory()
			.getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("work.id", workId))
			.add(Restrictions.eq("deleted", Boolean.FALSE));

		return (WorkToDecisionFlowAssociation) criteria.uniqueResult();
	}

	@Override
	public String findDecisionFlowUuid(Long workId) {
		Criteria criteria = getFactory()
			.getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("work.id", workId))
			.add(Restrictions.eq("deleted", Boolean.FALSE))
			.setProjection(Projections.projectionList()
				.add(Projections.property("decisionFlowUuid")));

		return (String) criteria.uniqueResult();
	}
}
