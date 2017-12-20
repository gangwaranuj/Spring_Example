package com.workmarket.common.template;

import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.notification.NotificationType;

public class AssessmentGradedNotificationTemplate extends AbstractAssessmentAttemptNotificationTemplate {
	private static final long serialVersionUID = 2407700696313386367L;

	public AssessmentGradedNotificationTemplate(Long fromId, Long toId, Attempt attempt) {
		super(fromId, toId, new NotificationType(NotificationType.ASSESSMENT_ATTEMPT_GRADED), attempt);
	}
}