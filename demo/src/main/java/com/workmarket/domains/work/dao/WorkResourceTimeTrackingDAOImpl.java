package com.workmarket.domains.work.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;

@Repository
public class WorkResourceTimeTrackingDAOImpl extends AbstractDAO<WorkResourceTimeTracking> implements WorkResourceTimeTrackingDAO {
	
	protected Class<WorkResourceTimeTracking> getEntityClass() {
		return WorkResourceTimeTracking.class;
	}

	@Override
	public WorkResourceTimeTracking findById(long id) {
		return (WorkResourceTimeTracking) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("id", id))
				.add(Restrictions.eq("deleted", false))
				.uniqueResult();
	}

	@Override
	public WorkResourceTimeTracking findLatestByWorkResource(long workResourceId) {
		return (WorkResourceTimeTracking) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("workResource.id", workResourceId))
				.addOrder(Order.desc("id"))
				.add(Restrictions.eq("deleted", false))
				.setMaxResults(1)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WorkResourceTimeTracking> findAllByWorkResourceId(long workResourceId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("workResource.work", FetchMode.JOIN)
				.add(Restrictions.eq("workResource.id", workResourceId))
				.add(Restrictions.eq("deleted", false))
				.list();
	}
	
}
