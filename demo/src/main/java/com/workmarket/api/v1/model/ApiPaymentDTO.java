package com.workmarket.api.v1.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Payment")
@JsonDeserialize(builder = ApiPaymentDTO.Builder.class)
public class ApiPaymentDTO {

	private final Double maxSpendLimit;
	private final Double actualSpendLimit;
	private final Double buyerFee;
	private final Double totalCost;
	private final Double hoursWorked;
	private final Long paidOn;
	private final Long paymentDueOn;

	private ApiPaymentDTO(Builder builder) {
		maxSpendLimit = builder.maxSpendLimit;
		actualSpendLimit = builder.actualSpendLimit;
		buyerFee = builder.buyerFee;
		totalCost = builder.totalCost;
		hoursWorked = builder.hoursWorked;
		paidOn = builder.paidOn;
		paymentDueOn = builder.paymentDueOn;
	}

	@ApiModelProperty(name = "max_spend_limit")
	@JsonProperty("max_spend_limit")
	public Double getMaxSpendLimit() {
		return maxSpendLimit;
	}

	@ApiModelProperty(name = "actual_spend_limit")
	@JsonProperty("actual_spend_limit")
	public Double getActualSpendLimit() {
		return actualSpendLimit;
	}

	@ApiModelProperty(name = "buyer_fee")
	@JsonProperty("buyer_fee")
	public Double getBuyerFee() {
		return buyerFee;
	}

	@ApiModelProperty(name = "total_cost")
	@JsonProperty("total_cost")
	public Double getTotalCost() {
		return totalCost;
	}

	@ApiModelProperty(name = "hours_worked")
	@JsonProperty("hours_worked")
	public Double getHoursWorked() {
		return hoursWorked;
	}

	@ApiModelProperty(name = "paid_on")
	@JsonProperty("paid_on")
	public Long getPaidOn() {
		return paidOn;
	}

	@ApiModelProperty(name = "payment_due_on")
	@JsonProperty("payment_due_on")
	public Long getPaymentDueOn() {
		return paymentDueOn;
	}


	public static final class Builder {
		private Double maxSpendLimit;
		private Double actualSpendLimit;
		private Double buyerFee;
		private Double totalCost;
		private Double hoursWorked;
		private Long paidOn;
		private Long paymentDueOn;

		public Builder() {
		}

		public Builder(ApiPaymentDTO copy) {
			this.maxSpendLimit = copy.maxSpendLimit;
			this.actualSpendLimit = copy.actualSpendLimit;
			this.buyerFee = copy.buyerFee;
			this.totalCost = copy.totalCost;
			this.hoursWorked = copy.hoursWorked;
			this.paidOn = copy.paidOn;
			this.paymentDueOn = copy.paymentDueOn;
		}

		@JsonProperty("max_spend_limit")
		public Builder withMaxSpendLimit(Double maxSpendLimit) {
			this.maxSpendLimit = maxSpendLimit;
			return this;
		}

		@JsonProperty("actual_spend_limit")
		public Builder withActualSpendLimit(Double actualSpendLimit) {
			this.actualSpendLimit = actualSpendLimit;
			return this;
		}

		@JsonProperty("buyer_fee")
		public Builder withBuyerFee(Double buyerFee) {
			this.buyerFee = buyerFee;
			return this;
		}

		@JsonProperty("total_cost")
		public Builder withTotalCost(Double totalCost) {
			this.totalCost = totalCost;
			return this;
		}

		@JsonProperty("hours_worked")
		public Builder withHoursWorked(Double hoursWorked) {
			this.hoursWorked = hoursWorked;
			return this;
		}

		@JsonProperty("paid_on")
		public Builder withPaidOn(Long paidOn) {
			this.paidOn = paidOn;
			return this;
		}

		@JsonProperty("payment_due_on")
		public Builder withPaymentDueOn(Long paymentDueOn) {
			this.paymentDueOn = paymentDueOn;
			return this;
		}

		public ApiPaymentDTO build() {
			return new ApiPaymentDTO(this);
		}
	}
}
