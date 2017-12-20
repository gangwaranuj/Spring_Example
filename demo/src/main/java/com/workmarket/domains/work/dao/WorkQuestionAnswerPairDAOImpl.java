package com.workmarket.domains.work.dao;

import java.util.List;

import com.workmarket.dao.AbstractDAO;
import org.springframework.stereotype.Repository;

import com.workmarket.domains.model.WorkQuestionAnswerPair;

@Repository
public class WorkQuestionAnswerPairDAOImpl extends AbstractDAO<WorkQuestionAnswerPair> implements WorkQuestionAnswerPairDAO  {
	
	protected Class<WorkQuestionAnswerPair> getEntityClass() {
		return WorkQuestionAnswerPair.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkQuestionAnswerPair> findByWork(Long workId) {
		return getFactory().getCurrentSession().getNamedQuery("workQuestionAnswerPair.byWork")
			.setLong("work_id", workId)
			.list();
	}

	@Override
	public Long findWorkIdByQuestionId(Long questionId) {

		return (Long)getFactory().getCurrentSession().getNamedQuery("workId.byQuestionId")
			.setLong("question_id", questionId)
			.uniqueResult();
	}
}
