package com.workmarket.dao.account.pricing.subscription;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionThroughputIncrementTransaction;

import java.util.List;

/**
 * Author: rocio
 */
public interface SubscriptionThroughputIncrementTransactionDAO extends DAOInterface<SubscriptionThroughputIncrementTransaction> {

	List<SubscriptionThroughputIncrementTransaction> findAllSubscriptionThroughputIncrementTxsByStatus(String transactionStatus);
}
