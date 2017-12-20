package com.workmarket.common.template;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkQuestionNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = 6953358909381343492L;
	private WorkQuestionAnswerPair question;
	private String type;
	private String fullName;

	public WorkQuestionNotificationTemplate(Long toId, Work work, WorkQuestionAnswerPair question, String fullName) {
		super(question.getQuestionerId(), toId, new NotificationType(NotificationType.WORK_QUESTION), ReplyToType.TRANSACTIONAL_FROM_USER, work);
		this.question = question;
		this.type = work.isWorkBundle() ? Constants.WORK_TYPE_BUNDLE : Constants.WORK_TYPE_ASSIGNMENT;
		this.fullName = fullName;
	}
	
	public WorkQuestionAnswerPair getQuestion() {
		return question;
	}

	public String getType() {
		return type;
	}

	public String getFullName() {
		return fullName;
	}
}