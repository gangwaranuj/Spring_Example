package com.workmarket.common.template;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.util.Assert;

import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class AssessmentInvitationNotificationTemplate extends NotificationTemplate
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6101655127889810416L;
	private AbstractAssessment assessment;
	private String companyName;
	private String companyNumber;
	private String assessmentName;

	public AssessmentInvitationNotificationTemplate(Long fromId, AbstractAssessment assessment, Long toId)
	{
		super(fromId, toId, new NotificationType(NotificationType.ASSESSMENT_INVITATION), ReplyToType.TRANSACTIONAL_FROM_USER);

		Assert.notNull(assessment);
		Assert.notNull(toId);

		this.companyName = assessment.getUser().getCompany().getEffectiveName();
		this.companyNumber = assessment.getCompany().getEncryptedId();
		this.assessmentName = StringEscapeUtils.unescapeHtml4(assessment.getName());
		this.assessment = assessment;
	}

	public AbstractAssessment getAssessment()
	{
		return assessment;
	}

	public String getCompanyName()
	{
		return companyName;
	}

	public String getAssessmentName() {
		return assessmentName;
	}

	public String getCompanyNumber()
	{
		return companyNumber;
	}
}
