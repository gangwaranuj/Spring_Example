package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class AbstractWorkBonusNegotiationNotificationTemplate extends AbstractWorkNotificationTemplate  {

	private static final long serialVersionUID = 5062981502230183436L;
	protected WorkBonusNegotiation negotiation;

	public AbstractWorkBonusNegotiationNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation, NotificationType notificationType, String noteCreatorFullName) {
		this(toId, work, negotiation, notificationType);
		this.noteCreatorFullName = noteCreatorFullName;
	}

	public AbstractWorkBonusNegotiationNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation, NotificationType notificationType) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, notificationType, ReplyToType.TRANSACTIONAL, work);
		this.negotiation = negotiation;
	}

	public WorkBonusNegotiation getNegotiation() {
		return negotiation;
	}
}
