package com.workmarket.dao.assessment;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.assessment.WorkAssessmentAssociation;

import java.util.List;

public interface WorkAssessmentAssociationDAO extends DAOInterface<WorkAssessmentAssociation> {
	WorkAssessmentAssociation findByWorkAndAssessment(Long workId, Long assessmentId);

	List<WorkAssessmentAssociation> findAllByWork(long workId);
}