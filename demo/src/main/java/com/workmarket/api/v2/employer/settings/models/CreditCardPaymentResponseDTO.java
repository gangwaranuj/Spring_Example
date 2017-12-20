package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "CreditCardPaymentResponse")
@JsonDeserialize(builder = CreditCardPaymentResponseDTO.Builder.class)
public class CreditCardPaymentResponseDTO {

	private boolean approved;
	private String responseMessage;
	private Long creditCardTransactionId;
	private Long creditCardFeeTransactionId;

	public CreditCardPaymentResponseDTO(CreditCardPaymentResponseDTO.Builder builder) {
		this.approved = builder.approved;
		this.responseMessage = builder.responseMessage;
		this.creditCardTransactionId = builder.creditCardTransactionId;
		this.creditCardFeeTransactionId = builder.creditCardFeeTransactionId;
	}

	@ApiModelProperty(name = "approved")
	@JsonProperty("approved")
	public boolean getApproved() {
		return approved;
	}

	@ApiModelProperty(name = "responseMessage")
	@JsonProperty("responseMessage")
	public String getResponseMessage() {
		return responseMessage;
	}

	@ApiModelProperty(name = "creditCardTransactionId")
	@JsonProperty("creditCardTransactionId")
	public Long getCreditCardTransactionId() {
		return creditCardTransactionId;
	}

	@ApiModelProperty(name = "creditCardFeeTransactionId")
	@JsonProperty("creditCardFeeTransactionId")
	public Long getCreditCardFeeTransactionId() {
		return creditCardFeeTransactionId;
	}

	public static class Builder {
		private boolean approved;
		private String responseMessage;
		private Long creditCardTransactionId;
		private Long creditCardFeeTransactionId;

		public Builder() {}

		public Builder(CreditCardPaymentResponseDTO creditCardPaymentResponseDTO) {
			this.approved = creditCardPaymentResponseDTO.approved;
			this.responseMessage = creditCardPaymentResponseDTO.responseMessage;
			this.creditCardTransactionId = creditCardPaymentResponseDTO.creditCardTransactionId;
			this.creditCardFeeTransactionId = creditCardPaymentResponseDTO.creditCardFeeTransactionId;
		}

		@JsonProperty("approved") public Builder setApproved(final boolean approved) {
			this.approved = approved;
			return this;
		}

		@JsonProperty("responseMessage") public Builder setResponseMessage(final String responseMessage) {
			this.responseMessage = responseMessage;
			return this;
		}

		@JsonProperty("creditCardTransactionId") public Builder setCreditCardTransactionId(final Long creditCardTransactionId) {
			this.creditCardTransactionId = creditCardTransactionId;
			return this;
		}

		@JsonProperty("creditCardFeeTransactionId") public Builder setCreditCardFeeTransactionId(final Long creditCardFeeTransactionId) {
			this.creditCardFeeTransactionId = creditCardFeeTransactionId;
			return this;
		}

		public CreditCardPaymentResponseDTO build() {
			return new CreditCardPaymentResponseDTO(this);
		}
	}
}
