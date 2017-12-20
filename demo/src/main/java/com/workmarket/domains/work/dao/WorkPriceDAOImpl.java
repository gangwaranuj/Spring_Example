package com.workmarket.domains.work.dao;

import com.google.common.base.Optional;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.WorkPrice;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkPriceDAOImpl extends AbstractDAO<WorkPrice> implements WorkPriceDAO {
	
	protected Class<WorkPrice> getEntityClass() {
		return WorkPrice.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkPrice> findPriceHistoryForWork(Long workId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(WorkPrice.class)
				.add(Restrictions.eq("work.id", workId))
				.addOrder(Order.desc("id"));
		return (List<WorkPrice>) criteria.list();
	}

	@Override public Optional<WorkPrice> findOriginalPriceHistoryForWork(Long workId) {
		return Optional.fromNullable((WorkPrice) getFactory().getCurrentSession().createCriteria(WorkPrice.class)
				.add(Restrictions.eq("work.id", workId))
				.addOrder(Order.asc("id"))
				.setMaxResults(1)
				.uniqueResult());
	}


}