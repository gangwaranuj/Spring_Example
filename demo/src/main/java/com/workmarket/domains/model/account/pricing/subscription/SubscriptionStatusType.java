package com.workmarket.domains.model.account.pricing.subscription;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "subscriptionStatusType")
@Table(name = "subscription_status_type")
public class SubscriptionStatusType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String PENDING = "pending";
	public static final String ACTIVE = "active";
	public static final String CANCELLED = "cancelled";
	public static final String EXPIRED = "expired";
	public static final String REJECTED = "rejected";
	public static final String PENDING_RENEWAL = "renewal";

	public SubscriptionStatusType() {
	}

	public SubscriptionStatusType(String code) {
		super(code);
	}

	@Transient
	public boolean isPending() {
		return getCode().equals(PENDING);
	}

	@Transient
	public boolean isActive() {
		return getCode().equals(ACTIVE);
	}

	@Transient
	public boolean isCancelled() {
		return getCode().equals(CANCELLED);
	}

	@Transient
	public boolean isExpired() {
		return getCode().equals(EXPIRED);
	}

	@Transient
	public boolean isRejected() {
		return getCode().equals(REJECTED);
	}
	
	@Transient
	public boolean isPendingRenewal() {
		return getCode().equals(PENDING_RENEWAL);
	}
}
