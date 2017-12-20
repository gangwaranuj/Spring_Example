package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModel;

@ApiModel("PermissionSettings")
@JsonDeserialize(builder = PermissionSettingsDTO.Builder.class)
public class PermissionSettingsDTO {

	private final boolean paymentAccessible;
	private final boolean fundsAccessible;
	private final boolean counterOfferAccessible;
	private final boolean pricingEditable;
	private final boolean workApprovalAllowed;
	private final boolean projectAccessible;

	private PermissionSettingsDTO(Builder builder) {
		this.paymentAccessible = builder.paymentAccessible;
		this.fundsAccessible = builder.fundsAccessible;
		this.counterOfferAccessible = builder.counterOfferAccessible;
		this.pricingEditable = builder.pricingEditable;
		this.workApprovalAllowed = builder.workApprovalAllowed;
		this.projectAccessible = builder.projectAccessible;
	}

	public boolean isPaymentAccessible() {
		return paymentAccessible;
	}

	public boolean isFundsAccessible() {
		return fundsAccessible;
	}

	public boolean isCounterOfferAccessible() {
		return counterOfferAccessible;
	}

	public boolean isPricingEditable() {
		return pricingEditable;
	}

	public boolean isWorkApprovalAllowed() {
		return workApprovalAllowed;
	}

	public boolean isProjectAccessible() {
		return projectAccessible;
	}

	public static class Builder implements AbstractBuilder<PermissionSettingsDTO> {

		private boolean paymentAccessible;
		private boolean fundsAccessible;
		private boolean counterOfferAccessible;
		private boolean pricingEditable;
		private boolean workApprovalAllowed;
		private boolean projectAccessible;

		public Builder(){}

		public Builder(PermissionSettingsDTO permissionSettingsDTO) {
			this.paymentAccessible = permissionSettingsDTO.paymentAccessible;
			this.fundsAccessible = permissionSettingsDTO.fundsAccessible;
			this.counterOfferAccessible = permissionSettingsDTO.counterOfferAccessible;
			this.pricingEditable = permissionSettingsDTO.pricingEditable;
			this.workApprovalAllowed = permissionSettingsDTO.workApprovalAllowed;
			this.projectAccessible = permissionSettingsDTO.projectAccessible;
		}

		@JsonProperty("paymentAccessible") public Builder setPaymentAccessible(final boolean paymentAccessible) {
			this.paymentAccessible = paymentAccessible;
			return this;
		}

		@JsonProperty("fundsAccessible") public Builder setFundsAccessible(final boolean fundsAccessible) {
			this.fundsAccessible = fundsAccessible;
			return this;
		}

		@JsonProperty("counterOfferAccessible") public Builder setCounterOfferAccessible(final boolean counterOfferAccessible) {
			this.counterOfferAccessible = counterOfferAccessible;
			return this;
		}

		@JsonProperty("pricingEditable") public Builder setPricingEditable(final boolean pricingEditable) {
			this.pricingEditable = pricingEditable;
			return this;
		}

		@JsonProperty("workApprovalAllowed") public Builder setWorkApprovalAllowed(final boolean workApprovalAllowed) {
			this.workApprovalAllowed = workApprovalAllowed;
			return this;
		}

		@JsonProperty("projectAccessible") public Builder setProjectAccessible(final boolean projectAccessible) {
			this.projectAccessible = projectAccessible;
			return this;
		}

		@Override
		public PermissionSettingsDTO build() {
			return new PermissionSettingsDTO(this);
		}
	}
}
