package com.workmarket.common.template;

import java.util.List;

import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class MultipleAssessmentInvitationsNotificationTemplate extends NotificationTemplate {
	private static final long serialVersionUID = 1L;
	private List<AbstractAssessment> assessments = Lists.newArrayList();
	private String companyName;

	public MultipleAssessmentInvitationsNotificationTemplate(Long fromId, List<AbstractAssessment> assessments, Long toId) {
		super(fromId, toId, new NotificationType(NotificationType.ASSESSMENT_INVITATION), ReplyToType.TRANSACTIONAL_FROM_USER);

		Assert.notNull(assessments);
		Assert.notNull(toId);
		
		this.assessments = assessments;
		if (!assessments.isEmpty()) {
			this.companyName = assessments.get(0).getCompany().getEffectiveName();
		}
	}

	public List<AbstractAssessment> getAssessments() {
		return assessments;
	}

	public String getCompanyName() {
		return companyName;
	}
}
