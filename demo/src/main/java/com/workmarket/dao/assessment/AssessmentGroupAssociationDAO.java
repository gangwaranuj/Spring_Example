package com.workmarket.dao.assessment;

import com.workmarket.dto.UserGroupDTO;

import java.util.List;

public interface AssessmentGroupAssociationDAO {
	List<UserGroupDTO> findGroupAssociationsByAssessmentId(Long assessmentId);

	Boolean isUserAllowedToTakeAssessment(Long assessmentId, Long userId);
}
