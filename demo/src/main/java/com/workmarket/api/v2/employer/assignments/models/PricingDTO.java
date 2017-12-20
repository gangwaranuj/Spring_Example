package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Pricing")
@JsonDeserialize(builder = PricingDTO.Builder.class)
public class PricingDTO {
	private final String mode;
	private final String type;
	private final Double flatPrice;
	private final Double perHourPrice;
	private final Double maxNumberOfHours;
	private final Double perUnitPrice;
	private final Double maxNumberOfUnits;
	private final Double initialPerHourPrice;
	private final Double initialNumberOfHours;
	private final Double additionalPerHourPrice;
	private final Double maxBlendedNumberOfHours;
	private final Integer paymentTermsDays;
	private final boolean offlinePayment;
	private final Boolean disablePriceNegotiation;

	private PricingDTO(Builder builder) {
		this.mode = builder.mode;
		this.type = builder.type;
		this.flatPrice = builder.flatPrice;
		this.perHourPrice = builder.perHourPrice;
		this.maxNumberOfHours = builder.maxNumberOfHours;
		this.perUnitPrice = builder.perUnitPrice;
		this.maxNumberOfUnits = builder.maxNumberOfUnits;
		this.initialPerHourPrice = builder.initialPerHourPrice;
		this.initialNumberOfHours = builder.initialNumberOfHours;
		this.additionalPerHourPrice = builder.additionalPerHourPrice;
		this.maxBlendedNumberOfHours = builder.maxBlendedNumberOfHours;
		this.paymentTermsDays = builder.paymentTermsDays;
		this.offlinePayment = builder.offlinePayment;
		this.disablePriceNegotiation = builder.disablePriceNegotiation;
	}

	@ApiModelProperty(name = "mode")
	@JsonProperty("mode")
	public String getMode() {
		return mode;
	}

	@ApiModelProperty(name = "type")
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@ApiModelProperty(name = "flatPrice")
	@JsonProperty("flatPrice")
	public Double getFlatPrice() {
		return flatPrice;
	}

	@ApiModelProperty(name = "perHourPrice")
	@JsonProperty("perHourPrice")
	public Double getPerHourPrice() {
		return perHourPrice;
	}

	@ApiModelProperty(name = "maxNumberOfHours")
	@JsonProperty("maxNumberOfHours")
	public Double getMaxNumberOfHours() {
		return maxNumberOfHours;
	}

	@ApiModelProperty(name = "perUnitPrice")
	@JsonProperty("perUnitPrice")
	public Double getPerUnitPrice() {
		return perUnitPrice;
	}

	@ApiModelProperty(name = "maxNumberOfUnits")
	@JsonProperty("maxNumberOfUnits")
	public Double getMaxNumberOfUnits() {
		return maxNumberOfUnits;
	}

	@ApiModelProperty(name = "initialPerHourPrice")
	@JsonProperty("initialPerHourPrice")
	public Double getInitialPerHourPrice() {
		return initialPerHourPrice;
	}

	@ApiModelProperty(name = "initialNumberOfHours")
	@JsonProperty("initialNumberOfHours")
	public Double getInitialNumberOfHours() {
		return initialNumberOfHours;
	}

	@ApiModelProperty(name = "additionalPerHourPrice")
	@JsonProperty("additionalPerHourPrice")
	public Double getAdditionalPerHourPrice() {
		return additionalPerHourPrice;
	}

	@ApiModelProperty(name = "maxBlendedNumberOfHours")
	@JsonProperty("maxBlendedNumberOfHours")
	public Double getMaxBlendedNumberOfHours() {
		return maxBlendedNumberOfHours;
	}

	@ApiModelProperty(name = "paymentTermsDays")
	@JsonProperty("paymentTermsDays")
	public Integer getPaymentTermsDays() {
		return paymentTermsDays;
	}

	public boolean isOfflinePayment() {
		return offlinePayment;
	}

	@ApiModelProperty(name = "disablePriceNegotiation")
	@JsonProperty("disablePriceNegotiation")
	public Boolean getDisablePriceNegotiation() {
		return disablePriceNegotiation;
	}

