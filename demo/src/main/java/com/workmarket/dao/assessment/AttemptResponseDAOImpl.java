package com.workmarket.dao.assessment;

import java.util.Calendar;
import java.util.List;

import com.workmarket.domains.model.assessment.AttemptStatusType;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.assessment.AssessmentUserAssociation;
import com.workmarket.domains.model.assessment.AttemptResponse;

@Repository
public class AttemptResponseDAOImpl extends DeletableAbstractDAO<AttemptResponse> implements AttemptResponseDAO {
	protected Class<AttemptResponse> getEntityClass() {
		return AttemptResponse.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AttemptResponse> findForItemInAttempt(Long attemptId, Long itemId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("attempt.id", attemptId))
			.add(Restrictions.eq("item.id", itemId))
			.add(Restrictions.eq("deleted", false))
			.list();
	}

	@Override
	public List<AttemptResponse> findByAttempt(Long attemptId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("attempt.id", attemptId))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.asc("id")).list();
	}

	@Override
	public List<AttemptResponse> findResponsesByItemAndStatus(Long itemId, String status) {
		return getFactory().getCurrentSession().getNamedQuery("assessmentAttemptResponse.findInProgressResponsesByItem").setParameter("itemId", itemId).setParameter("status", status).list();
	}
}
