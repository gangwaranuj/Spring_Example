package com.workmarket.domains.model.account.pricing;

import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity(name = "accountServiceTypeConfiguration")
@Table(name = "account_service_type_configuration")
@AuditChanges
public class AccountServiceTypeConfiguration extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private PaymentConfiguration paymentConfiguration;
	private Country country;
	private AccountServiceType accountServiceType;

	public AccountServiceTypeConfiguration() {
	}

	public AccountServiceTypeConfiguration(AccountServiceType accountServiceType, Country country) {
		this.accountServiceType = accountServiceType;
		this.country = country;
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
	@JoinColumn(name = "payment_configuration_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
	public PaymentConfiguration getPaymentConfiguration() {
		return paymentConfiguration;
	}

	public void setPaymentConfiguration(PaymentConfiguration paymentConfiguration) {
		this.paymentConfiguration = paymentConfiguration;
	}
}
