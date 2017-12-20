package com.workmarket.common.template;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;

public class SubscriptionCancelledNotificationTemplate extends AbstractSubscriptionNotificationTemplate {

	private static final long serialVersionUID = 1L;

	public SubscriptionCancelledNotificationTemplate(Long toId, SubscriptionConfiguration configuration) {
		super(toId, configuration);
	}
}
