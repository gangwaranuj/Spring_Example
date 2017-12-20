package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Complete")
@JsonDeserialize(builder = CompleteDTO.Builder.class)
public class CompleteDTO {

	private final String resolution;
	private final Integer overrideMinutesWorked;
	private final Integer units;
	private final Double additionalExpenses;
	private final Double overridePrice;
	private final Double taxPercent;

	@ApiModelProperty(name = "resolution")
	@JsonProperty("resolution")
	public String getResolution() {
		return resolution;
	}

	@ApiModelProperty(name = "overrideMinutesWorked")
	@JsonProperty("overrideMinutesWorked")
	public Integer getOverrideMinutesWorked() {
		return overrideMinutesWorked;
	}

	@ApiModelProperty(name = "units")
	@JsonProperty("units")
	public Integer getUnits() {
		return units;
	}

	@ApiModelProperty(name = "additionalExpenses")
	@JsonProperty("additionalExpenses")
	public Double getAdditionalExpenses() {
		return additionalExpenses;
	}

	@ApiModelProperty(name = "overridePrice")
	@JsonProperty("overridePrice")
	public Double getOverridePrice() {
		return overridePrice;
	}

	@ApiModelProperty(name = "taxPercent")
	@JsonProperty("taxPercent")
	public Double getTaxPercent() {
		return taxPercent;
	}

	private CompleteDTO(Builder builder) {
		resolution = builder.resolution;
		overrideMinutesWorked = builder.overrideMinutesWorked;
		units = builder.units;
		additionalExpenses = builder.additionalExpenses;
		overridePrice = builder.overridePrice;
		taxPercent = builder.taxPercent;
	}


	public static final class Builder {
		private String resolution;
		private Integer overrideMinutesWorked;
		private Integer units;
		private Double additionalExpenses;
		private Double overridePrice;
		private Double taxPercent;

		public Builder() {
		}

		public Builder(CompleteDTO copy) {
			this.resolution = copy.resolution;
			this.overrideMinutesWorked = copy.overrideMinutesWorked;
			this.units = copy.units;
			this.additionalExpenses = copy.additionalExpenses;
			this.overridePrice = copy.overridePrice;
			this.taxPercent = copy.taxPercent;
		}

		@JsonProperty("resolution")
		public Builder withResolution(String resolution) {
			this.resolution = resolution;
			return this;
		}

		@JsonProperty("overrideMinutesWorked")
		public Builder withOverrideMinutesWorked(Integer overrideMinutesWorked) {
			this.overrideMinutesWorked = overrideMinutesWorked;
			return this;
		}

		@JsonProperty("units")
		public Builder withUnits(Integer units) {
			this.units = units;
			return this;
		}

		@JsonProperty("additionalExpenses")
		public Builder withAdditionalExpenses(Double additionalExpenses) {
			this.additionalExpenses = additionalExpenses;
			return this;
		}

		@JsonProperty("overridePrice")
		public Builder withOverridePrice(Double overridePrice) {
			this.overridePrice = overridePrice;
			return this;
		}

		@JsonProperty("taxPercent")
		public Builder withTaxPercent(Double taxPercent) {
			this.taxPercent = taxPercent;
			return this;
		}

		public CompleteDTO build() {
			return new CompleteDTO(this);
		}
	}
}
