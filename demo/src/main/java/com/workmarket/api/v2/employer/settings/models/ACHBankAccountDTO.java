package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * This DTO is the subset of the BankAccountDTO, which only servers the purpose of saving ACH of account (checking or saving)
 */
@ApiModel("ACHBankAccont")
@JsonDeserialize(builder = ACHBankAccountDTO.Builder.class)
public class ACHBankAccountDTO {

	private final String type;
	private final String bankName;
	private final String nameOnAccount;
	private final String routingNumber;
	private final String accountNumber;
	private final String accountNumberConfirm;
	private final String bankAccountTypeCode;
	private final String country;

	public ACHBankAccountDTO(ACHBankAccountDTO.Builder builder) {
		this.type = builder.type;
		this.bankName = builder.bankName;
		this.nameOnAccount = builder.nameOnAccount;
		this.routingNumber = builder.routingNumber;
		this.accountNumber = builder.accountNumber;
		this.accountNumberConfirm = builder.accountNumberConfirm;
		this.bankAccountTypeCode = builder.bankAccountTypeCode;
		this.country = builder.country;
	}

	@ApiModelProperty(name = "type")
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@ApiModelProperty(name = "bankName")
	@JsonProperty("bankName")
	public String getBankName() {
		return bankName;
	}

	@ApiModelProperty(name = "nameOnAccount")
	@JsonProperty("nameOnAccount")
	public String getNameOnAccount() {
		return nameOnAccount;
	}

	@ApiModelProperty(name = "routingNumber")
	@JsonProperty("routingNumber")
	public String getRoutingNumber() {
		return routingNumber;
	}

	@ApiModelProperty(name = "accountNumber")
	@JsonProperty("accountNumber")
	public String getAccountNumber() {
		return accountNumber;
	}

	@ApiModelProperty(name = "accountNumberConfirm")
	@JsonProperty("accountNumberConfirm")
	public String getAccountNumberConfirm() {
		return accountNumberConfirm;
	}

	@ApiModelProperty(name = "bankAccountTypeCode")
	@JsonProperty("bankAccountTypeCode")
	public String getBankAccountTypeCode() {
		return bankAccountTypeCode;
	}

	@ApiModelProperty(name = "country")
	@JsonProperty("country")
	public String getCountry() {
		return country;
	}

	public static class Builder {
		private String type;
		private String bankName;
		private String nameOnAccount;
		private String routingNumber;
		private String accountNumber;
		private String accountNumberConfirm;
		private String bankAccountTypeCode;
		private String country;

		public Builder() {}

		public Builder(ACHBankAccountDTO bankAccountDTO) {
			this.type = bankAccountDTO.type;
			this.bankName = bankAccountDTO.bankName;
			this.nameOnAccount = bankAccountDTO.nameOnAccount;
			this.routingNumber = bankAccountDTO.routingNumber;
			this.accountNumber = bankAccountDTO.accountNumber;
			this.accountNumberConfirm = bankAccountDTO.accountNumberConfirm;
			this.bankAccountTypeCode = bankAccountDTO.bankAccountTypeCode;
			this.country = bankAccountDTO.country;
		}

		@JsonProperty("type") public Builder setType(String type) {
			this.type = type;
			return this;
		}

		@JsonProperty("bankName") public Builder setBankName(String bankName) {
			this.bankName = bankName;
			return this;
		}

		@JsonProperty("nameOnAccount") public Builder setNameOnAccount(String nameOnAccount) {
			this.nameOnAccount = nameOnAccount;
			return this;
		}

		@JsonProperty("routingNumber") public Builder setRoutingNumber(String routingNumber) {
			this.routingNumber = routingNumber;
			return this;
		}

		@JsonProperty("accountNumber") public Builder setAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
			return this;
		}

		@JsonProperty("accountNumberConfirm") public Builder setAccountNumberConfirm(String accountNumberConfirm) {
			this.accountNumberConfirm = accountNumberConfirm;
			return this;
		}

		@JsonProperty("bankAccountTypeCode") public Builder setBankAccountTypeCode(String bankAccountTypeCode) {
			this.bankAccountTypeCode = bankAccountTypeCode;
			return this;
		}

		@JsonProperty("country") public Builder setCountry(String country) {
			this.country = country;
			return this;
		}

		public ACHBankAccountDTO build() {
			return new ACHBankAccountDTO(this);
		}
	}
}
