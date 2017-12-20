package com.workmarket.dao.assessment;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.assessment.Attempt;

public interface AttemptDAO extends DAOInterface<Attempt> {
	Attempt findLatestForAssessmentByUser(Long assessmentId, Long userId);

	Attempt findLatestForAssessmentByUserAndWork(Long assessmentId, Long userId, Long workId);

	List<Attempt> findLatestByUserAndWork(Long userId, Long workId);

	Attempt findById(long id);
}