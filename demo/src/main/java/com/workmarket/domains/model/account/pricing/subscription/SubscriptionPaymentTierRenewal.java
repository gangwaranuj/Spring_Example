package com.workmarket.domains.model.account.pricing.subscription;

import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "subscriptionPaymentTierRenewal")
@Table(name = "subscription_payment_tier_renewal")
@AuditChanges
public class SubscriptionPaymentTierRenewal extends AbstractPaymentTier implements Comparable<SubscriptionPaymentTierRenewal> {

	private static final long serialVersionUID = -5516150199009339688L;

	private SubscriptionRenewalRequest subscriptionRenewalRequest;

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional = false)
	@JoinColumn(name = "subscription_renewal_request_id", referencedColumnName = "id", updatable = false)
	public SubscriptionRenewalRequest getSubscriptionRenewalRequest() {
		return subscriptionRenewalRequest;
	}

	public void setSubscriptionRenewalRequest(SubscriptionRenewalRequest subscriptionRenewalRequest) {
		this.subscriptionRenewalRequest = subscriptionRenewalRequest;
	}

	@Override
	public int compareTo(SubscriptionPaymentTierRenewal subscriptionPaymentTier) {
		if (this.getMinimum() == null)
			return (subscriptionPaymentTier != null && subscriptionPaymentTier.getMinimum() != null) ? -1 : 0;
		if (subscriptionPaymentTier == null || subscriptionPaymentTier.getMinimum() == null)
			return 1;
		return this.getMinimum()
				.compareTo(subscriptionPaymentTier.getMinimum());
	}
}
