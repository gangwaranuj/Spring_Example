package com.workmarket.domains.work.service;

import com.google.common.base.Optional;
import com.workmarket.domains.model.WorkQuestionAnswerPair;

import java.util.List;

/**
 * Created by nick on 2013-02-25 8:31 AM
 */
public interface WorkQuestionService {

	public WorkQuestionAnswerPair saveQuestion(Long workId, Long userId, String question);

	public WorkQuestionAnswerPair saveQuestion(Long workId, Long userId, String question, Long onBehalfOfUserId);

	public WorkQuestionAnswerPair saveAnswerToQuestion(Long questionAnswerPairId, Long userId, String answer, Long workId);

	public WorkQuestionAnswerPair saveAnswerToQuestion(Long questionAnswerPairId, Long userId, Long onBehalfOfUserId, String answer, Long workId);

	public List<WorkQuestionAnswerPair> findQuestionAnswerPairs(Long workId);

	public List<WorkQuestionAnswerPair> findQuestionAnswerPairs(Long workId, Long userId, Long CompanyId);

	public Optional<WorkQuestionAnswerPair> findQuestionAnswerPairById(Long id);

	public Long findWorkIdByQuestionId(Long id);

}
