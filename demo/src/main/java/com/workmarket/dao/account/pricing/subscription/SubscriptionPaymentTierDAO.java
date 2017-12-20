package com.workmarket.dao.account.pricing.subscription;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;

public interface SubscriptionPaymentTierDAO extends DAOInterface<SubscriptionPaymentTier> {

	SubscriptionPaymentTier findActiveSubscriptionPaymentTier(long subscriptionId, SubscriptionPaymentTier.PaymentTierCategory paymentTierCategory);

}