	public static class Builder implements AbstractBuilder<PricingDTO> {
		private String mode;
		private String type;
		private Double flatPrice;
		private Double perHourPrice;
		private Double maxNumberOfHours;
		private Double perUnitPrice;
		private Double maxNumberOfUnits;
		private Double initialPerHourPrice;
		private Double initialNumberOfHours;
		private Double additionalPerHourPrice;
		private Double maxBlendedNumberOfHours;
		private Integer paymentTermsDays;
		private boolean offlinePayment = false;
		private boolean disablePriceNegotiation = false;

		public Builder() {}

		public Builder(PricingDTO pricingDTO) {
			this.mode = pricingDTO.mode;
			this.type = pricingDTO.type;
			this.flatPrice = pricingDTO.flatPrice;
			this.perHourPrice = pricingDTO.perHourPrice;
			this.maxNumberOfHours = pricingDTO.maxNumberOfHours;
			this.perUnitPrice = pricingDTO.perUnitPrice;
			this.maxNumberOfUnits = pricingDTO.maxNumberOfUnits;
			this.initialPerHourPrice = pricingDTO.initialPerHourPrice;
			this.initialNumberOfHours = pricingDTO.initialNumberOfHours;
			this.additionalPerHourPrice = pricingDTO.additionalPerHourPrice;
			this.maxBlendedNumberOfHours = pricingDTO.maxBlendedNumberOfHours;
			this.paymentTermsDays = pricingDTO.paymentTermsDays;
			this.offlinePayment = pricingDTO.offlinePayment;
			this.disablePriceNegotiation = pricingDTO.disablePriceNegotiation;
		}

		@JsonProperty("mode") public Builder setMode(String mode) {
			this.mode = mode;
			return this;
		}

		@JsonProperty("type") public Builder setType(String type) {
			this.type = type;
			return this;
		}

		@JsonProperty("flatPrice") public Builder setFlatPrice(Double flatPrice) {
			this.flatPrice = flatPrice;
			return this;
		}

		@JsonProperty("perHourPrice") public Builder setPerHourPrice(Double perHourPrice) {
			this.perHourPrice = perHourPrice;
			return this;
		}

		@JsonProperty("maxNumberOfHours") public Builder setMaxNumberOfHours(Double maxNumberOfHours) {
			this.maxNumberOfHours = maxNumberOfHours;
			return this;
		}

		@JsonProperty("perUnitPrice") public Builder setPerUnitPrice(Double perUnitPrice) {
			this.perUnitPrice = perUnitPrice;
			return this;
		}

		@JsonProperty("maxNumberOfUnits") public Builder setMaxNumberOfUnits(Double maxNumberOfUnits) {
			this.maxNumberOfUnits = maxNumberOfUnits;
			return this;
		}

		@JsonProperty("initialPerHourPrice") public Builder setInitialPerHourPrice(Double initialPerHourPrice) {
			this.initialPerHourPrice = initialPerHourPrice;
			return this;
		}

		@JsonProperty("initialNumberOfHours") public Builder setInitialNumberOfHours(Double initialNumberOfHours) {
			this.initialNumberOfHours = initialNumberOfHours;
			return this;
		}

		@JsonProperty("additionalPerHourPrice") public Builder setAdditionalPerHourPrice(Double additionalPerHourPrice) {
			this.additionalPerHourPrice = additionalPerHourPrice;
			return this;
		}

		@JsonProperty("maxBlendedNumberOfHours") public Builder setMaxBlendedNumberOfHours(Double maxBlendedNumberOfHours) {
			this.maxBlendedNumberOfHours = maxBlendedNumberOfHours;
			return this;
		}

		@JsonProperty("paymentTermsDays") public Builder setPaymentTermsDays(Integer paymentTermsDays) {
			this.paymentTermsDays = paymentTermsDays;
			return this;
		}

		@JsonProperty("offlinePayment") public Builder setOfflinePayment(boolean offlinePayment) {
			this.offlinePayment = offlinePayment;
			return this;
		}

		@JsonProperty("disablePriceNegotiation") public Builder setDisablePriceNegotiation(boolean disablePriceNegotiation) {
			this.disablePriceNegotiation = disablePriceNegotiation;
			return this;
		}

		public PricingDTO build() {
			return new PricingDTO(this);
		}
	}
}
