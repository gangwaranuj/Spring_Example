package com.workmarket.dao.assessment;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.assessment.AssessmentUserAssociation;
import com.workmarket.domains.model.assessment.AssessmentUserAssociationPagination;
import com.workmarket.dto.AssessmentUserPagination;
import com.workmarket.dto.AggregatesDTO;

import java.util.List;
import java.util.Set;

public interface AssessmentUserAssociationDAO extends DAOInterface<AssessmentUserAssociation> {
	AssessmentUserAssociation findByUserAndAssessment(Long userId, Long assessmentId);
	AssessmentUserAssociation findByUserAssessmentAndWork(Long userId, Long assessmentId, Long workId);
	AssessmentUserAssociationPagination findByUser(Long userId, AssessmentUserAssociationPagination pagination) ;
	AssessmentUserAssociationPagination findByUsers(Set<Long> userIds, AssessmentUserAssociationPagination pagination);

	AssessmentUserPagination findAllAssessmentUsers(Long companyId, Long assessmentId, AssessmentUserPagination pagination);

	AggregatesDTO countAssessmentUsers(Long companyId, Long assessmentId, AssessmentUserPagination pagination);

	AssessmentUserPagination findLatestAssessmentUserAttempts(Long assessmentId, AssessmentUserPagination pagination);

	List<Long> findSurveysCompletedForWork(Long workId, Long userId);
	List<Long> findSurveysCompletedForWorkOnBehalf(Long workId, Long behalfOfId);
}
