package com.workmarket.dao.assessment;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentPagination;
import com.workmarket.domains.model.assessment.AssessmentStatistics;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AbstractAssessmentDAO extends PaginatableDAOInterface<AbstractAssessment> {

	Map<Long, String> findSurveysByCompany(Long companyId);

	AssessmentPagination findByCompany(Long companyId, AssessmentPagination pagination);

	AbstractAssessment findAssessmentById(Long assessmentId);

	Map<Long, String> findAllAssessmentNamesToHydrateSearchData(Set<Long> assessmentIdsInResponse);

	Map<Long, String> findAllAssessmentNamesToHydrateSearchData();

	Integer countAssessmentsByCompany(long companyId);

	Integer countAssessmentsByCompanyCreatedSince(long companyId, Calendar dateFrom);

	List<String> getActiveAssessmentForGroup(Long companyId, Long assessmentId);

	List<String> getActiveAssessmentForAssignment(Long companyId, Long assessmentId);

	List<String> getActiveAssessmentForReqSet(Long companyId, Long assessmentId);

	AssessmentStatistics getAssessmentStatistics(Long assessmentId);

	List<Long> findAssessmentIdsByUser(Long userId);

	int updateAssessmentOwner(Long newOwnerId, List<Long> testIds);
}