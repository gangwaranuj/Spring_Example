package com.workmarket.dao.account.pricing.subscription;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionAddOnTypeAssociation;

import java.util.List;

/**
 * Author: rocio
 */
public interface SubscriptionAddOnTypeAssociationDAO extends DAOInterface<SubscriptionAddOnTypeAssociation> {

	List<SubscriptionAddOnTypeAssociation> findBySubscriptionIdAndAddOnType(long subscriptionId, String subscriptionAddOnTypeCode);

}
