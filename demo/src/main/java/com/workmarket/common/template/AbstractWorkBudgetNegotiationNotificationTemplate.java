package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

/**
 * Created by nick on 2012-11-06 9:23 AM
 */
public class AbstractWorkBudgetNegotiationNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = 5062981502230183736L;

	protected WorkBudgetNegotiation negotiation;

	public AbstractWorkBudgetNegotiationNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation, NotificationType notificationType, String noteCreatorFullName) {
		this(toId, work, negotiation, notificationType);
		this.noteCreatorFullName = noteCreatorFullName;
	}

	public AbstractWorkBudgetNegotiationNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation, NotificationType notificationType) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, notificationType, ReplyToType.TRANSACTIONAL, work);
		this.negotiation = negotiation;
	}

	public WorkBudgetNegotiation getNegotiation() {
		return negotiation;
	}
}
