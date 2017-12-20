package com.workmarket.domains.velvetrope.rope;

import com.google.api.client.repackaged.com.google.common.annotations.VisibleForTesting;
import com.workmarket.domains.model.assessment.WorkAssessmentAssociation;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.velvetrope.Rope;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class AssignmentsSurveyDeletionRope implements Rope {
	private AssessmentService assessmentService;
	private long workId;

	public AssignmentsSurveyDeletionRope() {}

	@VisibleForTesting
	public AssignmentsSurveyDeletionRope(final AssessmentService assessmentService, final long workId) {
		this.assessmentService = assessmentService;
		this.workId = workId;
	}

	@Override
	public void enter() {
		final List<WorkAssessmentAssociation> preExistingWorkAssessmentAssociations =
			assessmentService.findAllWorkAssessmentAssociationByWork(workId);

		if (isNotEmpty(preExistingWorkAssessmentAssociations)) {
			for (WorkAssessmentAssociation preExistingWorkAssessmentAssociation : preExistingWorkAssessmentAssociations) {
				preExistingWorkAssessmentAssociation.setDeleted(true);
			}
		}
	}
}
