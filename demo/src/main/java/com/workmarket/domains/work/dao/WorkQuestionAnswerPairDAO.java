package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.WorkQuestionAnswerPair;

import java.util.List;

public interface WorkQuestionAnswerPairDAO extends DAOInterface<WorkQuestionAnswerPair> {

	public List<WorkQuestionAnswerPair> findByWork(Long workId);

	public Long findWorkIdByQuestionId(Long questionId);
}