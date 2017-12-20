package com.workmarket.common.template;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public abstract class AbstractSubscriptionNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 1L;
	private SubscriptionConfiguration subscriptionConfiguration;

	protected AbstractSubscriptionNotificationTemplate(Long toId, SubscriptionConfiguration subscriptionConfiguration) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.SUBSCRIPTION_REMINDER), ReplyToType.TRANSACTIONAL);
		this.subscriptionConfiguration = subscriptionConfiguration;
	}

	public SubscriptionConfiguration getSubscriptionConfiguration() {
		return subscriptionConfiguration;
	}
}
