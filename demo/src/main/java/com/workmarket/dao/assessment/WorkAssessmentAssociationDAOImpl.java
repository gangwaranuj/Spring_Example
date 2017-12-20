package com.workmarket.dao.assessment;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.assessment.WorkAssessmentAssociation;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkAssessmentAssociationDAOImpl extends AbstractDAO<WorkAssessmentAssociation> implements WorkAssessmentAssociationDAO {
	@Override
	protected Class<WorkAssessmentAssociation> getEntityClass() {
		return WorkAssessmentAssociation.class;
	}
	
	@Override
	public WorkAssessmentAssociation findByWorkAndAssessment(Long workId, Long assessmentId) {
		return (WorkAssessmentAssociation)getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("work.id", workId))
			.add(Restrictions.eq("assessment.id", assessmentId))
			.uniqueResult();
	}

	@Override
	public List<WorkAssessmentAssociation> findAllByWork(long workId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("deleted", false))
				.list();
	}
}