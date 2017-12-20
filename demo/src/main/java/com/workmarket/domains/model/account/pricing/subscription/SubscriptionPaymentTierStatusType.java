package com.workmarket.domains.model.account.pricing.subscription;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "subscriptionPaymentTierStatusType")
@Table(name = "subscription_payment_tier_status_type")
public class SubscriptionPaymentTierStatusType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String ACTIVE = "active";
	public static final String PROCESSING = "processing";
	public static final String NOT_REACHED = "not_reached";
	public static final String REACHED = "reached";

	public SubscriptionPaymentTierStatusType() {
	}

	public SubscriptionPaymentTierStatusType(String code) {
		super(code);
	}

	@Transient
	public boolean isActive() {
		return getCode().equals(ACTIVE);
	}

	@Transient
	public boolean isProcessing() {
		return getCode().equals(PROCESSING);
	}

	@Transient
	public boolean isNotReached() {
		return getCode().equals(NOT_REACHED);
	}
}
