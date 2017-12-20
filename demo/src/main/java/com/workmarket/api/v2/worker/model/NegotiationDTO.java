package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.thrift.work.Work;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Negotiation")
@JsonDeserialize(builder = NegotiationDTO.Builder.class)
public class NegotiationDTO {

	private final Double flatPrice;
	private final Double perHour;
	private final Double maxHours;
	private final Double initialHours;
	private final Double perAdditionalHour;
	private final Double maxAdditionalHours;
	private final Double perUnit;
	private final Double maxUnits;
	private final Double blendedPerHour;
	private final Boolean internal;
	private final Double bonus;
	private final Double reimbursement;
	private final String note;

	private NegotiationDTO(Builder builder) {
		flatPrice = builder.flatPrice;
		perHour = builder.perHour;
		maxHours = builder.maxHours;
		initialHours = builder.initialHours;
		perAdditionalHour = builder.perAdditionalHour;
		maxAdditionalHours = builder.maxAdditionalHours;
		perUnit = builder.perUnit;
		maxUnits = builder.maxUnits;
		blendedPerHour = builder.blendedPerHour;
		internal = builder.internal;
		bonus = builder.bonus;
		reimbursement = builder.reimbursement;
		note = builder.note;
	}

	@ApiModelProperty(name = "flatPrice")
	@JsonProperty("flatPrice")
	public Double getFlatPrice() {
		return flatPrice;
	}

	@ApiModelProperty(name = "perHour")
	@JsonProperty("perHour")
	public Double getPerHour() {
		return perHour;
	}

	@ApiModelProperty(name = "maxHours")
	@JsonProperty("maxHours")
	public Double getMaxHours() {
		return maxHours;
	}

	@ApiModelProperty(name = "initialHours")
	@JsonProperty("initialHours")
	public Double getInitialHours() {
		return initialHours;
	}

	@ApiModelProperty(name = "perAdditionalHour")
	@JsonProperty("perAdditionalHour")
	public Double getPerAdditionalHour() {
		return perAdditionalHour;
	}

	@ApiModelProperty(name = "maxAdditionalHours")
	@JsonProperty("maxAdditionalHours")
	public Double getMaxAdditionalHours() {
		return maxAdditionalHours;
	}

	@ApiModelProperty(name = "perUnit")
	@JsonProperty("perUnit")
	public Double getPerUnit() {
		return perUnit;
	}

	@ApiModelProperty(name = "maxUnits")
	@JsonProperty("maxUnits")
	public Double getMaxUnits() {
		return maxUnits;
	}

	@ApiModelProperty(name = "blendedPerHour")
	@JsonProperty("blendedPerHour")
	public Double getBlendedPerHour() {
		return blendedPerHour;
	}

	@ApiModelProperty(name = "internal")
	@JsonProperty("internal")
	public Boolean getInternal() {
		return internal;
	}

	@ApiModelProperty(name = "bonus")
	@JsonProperty("bonus")
	public Double getBonus() {
		return bonus;
	}

	@ApiModelProperty(name = "reimbursement")
	@JsonProperty("reimbursement")
	public Double getReimbursement() {
		return reimbursement;
	}

	@ApiModelProperty(name = "note")
	@JsonProperty("note")
	public String getNote() {
		return note;
	}

	public boolean hasBudgetNegotiation() {
		return hasValue(this.flatPrice) ||
			hasValue(this.blendedPerHour) ||
			hasValue(this.initialHours) ||
			hasValue(this.maxAdditionalHours) ||
			hasValue(this.maxHours) ||
			hasValue(this.maxUnits) ||
			hasValue(this.perAdditionalHour) ||
			hasValue(this.perHour) ||
			hasValue(this.perUnit);
	}

	public WorkNegotiationDTO toApplicationDTO(final Work work) {

		final Long pricingStrategyId = work.getPricing().getId();

		final WorkNegotiationDTO dto = toBaseDTO(work);

		dto.setPriceNegotiation(Boolean.TRUE);
		dto.setPricingStrategyId(pricingStrategyId);
		dto.setBonus(this.bonus);
		dto.setAdditionalExpenses(this.reimbursement == null ? 0 : this.reimbursement);

		return dto;
	}

	public WorkNegotiationDTO toBudgetIncreaseDTO(final Work work) {

		final WorkNegotiationDTO dto = toBaseDTO(work);

		dto.setPriceNegotiation(Boolean.TRUE);
		dto.setBudgetIncrease(Boolean.TRUE);
		dto.setNote(this.note);

		return dto;
	}

	public WorkNegotiationDTO toReimburseRequestDTO(final Work work) {

		final WorkNegotiationDTO dto = toBaseDTO(work);

		dto.setPriceNegotiation(Boolean.TRUE);
		dto.setAdditionalExpenses(this.reimbursement);
		dto.setNote(this.note);

		return dto;
	}

	public WorkNegotiationDTO toBonusRequestDTO(final Work work) {
		final WorkNegotiationDTO dto = toBaseDTO(work);
		dto.setPriceNegotiation(Boolean.TRUE);
		dto.setBonus(this.bonus);
		dto.setNote(this.note);
		return dto;
	}

