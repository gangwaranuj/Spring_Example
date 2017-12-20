package com.workmarket.dao.account.pricing.subscription;


import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTierStatusType;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class SubscriptionPaymentTierDAOImpl extends AbstractDAO<SubscriptionPaymentTier> implements SubscriptionPaymentTierDAO {

	@Override
	protected Class<SubscriptionPaymentTier> getEntityClass() {
		return SubscriptionPaymentTier.class;
	}

	@Override
	public SubscriptionPaymentTier findActiveSubscriptionPaymentTier(long subscriptionId, SubscriptionPaymentTier.PaymentTierCategory paymentTierCategory) {
		Assert.notNull(paymentTierCategory);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("subscriptionFeeConfiguration", FetchMode.JOIN)
				.createAlias("subscriptionFeeConfiguration", "feeConfiguration")
				.setFetchMode("feeConfiguration.subscriptionConfiguration", FetchMode.JOIN)
				.createAlias("feeConfiguration.subscriptionConfiguration", "subscription")
				.add(Restrictions.eq("subscription.id", subscriptionId))
				.add(Restrictions.eq("feeConfiguration.active", true))
				.setMaxResults(1);

		switch (paymentTierCategory) {
			case SOFTWARE:
				criteria.add(Restrictions.eq("subscriptionPaymentTierSoftwareStatusType.code", SubscriptionPaymentTierStatusType.ACTIVE));
				break;
			case VENDOR_OF_RECORD:
				criteria.add(Restrictions.eq("subscriptionPaymentTierVorStatusType.code", SubscriptionPaymentTierStatusType.ACTIVE));
				break;
		}
		return (SubscriptionPaymentTier)criteria.uniqueResult();
	}

}
