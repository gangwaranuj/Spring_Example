package com.workmarket.dao.account.pricing.subscription;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionFeeConfiguration;

/**
 * Author: rocio
 */
public interface SubscriptionFeeConfigurationDAO extends DAOInterface<SubscriptionFeeConfiguration> {

	public SubscriptionFeeConfiguration getLatestPendingApprovalSubscriptionFeeConfiguration(long subscriptionConfigurationId);

}