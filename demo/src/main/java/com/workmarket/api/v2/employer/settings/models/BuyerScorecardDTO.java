package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("BuyerScorecard")
@JsonDeserialize(builder = BuyerScorecardDTO.Builder.class)
public class BuyerScorecardDTO {
	private final Integer paidWorkCount;
	private final Double avgTimeToApproveWorkInDays;
	private final Double avgTimeToPayWorkInDays;
	private final Double satisfactionRate;

	private BuyerScorecardDTO(Builder builder) {
		this.paidWorkCount = builder.paidWorkCount;
		this.avgTimeToApproveWorkInDays = builder.avgTimeToApproveWorkInDays;
		this.avgTimeToPayWorkInDays = builder.avgTimeToPayWorkInDays;
		this.satisfactionRate= builder.satisfactionRate;
	}

	@ApiModelProperty(name = "paidWorkCount")
	@JsonProperty("paidWorkCount")
	public Integer getPaidWorkCount() {
		return paidWorkCount;
	}

	@ApiModelProperty(name = "avgTimeToApproveWorkInDays")
	@JsonProperty("avgTimeToApproveWorkInDays")
	public Double getAvgTimeToApproveWorkInDays() {
		return avgTimeToApproveWorkInDays;
	}

	@ApiModelProperty(name = "avgTimeToPayWorkInDays")
	@JsonProperty("avgTimeToPayWorkInDays")
	public Double getAvgTimeToPayWorkInDays() {
		return avgTimeToPayWorkInDays;
	}

	@ApiModelProperty(name = "satisfactionRate")
	@JsonProperty("satisfactionRate")
	public Double getSatisfactionRate() {
		return satisfactionRate;
	}

	public static class Builder implements AbstractBuilder<BuyerScorecardDTO> {
		private Integer paidWorkCount;
		private Double avgTimeToApproveWorkInDays;
		private Double avgTimeToPayWorkInDays;
		private Double satisfactionRate;

		public Builder(BuyerScorecardDTO scorecardDTO) {
			this.paidWorkCount = scorecardDTO.paidWorkCount;
			this.avgTimeToApproveWorkInDays = scorecardDTO.avgTimeToApproveWorkInDays;
			this.avgTimeToPayWorkInDays = scorecardDTO.avgTimeToPayWorkInDays;
			this.satisfactionRate = scorecardDTO.satisfactionRate;
		}

		public Builder() {}

		@JsonProperty("paidWorkCount") public Builder setPaidWorkCount(Integer paidWorkCount) {
			this.paidWorkCount = paidWorkCount;
			return this;
		}

		@JsonProperty("avgTimeToApproveWorkInDays") public Builder setAvgTimeToApproveWorkInDays(Double avgTimeToApproveWorkInDays) {
			this.avgTimeToApproveWorkInDays = avgTimeToApproveWorkInDays;
			return this;
		}

		@JsonProperty("avgTimeToPayWorkInDays") public Builder setAvgTimeToPayWorkInDays(Double avgTimeToPayWorkInDays) {
			this.avgTimeToPayWorkInDays = avgTimeToPayWorkInDays;
			return this;
		}

		@JsonProperty("satisfactionRate") public Builder setSatisfactionRate(Double satisfactionRate) {
			this.satisfactionRate = satisfactionRate;
			return this;
		}

		@Override
		public BuyerScorecardDTO build() {
			return new BuyerScorecardDTO(this);
		}
	}
}
