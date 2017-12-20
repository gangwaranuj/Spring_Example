package com.workmarket.service.business.assessment;

import com.google.common.base.Optional;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination;

import java.util.List;

public interface RecommendedAssessmentCache {

	Optional<List<Long>> get(long userId);

	void set(long userId, ManagedAssessmentPagination pagination);

}
