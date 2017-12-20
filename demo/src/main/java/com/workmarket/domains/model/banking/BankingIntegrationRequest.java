package com.workmarket.domains.model.banking;

import com.workmarket.domains.model.postalcode.Country;
import org.springframework.util.Assert;

import java.io.Serializable;

public class BankingIntegrationRequest implements Serializable {

	private static final long serialVersionUID = 6935055607041660966L;

	private String bankingIntegrationRequestType;
	private String transactionType;
	private boolean autoApproved;
	private Country country;

	public BankingIntegrationRequest(String bankingIntegrationRequestType, String transactionType) {
		Assert.notNull(bankingIntegrationRequestType);
		Assert.notNull(transactionType);
		this.bankingIntegrationRequestType = bankingIntegrationRequestType;
		this.transactionType = transactionType;
	}

	public boolean isAutoApproved() {
		return autoApproved;
	}

	public BankingIntegrationRequest setAutoApproved(boolean autoApproved) {
		this.autoApproved = autoApproved;
		return this;
	}

	public String getBankingIntegrationRequestType() {
		return bankingIntegrationRequestType;
	}

	public Country getCountry() {
		return country;
	}

	public BankingIntegrationRequest setCountry(Country country) {
		this.country = country;
		return this;
	}

	public String getTransactionType() {
		return transactionType;
	}
}
