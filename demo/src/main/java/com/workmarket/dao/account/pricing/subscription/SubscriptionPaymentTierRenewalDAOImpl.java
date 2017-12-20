package com.workmarket.dao.account.pricing.subscription;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTierRenewal;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionPaymentTierRenewalDAOImpl extends AbstractDAO<SubscriptionPaymentTierRenewal> implements SubscriptionPaymentTierRenewalDAO {

	@Override
	protected Class<SubscriptionPaymentTierRenewal> getEntityClass() {
		return SubscriptionPaymentTierRenewal.class;
	}
}
