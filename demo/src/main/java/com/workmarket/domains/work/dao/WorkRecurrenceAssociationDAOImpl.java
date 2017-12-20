package com.workmarket.domains.work.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.WorkRecurrenceAssociation;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class WorkRecurrenceAssociationDAOImpl extends AbstractDAO<WorkRecurrenceAssociation> implements
		WorkRecurrenceAssociationDAO {

	protected Class<WorkRecurrenceAssociation> getEntityClass() {
		return WorkRecurrenceAssociation.class;
	}

	@Override
	public void addWorkRecurrence(
			AbstractWork work,
			AbstractWork recurringWork,
			String recurrenceUUID) {
		Assert.notNull(work);
		Assert.notNull(recurrenceUUID);

		WorkRecurrenceAssociation workSkill = findWorkRecurrenceAssociation(work.getId());

		if (workSkill == null) {
			workSkill = new WorkRecurrenceAssociation(work.getId(), recurringWork.getId(), recurrenceUUID);
			saveOrUpdate(workSkill);
		} else {
			workSkill.setDeleted(false);
		}
	}

	@Override
	public WorkRecurrenceAssociation findWorkRecurrenceAssociation(Long workId) {
		Criteria criteria = getFactory()
				.getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("deleted", Boolean.FALSE));

		return (WorkRecurrenceAssociation) criteria.uniqueResult();
	}
}
