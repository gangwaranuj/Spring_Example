package com.workmarket.dao.assessment;

import java.util.Calendar;
import java.util.List;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.assessment.AttemptResponse;
import com.workmarket.domains.model.assessment.AttemptStatusType;

public interface AttemptResponseDAO extends DeletableDAOInterface<AttemptResponse> {
	public List<AttemptResponse> findForItemInAttempt(Long attemptId, Long itemId);
	public List<AttemptResponse> findByAttempt(Long attemptId);

	List<AttemptResponse> findResponsesByItemAndStatus(Long itemId, String status);
}