	private WorkNegotiationDTO toBaseDTO(final Work work) {
		final WorkNegotiationDTO dto = new WorkNegotiationDTO();
		setDtoPricing(dto, work);
		return dto;
	}

	private void setDtoPricing(final WorkNegotiationDTO dto,
														 final Work work) {

		dto.setFlatPrice
			(
				flatPrice != null && flatPrice > 0 ? flatPrice : work.getPricing().getFlatPrice()
			);
		dto.setPerUnitPrice
			(
				perUnit != null && perUnit > 0 ? perUnit : work.getPricing().getPerUnitPrice()
			);
		dto.setPerHourPrice
			(
				perHour != null && perHour > 0 ? perHour : work.getPricing().getPerHourPrice()
			);
		dto.setMaxNumberOfUnits
			(
				maxUnits != null && maxUnits > 0 ? maxUnits : work.getPricing().getMaxNumberOfUnits()
			);
		dto.setMaxNumberOfHours
			(
				maxHours != null && maxHours > 0 ? maxHours : work.getPricing().getMaxNumberOfHours()
			);
		dto.setInitialPerHourPrice
			(
				blendedPerHour != null && blendedPerHour > 0 ? blendedPerHour : work.getPricing().getInitialPerHourPrice()
			);
		dto.setInitialNumberOfHours
			(
				initialHours != null && initialHours > 0 ? initialHours : work.getPricing().getInitialNumberOfHours()
			);
		dto.setAdditionalPerHourPrice
			(
				perAdditionalHour != null && perAdditionalHour > 0 ? perAdditionalHour : work.getPricing().getAdditionalPerHourPrice()
			);
		dto.setMaxBlendedNumberOfHours
			(
				maxAdditionalHours != null && maxAdditionalHours > 0 ? maxAdditionalHours : work.getPricing().getMaxBlendedNumberOfHours()
			);
	}

	private boolean hasValue(final Double number) {
		return (number != null && number > 0);
	}


	public static final class Builder {
		private Double flatPrice;
		private Double perHour;
		private Double maxHours;
		private Double initialHours;
		private Double perAdditionalHour;
		private Double maxAdditionalHours;
		private Double perUnit;
		private Double maxUnits;
		private Double blendedPerHour;
		private Boolean internal;
		private Double bonus;
		private Double reimbursement;
		private String note;

		public Builder() {
		}

		public Builder(NegotiationDTO copy) {
			this.flatPrice = copy.flatPrice;
			this.perHour = copy.perHour;
			this.maxHours = copy.maxHours;
			this.initialHours = copy.initialHours;
			this.perAdditionalHour = copy.perAdditionalHour;
			this.maxAdditionalHours = copy.maxAdditionalHours;
			this.perUnit = copy.perUnit;
			this.maxUnits = copy.maxUnits;
			this.blendedPerHour = copy.blendedPerHour;
			this.internal = copy.internal;
			this.bonus = copy.bonus;
			this.reimbursement = copy.reimbursement;
			this.note = copy.note;
		}

		@JsonProperty("flatPrice")
		public Builder withFlatPrice(Double flatPrice) {
			this.flatPrice = flatPrice;
			return this;
		}

		@JsonProperty("perHour")
		public Builder withPerHour(Double perHour) {
			this.perHour = perHour;
			return this;
		}

		@JsonProperty("maxHours")
		public Builder withMaxHours(Double maxHours) {
			this.maxHours = maxHours;
			return this;
		}

		@JsonProperty("initialHours")
		public Builder withInitialHours(Double initialHours) {
			this.initialHours = initialHours;
			return this;
		}

		@JsonProperty("perAdditionalHour")
		public Builder withPerAdditionalHour(Double perAdditionalHour) {
			this.perAdditionalHour = perAdditionalHour;
			return this;
		}

		@JsonProperty("maxAdditionalHours")
		public Builder withMaxAdditionalHours(Double maxAdditionalHours) {
			this.maxAdditionalHours = maxAdditionalHours;
			return this;
		}

		@JsonProperty("perUnit")
		public Builder withPerUnit(Double perUnit) {
			this.perUnit = perUnit;
			return this;
		}

		@JsonProperty("maxUnits")
		public Builder withMaxUnits(Double maxUnits) {
			this.maxUnits = maxUnits;
			return this;
		}

		@JsonProperty("blendedPerHour")
		public Builder withBlendedPerHour(Double blendedPerHour) {
			this.blendedPerHour = blendedPerHour;
			return this;
		}

		@JsonProperty("internal")
		public Builder withInternal(Boolean internal) {
			this.internal = internal;
			return this;
		}

		@JsonProperty("bonus")
		public Builder withBonus(Double bonus) {
			this.bonus = bonus;
			return this;
		}

		@JsonProperty("reimbursement")
		public Builder withReimbursement(Double reimbursement) {
			this.reimbursement = reimbursement;
			return this;
		}

		@JsonProperty("note")
		public Builder withNote(String note) {
			this.note = note;
			return this;
		}

		public NegotiationDTO build() {
			return new NegotiationDTO(this);
		}
	}
}
