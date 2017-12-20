package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkCompletedByBuyerNotificationTemplate extends AbstractWorkNotificationTemplate {
	private static final long serialVersionUID = -2725024627495132894L;
	private final Boolean fastFundsEnabled;

	public WorkCompletedByBuyerNotificationTemplate(Long fromId, Long toId, Work work, Boolean fastFundsEnabled) {
		super(fromId, toId, new NotificationType(NotificationType.WORK_COMPLETED_BY_BUYER), ReplyToType.TRANSACTIONAL_FROM_USER, work);
		this.fastFundsEnabled = fastFundsEnabled;
	}

	public Boolean isFastFundsEnabled() {
		return fastFundsEnabled;
	}
}