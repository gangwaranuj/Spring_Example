package com.workmarket.dao.assessment;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.assessment.AssessmentUserAssociation;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination;
import java.util.List;

public interface ManagedAssessmentDAO extends DAOInterface<AssessmentUserAssociation> {
	ManagedAssessmentPagination findAssessmentsForUser(
		Long userCompanyId,
		Long userId,
		Long industryId,
		List<Long> exclusiveCompanyIds,
		List<Long> excludeCompanyIds,
		ManagedAssessmentPagination pagination);
}
