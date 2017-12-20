package com.workmarket.domains.model.account.pricing.subscription;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.ApprovableVerifiableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

import java.util.List;

@Entity(name = "subscriptionRenewalRequest")
@Table(name = "subscription_renewal_request")
@AuditChanges
public class SubscriptionRenewalRequest extends ApprovableVerifiableEntity {
	private static final long serialVersionUID = 263152329503522159L;

	private Integer numberOfPeriods = 0;
	private List<SubscriptionPaymentTierRenewal> subscriptionPaymentTiers = Lists.newArrayList();
	private SubscriptionConfiguration parentSubscription;

	@Column(name = "number_of_periods")
	public Integer getNumberOfPeriods() {
		return numberOfPeriods;
	}

	public void setNumberOfPeriods(Integer numberOfPeriods) {
		this.numberOfPeriods = numberOfPeriods;
	}

	@Transient
	public Boolean getModifyPricing() {
		return !subscriptionPaymentTiers.isEmpty();
	}

	@Fetch(FetchMode.JOIN)
	@OneToMany
	@JoinColumn(name = "subscription_renewal_request_id")
	public List<SubscriptionPaymentTierRenewal> getSubscriptionPaymentTiers() {
		return subscriptionPaymentTiers;
	}

	public void setSubscriptionPaymentTiers(List<SubscriptionPaymentTierRenewal> subscriptionPaymentTiers) {
		this.subscriptionPaymentTiers = subscriptionPaymentTiers;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional = false)
	@JoinColumn(name = "parent_subscription_id", referencedColumnName = "id", updatable = false)
	public SubscriptionConfiguration getParentSubscription() {
		return parentSubscription;
	}

	public void setParentSubscription(SubscriptionConfiguration parentSubscription) {
		this.parentSubscription = parentSubscription;
	}

}
