package com.workmarket.domains.model.account.pricing;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Access(AccessType.PROPERTY)
public class AccountPricingServiceTypeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private AccountPricingType accountPricingType = new AccountPricingType(AccountPricingType.TRANSACTIONAL_PRICING_TYPE);
	private AccountServiceType accountServiceType = new AccountServiceType(AccountServiceType.NONE);

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "account_pricing_type_code", referencedColumnName = "code")
	public AccountPricingType getAccountPricingType() {
		return accountPricingType;
	}

	public void setAccountPricingType(AccountPricingType accountPricingType) {
		this.accountPricingType = accountPricingType;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "account_service_type_code", referencedColumnName = "code")
	public AccountServiceType getAccountServiceType() {
		return accountServiceType;
	}

	public void setAccountServiceType(AccountServiceType accountServiceType) {
		this.accountServiceType = accountServiceType;
	}
}