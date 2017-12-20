package com.workmarket.dao.requirement;

import com.workmarket.domains.model.requirementset.test.TestRequirement;

import java.util.List;

public interface TestRequirementDAO extends RequirementDAO<TestRequirement> {
	List<Long> findSentWorkIdsWithTestRequirement(Long assessmentId, Long userId);
	List<Long> findSentWorkIdsWithTestRequirementFromGroup(Long assessmentId, Long userId);
}
