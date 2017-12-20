package com.workmarket.domains.groups.dao;

import com.workmarket.domains.groups.model.WorkGroupAssociation;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;

import java.util.HashSet;
import java.util.Set;

@Repository
public class WorkGroupAssociationDAOImpl extends AbstractDAO<WorkGroupAssociation> implements WorkGroupAssociationDAO {
	@Override
	protected Class<WorkGroupAssociation> getEntityClass() {
		return WorkGroupAssociation.class;
	}

	@Override
	public WorkGroupAssociation findByWorkAndGroupDeleted(Long workId, Long groupId) {
		return (WorkGroupAssociation)getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("group.id", groupId))
				.uniqueResult();
	}


	@Override
	public Set<WorkGroupAssociation> findAllByWork(Long workId) {
		Criteria cr = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("work.id", workId));

		return new HashSet<>(cr.list());
	}

}
