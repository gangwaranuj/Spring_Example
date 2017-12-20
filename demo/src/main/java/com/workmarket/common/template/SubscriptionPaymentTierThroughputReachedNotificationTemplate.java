package com.workmarket.common.template;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class SubscriptionPaymentTierThroughputReachedNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 1L;
	private SubscriptionConfiguration subscriptionConfiguration;
	private SubscriptionPaymentTier activeSubscriptionPaymentTier;
	private BigDecimal throughput;

	public SubscriptionPaymentTierThroughputReachedNotificationTemplate(Long toId, SubscriptionConfiguration configuration, SubscriptionPaymentTier subscriptionPaymentTier, BigDecimal brokenThroughput) {
		super(configuration.getId(), toId, new NotificationType(NotificationType.SUBSCRIPTION_THROUGHPUT_REACHED), ReplyToType.TRANSACTIONAL);
		this.subscriptionConfiguration = configuration;
		this.activeSubscriptionPaymentTier = subscriptionPaymentTier;
		this.throughput = brokenThroughput;
	}

	public SubscriptionConfiguration getSubscriptionConfiguration() {
		return subscriptionConfiguration;
	}

	public SubscriptionPaymentTier getActiveSubscriptionPaymentTier() {
		return activeSubscriptionPaymentTier;
	}

	public String getThroughput() {
		if (throughput != null) {
			return StringUtilities.formatMoneyForAccounting(throughput);
		}
		return StringUtils.EMPTY;
	}
}