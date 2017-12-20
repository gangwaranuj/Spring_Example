package com.workmarket.api.v2.employer.paymentconfiguration.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("PaymentConfiguration")
@JsonDeserialize(builder = PaymentConfigurationDTO.Builder.class)
public class PaymentConfigurationDTO {
	private final BigDecimal workFeePercentage;
	private final boolean subscribed;
	private final Integer assignmentPricingType;
	private final boolean offlinePaymentEnabled;

	private PaymentConfigurationDTO(Builder builder) {
		this.workFeePercentage = builder.workFeePercentage;
		this.subscribed = builder.subscribed;
		this.assignmentPricingType = builder.assignmentPricingType;
		this.offlinePaymentEnabled = builder.offlinePaymentEnabled;
	}

	@ApiModelProperty(name = "workFeePercentage")
	@JsonProperty("workFeePercentage")
	public BigDecimal getWorkFeePercentage() {
		return workFeePercentage;
	}

	@ApiModelProperty(name = "subscribed")
	@JsonProperty("subscribed")
	public boolean isSubscribed() {
		return subscribed;
	}

	@ApiModelProperty(name = "assignmentPricingType")
	@JsonProperty("assignmentPricingType")
	public Integer getAssignmentPricingType() {
		return assignmentPricingType;
	}

	public boolean isOfflinePaymentEnabled() {
		return offlinePaymentEnabled;
	}

	public static class Builder implements AbstractBuilder<PaymentConfigurationDTO> {
		private BigDecimal workFeePercentage;
		private boolean subscribed = false;
		private Integer assignmentPricingType;
		private boolean offlinePaymentEnabled = false;

		public Builder(PaymentConfigurationDTO paymentConfigurationDTO) {
			this.workFeePercentage = paymentConfigurationDTO.workFeePercentage;
			this.subscribed = paymentConfigurationDTO.subscribed;
			this.assignmentPricingType = paymentConfigurationDTO.assignmentPricingType;
			this.offlinePaymentEnabled = paymentConfigurationDTO.offlinePaymentEnabled;
		}

		public Builder() {}

		@JsonProperty("workFeePercentage") public Builder setWorkFeePercentage(BigDecimal workFeePercentage) {
			this.workFeePercentage = workFeePercentage;
			return this;
		}

		@JsonProperty("subscribed") public Builder setSubscribed(boolean subscribed) {
			this.subscribed = subscribed;
			return this;
		}

		@JsonProperty("assignmentPricingType") public Builder setAssignmentPricingType(Integer assignmentPricingType) {
			this.assignmentPricingType = assignmentPricingType;
			return this;
		}

		@JsonProperty("offlinePaymentEnabled")
		public Builder setOfflinePaymentEnabled(boolean offlinePaymentEnabled) {
			this.offlinePaymentEnabled = offlinePaymentEnabled;
			return this;
		}

		@Override
		public PaymentConfigurationDTO build() {
			return new PaymentConfigurationDTO(this);
		}
	}
}