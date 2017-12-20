package com.workmarket.domains.model.account.pricing;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity(name = "accountPricingType")
@Table(name = "account_pricing_type")
@AttributeOverrides({
	@AttributeOverride(name = "code", column = @Column(length = 15))
})
public class AccountPricingType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String TRANSACTIONAL_PRICING_TYPE = "transactional";
	public static final String SUBSCRIPTION_PRICING_TYPE = "subscription";

	public AccountPricingType() {
		super();
	}

	public AccountPricingType(String code) {
		super(code);
	}

	@Transient
	public boolean isTransactionalPricing() {
		return getCode().equals(TRANSACTIONAL_PRICING_TYPE);
	}

	@Transient
	public boolean isSubscriptionPricing() {
		return getCode().equals(SUBSCRIPTION_PRICING_TYPE);
	}
}