package com.workmarket.domains.model.account.pricing.subscription;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "subscriptionPeriodType")
@Table(name = "subscription_period_type")
public class SubscriptionPeriodType extends LookupEntity {

	private static final long serialVersionUID = 2779131134653411973L;

	public static final String AUTO = "auto";
	public static final String ADHOC = "adhoc";

	public SubscriptionPeriodType() {
	}

	public SubscriptionPeriodType(String code) {
		super(code);
	}
}
