package com.workmarket.api.v2.worker.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@ApiModel("WithdrawalRequest")
@JsonDeserialize(builder = WithdrawalRequestDTO.Builder.class)
public class WithdrawalRequestDTO {

	@NotNull
	private final Long account;

	@NotNull
	@DecimalMin(value = "0.01", message = "Amount cannot be less than $.01")
	private final BigDecimal amount;

	private WithdrawalRequestDTO(Builder builder) {
		account = builder.account;
		amount = builder.amount;
	}

	@ApiModelProperty(name = "account")
	@JsonProperty("account")
	public Long getAccount() {
		return account;
	}

	@ApiModelProperty(name = "amount")
	@JsonProperty("amount")
	public BigDecimal getAmount() {
		return amount;
	}

	public static final class Builder {
		private Long account;
		private BigDecimal amount;

		public Builder() {
		}

		public Builder(WithdrawalRequestDTO copy) {
			this.account = copy.account;
			this.amount = copy.amount;
		}

		@JsonProperty("account")
		public Builder withAccount(Long account) {
			this.account = account;
			return this;
		}

		@JsonProperty("amount")
		public Builder withAmount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}

		public WithdrawalRequestDTO build() {
			return new WithdrawalRequestDTO(this);
		}
	}
}
