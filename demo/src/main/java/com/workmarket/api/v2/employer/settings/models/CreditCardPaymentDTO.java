package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.model.LocationDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("CreditCardPayment")
@JsonDeserialize(builder = CreditCardPaymentDTO.Builder.class)
public class CreditCardPaymentDTO {

	private final String amount;
	private final String cardType; // Values: Visa, MasterCard, Discover, Amex
	private final String cardNumber;
	private final String cardExpirationMonth;
	private final String cardExpirationYear;
	private final String cardSecurityCode;
	private final String nameOnCard;
	private final LocationDTO location;

	public CreditCardPaymentDTO(Builder builder) {
		this.amount = builder.amount;
		this.cardType = builder.cardType;
		this.cardNumber = builder.cardNumber;
		this.cardExpirationMonth = builder.cardExpirationMonth;
		this.cardExpirationYear = builder.cardExpirationYear;
		this.cardSecurityCode = builder.cardSecurityCode;
		this.nameOnCard = builder.nameOnCard;
		this.location = builder.location.build();
	}

	@ApiModelProperty(name = "amount")
	@JsonProperty("amount")
	public String getAmount() {
		return amount;
	}

	@ApiModelProperty(name = "cardType")
	@JsonProperty("cardType")
	public String getCardType() {
		return cardType;
	}

	@ApiModelProperty(name = "cardNumber")
	@JsonProperty("cardNumber")
	public String getCardNumber() {
		return cardNumber;
	}

	@ApiModelProperty(name = "cardExpirationMonth")
	@JsonProperty("cardExpirationMonth")
	public String getCardExpirationMonth() {
		return cardExpirationMonth;
	}

	@ApiModelProperty(name = "cardExpirationYear")
	@JsonProperty("cardExpirationYear")
	public String getCardExpirationYear() {
		return cardExpirationYear;
	}

	@ApiModelProperty(name = "cardSecurityCode")
	@JsonProperty("cardSecurityCode")
	public String getCardSecurityCode() {
		return cardSecurityCode;
	}

	@ApiModelProperty(name = "nameOnCard")
	@JsonProperty("nameOnCard")
	public String getNameOnCard() {
		return nameOnCard;
	}

	@ApiModelProperty(name = "location")
	@JsonProperty("location")
	public LocationDTO getLocation() {
		return location;
	}

	public static class Builder {
		private String amount;
		private String cardType;
		private String cardNumber;
		private String cardExpirationMonth;
		private String cardExpirationYear;
		private String cardSecurityCode;
		private String nameOnCard;
		private LocationDTO.Builder location = new LocationDTO.Builder();

		public Builder() {}

		public Builder(CreditCardPaymentDTO creditCardPaymentDTO) {
			this.amount = creditCardPaymentDTO.amount;
			this.cardType = creditCardPaymentDTO.cardType;
			this.cardNumber = creditCardPaymentDTO.cardNumber;
			this.cardExpirationMonth = creditCardPaymentDTO.cardExpirationMonth;
			this.cardExpirationYear = creditCardPaymentDTO.cardExpirationYear;
			this.cardSecurityCode = creditCardPaymentDTO.cardSecurityCode;
			this.nameOnCard = creditCardPaymentDTO.nameOnCard;
			this.location = new LocationDTO.Builder(creditCardPaymentDTO.location);
		}

		@JsonProperty("amount") public Builder setAmount(final String amount) {
			this.amount = amount;
			return this;
		}

		@JsonProperty("cardType") public Builder setCardType(final String cardType) {
			this.cardType = cardType;
			return this;
		}

		@JsonProperty("cardNumber") public Builder setCardNumber(final String cardNumber) {
			this.cardNumber = cardNumber;
			return this;
		}

		@JsonProperty("cardExpirationMonth") public Builder setCardExpirationMonth(final String cardExpirationMonth) {
			this.cardExpirationMonth = cardExpirationMonth;
			return this;
		}

		@JsonProperty("cardExpirationYear") public Builder setCardExpirationYear(final String cardExpirationYear) {
			this.cardExpirationYear = cardExpirationYear;
			return this;
		}

		@JsonProperty("cardSecurityCode") public Builder setCardSecurityCode(final String cardSecurityCode) {
			this.cardSecurityCode = cardSecurityCode;
			return this;
		}

		@JsonProperty("nameOnCard") public Builder setNameOnCard(final String nameOnCard) {
			this.nameOnCard = nameOnCard;
			return this;
		}

		@JsonProperty("location") public Builder setLocation(final LocationDTO.Builder location) {
			this.location = location;
			return this;
		}

		public CreditCardPaymentDTO build() {
			return new CreditCardPaymentDTO(this);
		}
	}
}
