package com.workmarket.common.template;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;

public class SubscriptionEffectiveNotificationTemplate extends AbstractSubscriptionNotificationTemplate {

	private static final long serialVersionUID = 1L;

	public SubscriptionEffectiveNotificationTemplate(Long toId, SubscriptionConfiguration configuration) {
		super(toId, configuration);
	}
}
