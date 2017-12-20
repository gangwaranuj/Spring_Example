package com.workmarket.common.template;

import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.notification.NotificationType;

public class AssessmentCompletedNotificationTemplate extends AbstractAssessmentAttemptNotificationTemplate {
	private static final long serialVersionUID = 2407700696313386367L;

	public AssessmentCompletedNotificationTemplate(Long fromId, Long toId, Attempt attempt, AbstractAssessment assessment) {
		super(fromId, toId, new NotificationType(NotificationType.ASSESSMENT_ATTEMPT_COMPLETED), attempt);
	}
}