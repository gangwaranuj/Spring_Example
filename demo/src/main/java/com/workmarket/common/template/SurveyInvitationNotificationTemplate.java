package com.workmarket.common.template;

import org.springframework.util.Assert;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class SurveyInvitationNotificationTemplate extends NotificationTemplate {
	private static final long serialVersionUID = 1L;
	
	private AbstractAssessment assessment;
	private String companyName;
	private String companyNumber;
	
	public SurveyInvitationNotificationTemplate(Long fromId, AbstractAssessment assessment, Long toId)
	{
		super(fromId, toId, new NotificationType(NotificationType.SURVEY_INVITATION), ReplyToType.TRANSACTIONAL_FROM_USER);

		Assert.notNull(assessment);
		Assert.notNull(toId);

		this.companyName = assessment.getUser().getCompany().getEffectiveName();
		this.companyNumber = assessment.getCompany().getEncryptedId();
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
	
	public String getCompanyNumber()
	{
		return companyNumber;
	}
}