package com.workmarket.service.business.dto.account.pricing;

/**
 * @author gparada
 */
public class AccountServiceTypeDTO {

	private String countryCode;
	private String accountServiceTypeCode;

	public AccountServiceTypeDTO() {
	}

	public AccountServiceTypeDTO(String accountServiceTypeCode, String countryCode) {
		this.accountServiceTypeCode = accountServiceTypeCode;
		this.countryCode = countryCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getAccountServiceTypeCode() {
		return accountServiceTypeCode;
	}

	public void setAccountServiceTypeCode(String accountServiceTypeCode) {
		this.accountServiceTypeCode = accountServiceTypeCode;
	}
}