package com.workmarket.dao.account.pricing.subscription;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionRenewalRequest;

public interface SubscriptionRenewalRequestDAO extends DAOInterface<SubscriptionRenewalRequest> {

	SubscriptionRenewalRequest findLatestPendingApprovalSubscriptionRenewalRequest(long parentSubscriptionId);
}
