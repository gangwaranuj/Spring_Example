package com.workmarket.domains.work.dao.state;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.utility.HibernateUtilities;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkSubStatusTypeAssociationDAOImpl extends AbstractDAO<WorkSubStatusTypeAssociation> implements WorkSubStatusTypeAssociationDAO {

	protected Class<WorkSubStatusTypeAssociation> getEntityClass() {
		return WorkSubStatusTypeAssociation.class;
	}

	@Override
	public WorkSubStatusTypeAssociation findByWorkSubStatusAndWorkId(long subStatusId, long workId) {
		return (WorkSubStatusTypeAssociation) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("workSubStatusType.id", subStatusId))
				.setMaxResults(1).uniqueResult();
	}

	@Override
	public List<WorkSubStatusType> findAllUnResolvedSubStatuses(long workId) {
		return HibernateUtilities.listAndCast(getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("resolved", false))
				.add(Restrictions.eq("deleted", false))
				.setProjection(Projections.property("workSubStatusType")));
	}

	@Override public List<WorkSubStatusTypeAssociation> findByWorkSubStatusId(long subStatusId) {
		return HibernateUtilities.listAndCast(getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("workSubStatusType.id", subStatusId))
				.add(Restrictions.eq("resolved", false))
				.add(Restrictions.eq("deleted", false)));
	}
}
