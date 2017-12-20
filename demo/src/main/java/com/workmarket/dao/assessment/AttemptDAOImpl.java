package com.workmarket.dao.assessment;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.assessment.WorkScopedAttempt;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AttemptDAOImpl extends AbstractDAO<Attempt> implements AttemptDAO {
	protected Class<Attempt> getEntityClass() {
		return Attempt.class;
	}


	@Override
	public Attempt findLatestForAssessmentByUser(Long assessmentId, Long userId) {
		return (Attempt) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("assessmentUserAssociation", FetchMode.JOIN)
				.createAlias("assessmentUserAssociation", "aua")
				.setFetchMode("aua.assessment", FetchMode.JOIN)
				.createAlias("aua.assessment", "assessment")
				.setFetchMode("assessment.configuration", FetchMode.JOIN)
				.add(Restrictions.eq("aua.user.id", userId))
				.add(Restrictions.eq("assessment.id", assessmentId))
				.addOrder(Order.desc("id"))
				.setMaxResults(1)
				.uniqueResult();
	}


	@Override
	public Attempt findLatestForAssessmentByUserAndWork(Long assessmentId, Long userId, Long workId) {
		return (Attempt) getFactory().getCurrentSession().createCriteria(WorkScopedAttempt.class)
				.setFetchMode("assessmentUserAssociation", FetchMode.JOIN)
				.createAlias("assessmentUserAssociation", "aua")
				.setFetchMode("user", FetchMode.JOIN)
				.createAlias("aua.user", "user")
				.setFetchMode("work", FetchMode.JOIN)
				.add(Restrictions.eq("aua.user.id", userId))
				.add(Restrictions.eq("aua.assessment.id", assessmentId))
				.add(Restrictions.eq("work.id", workId))
				.addOrder(Order.desc("id"))
				.setMaxResults(1)
				.uniqueResult();
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<Attempt> findLatestByUserAndWork(Long userId, Long workId) {
		return getFactory().getCurrentSession().createCriteria(WorkScopedAttempt.class)
				.createAlias("assessmentUserAssociation", "aua")
				.add(Restrictions.eq("aua.user.id", userId))
				.add(Restrictions.eq("work.id", workId))
				.addOrder(Order.desc("id"))
				.list();
	}

	@Override
	public Attempt findById(long id) {
		return (Attempt)getFactory().getCurrentSession().createCriteria(Attempt.class)
				.createAlias("assessmentUserAssociation", "assessmentUserAssociation")
				.setFetchMode("assessmentUserAssociation.user", FetchMode.JOIN)
				.add(Restrictions.eq("id", id))
				.uniqueResult();
	}
}
