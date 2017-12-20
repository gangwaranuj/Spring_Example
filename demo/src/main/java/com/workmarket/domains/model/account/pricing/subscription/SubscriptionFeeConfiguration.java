package com.workmarket.domains.model.account.pricing.subscription;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.ActiveEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@AuditChanges
@Entity(name = "subscriptionFeeConfiguration")
@Table(name = "subscription_fee_configuration")
public class SubscriptionFeeConfiguration extends ActiveEntity implements Comparable<SubscriptionFeeConfiguration> {

	private static final long serialVersionUID = 1L;

	private SubscriptionConfiguration subscriptionConfiguration;
	private List<SubscriptionPaymentTier> subscriptionPaymentTiers = Lists.newArrayList();
	private Calendar effectiveDate;
	private Calendar removedOn;
	private SubscriptionType subscriptionType;
	private BigDecimal blockTierPercentage;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_configuration_id", updatable = false)
	public SubscriptionConfiguration getSubscriptionConfiguration() {
		return subscriptionConfiguration;
	}

	public void setSubscriptionConfiguration(SubscriptionConfiguration subscriptionConfiguration) {
		this.subscriptionConfiguration = subscriptionConfiguration;
	}

	@Column(name = "effective_date")
	public Calendar getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Calendar effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Fetch(FetchMode.JOIN)
	@OneToMany
	@JoinColumn(name = "subscription_fee_configuration_id")
	public List<SubscriptionPaymentTier> getSubscriptionPaymentTiers() {
		if (subscriptionPaymentTiers != null) {
			Collections.sort(subscriptionPaymentTiers);
		}
		return subscriptionPaymentTiers;
	}

	public void setSubscriptionPaymentTiers(List<SubscriptionPaymentTier> subscriptionPaymentTiers) {
		this.subscriptionPaymentTiers = subscriptionPaymentTiers;
	}

	@Column(name = "removed_on")
	public Calendar getRemovedOn() {
		return removedOn;
	}

	public void setRemovedOn(Calendar removedOn) {
		this.removedOn = removedOn;
	}

	@ManyToOne(fetch= FetchType.LAZY, optional = false)
	@JoinColumn(name="subscription_type", referencedColumnName="code")
	public SubscriptionType getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(SubscriptionType subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	@Column(name = "block_tier_percentage")
	public BigDecimal getBlockTierPercentage() {
		return blockTierPercentage;
	}

	public void setBlockTierPercentage(BigDecimal blockTierPercentage) {
		this.blockTierPercentage = blockTierPercentage;
	}

	@Transient
	public SubscriptionPaymentTier findSubscriptionPaymentTierForThroughputAmount(BigDecimal throughput) {
		if (throughput != null) {
			for (SubscriptionPaymentTier tier : getSubscriptionPaymentTiers()) {
				if (tier.getMinimum().compareTo(throughput) <= 0 && tier.getMaximum().compareTo(throughput) >= 0) {
					return tier;
				}
			}
		}
		return null;
	}

	@Transient
	public SubscriptionPaymentTier findActiveSubscriptionPaymentTierByPaymentTierCategory(SubscriptionPaymentTier.PaymentTierCategory paymentTierCategory) {
		if (paymentTierCategory != null) {
			for (SubscriptionPaymentTier paymentTier : getSubscriptionPaymentTiers()) {
				switch (paymentTierCategory) {
					case SOFTWARE:
						if (SubscriptionPaymentTierStatusType.ACTIVE.equals(paymentTier.getSubscriptionPaymentTierSoftwareStatusType().getCode())) {
							return paymentTier;
						}
						break;
					case VENDOR_OF_RECORD:
						if (SubscriptionPaymentTierStatusType.ACTIVE.equals(paymentTier.getSubscriptionPaymentTierVorStatusType().getCode())) {
							return paymentTier;
						}
						break;
				}
			}
		}
		return null;
	}

	@Transient
	public void resetSubscriptionPaymentTiers() {
		if (getSubscriptionPaymentTiers() != null) {
			for (int i = 0; i < getSubscriptionPaymentTiers().size(); i++) {
				SubscriptionPaymentTier paymentTier = (getSubscriptionPaymentTiers()).get(i);
				if (i == 0) {
					paymentTier.resetToActive();
				} else {
					paymentTier.resetToNotReached();
				}
			}
		}
	}


	@Override
	public int compareTo(SubscriptionFeeConfiguration subscriptionFeeConfiguration) {
		if (this.getEffectiveDate() == null) {
			return (subscriptionFeeConfiguration != null && subscriptionFeeConfiguration.getEffectiveDate() != null) ? -1 : 0;
		}
		if (subscriptionFeeConfiguration == null || subscriptionFeeConfiguration.getEffectiveDate() == null) {
			return 1;
		}
		return this.getEffectiveDate().compareTo(subscriptionFeeConfiguration.getEffectiveDate());
	}

}
