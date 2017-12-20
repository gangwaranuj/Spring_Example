package com.workmarket.service.business.assessment;

import com.workmarket.thrift.assessment.AssessmentRequestInfo;
import com.workmarket.thrift.assessment.AssessmentResponse;
import com.workmarket.thrift.assessment.ItemType;

import java.util.Set;

public interface AssessmentResponseBuilder {
	AssessmentResponse buildAssessmentResponse(com.workmarket.domains.model.assessment.AbstractAssessment assessment, com.workmarket.domains.model.User currentUser, Set<AssessmentRequestInfo> includes, Long scopedWorkId) throws Exception;
	AssessmentResponse buildAssessmentResponseForGrading(com.workmarket.domains.model.assessment.AbstractAssessment assessment, com.workmarket.domains.model.User currentUser, com.workmarket.domains.model.assessment.Attempt attempt) throws Exception;

	ItemType getItemType(String type);
}
