package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class AbstractWorkExpenseNegotiationNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = 5062981502230183736L;
	protected WorkExpenseNegotiation negotiation;

	public AbstractWorkExpenseNegotiationNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation, NotificationType notificationType) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, notificationType, ReplyToType.TRANSACTIONAL, work);
		this.negotiation = negotiation;
	}

	public AbstractWorkExpenseNegotiationNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation, NotificationType notificationType, String noteCreatorFullName) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, notificationType, ReplyToType.TRANSACTIONAL, work, noteCreatorFullName);
		this.negotiation = negotiation;
	}

	public WorkExpenseNegotiation getNegotiation() {
		return negotiation;
	}
}
