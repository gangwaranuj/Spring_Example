package com.workmarket.domains.model.account.pricing;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity(name = "subscriptionAccountServiceTypeConfiguration")
@Table(name = "subscription_account_service_type_configuration")
@AuditChanges
public class SubscriptionAccountServiceTypeConfiguration extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private SubscriptionConfiguration subscriptionConfiguration;
	private Country country;
	private AccountServiceType accountServiceType;

	public SubscriptionAccountServiceTypeConfiguration() {
	}

	public SubscriptionAccountServiceTypeConfiguration(AccountServiceType accountServiceType, Country country, SubscriptionConfiguration subscriptionConfiguration) {
		this.accountServiceType = accountServiceType;
		this.country = country;
		this.subscriptionConfiguration = subscriptionConfiguration;
	}

	@ManyToOne
	@JoinColumn(name = "account_service_type_code", referencedColumnName = "code")
	public AccountServiceType getAccountServiceType() {
		return accountServiceType;
	}

	public void setAccountServiceType(AccountServiceType accountServiceType) {
		this.accountServiceType = accountServiceType;
	}

	@ManyToOne
	@JoinColumn(name = "country_id", referencedColumnName = "id")
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@ManyToOne
	@JoinColumn(name = "subscription_configuration_id")
	public SubscriptionConfiguration getSubscriptionConfiguration() {
		return subscriptionConfiguration;
	}

	public void setSubscriptionConfiguration(SubscriptionConfiguration subscriptionConfiguration) {
		this.subscriptionConfiguration = subscriptionConfiguration;
	}

}
