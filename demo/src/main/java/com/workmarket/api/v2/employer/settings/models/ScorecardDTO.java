package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Scorecard")
@JsonDeserialize(builder = ScorecardDTO.Builder.class)
public class ScorecardDTO {
	private final int workCompletedCount;
	private final int workCancelledCount;
	private final int workAbandonedCount;
	private final int onTimePercentage;
	private final int deliverableOnTimePercentage;
	private final int satisfactionRate;

	private ScorecardDTO(Builder builder) {
		this.workCompletedCount = builder.workCompletedCount;
		this.workCancelledCount = builder.workCancelledCount;
		this.workAbandonedCount = builder.workAbandonedCount;
		this.onTimePercentage = builder.onTimePercentage;
		this.deliverableOnTimePercentage = builder.deliverableOnTimePercentage;
		this.satisfactionRate = builder.satisfactionRate;
	}

	@ApiModelProperty(name = "workCompletedCount")
	@JsonProperty("workCompletedCount")
	public int getWorkCompletedCount() {
		return workCompletedCount;
	}

	@ApiModelProperty(name = "workCancelledCount")
	@JsonProperty("workCancelledCount")
	public int getWorkCancelledCount() {
		return workCancelledCount;
	}

	@ApiModelProperty(name = "workAbandonedCount")
	@JsonProperty("workAbandonedCount")
	public int getWorkAbandonedCount() {
		return workAbandonedCount;
	}

	@ApiModelProperty(name = "onTimePercentage")
	@JsonProperty("onTimePercentage")
	public int getOnTimePercentage() {
		return onTimePercentage;
	}

	@ApiModelProperty(name = "deliverableOnTimePercentage")
	@JsonProperty("deliverableOnTimePercentage")
	public int getDeliverableOnTimePercentage() {
		return deliverableOnTimePercentage;
	}

	@ApiModelProperty(name = "satisfactionRate")
	@JsonProperty("satisfactionRate")
	public int getSatisfactionRate() {
		return satisfactionRate;
	}

	public static class Builder implements AbstractBuilder<ScorecardDTO> {
		private int workCompletedCount;
		private int workCancelledCount;
		private int workAbandonedCount;
		private int onTimePercentage;
		private int deliverableOnTimePercentage;
		private int satisfactionRate;

		public Builder(ScorecardDTO scorecardDTO) {
			this.workCompletedCount = scorecardDTO.workCompletedCount;
			this.workCancelledCount = scorecardDTO.workCancelledCount;
			this.workAbandonedCount = scorecardDTO.workAbandonedCount;
			this.onTimePercentage = scorecardDTO.onTimePercentage;
			this.deliverableOnTimePercentage = scorecardDTO.deliverableOnTimePercentage;
			this.satisfactionRate = scorecardDTO.satisfactionRate;
		}

		public Builder() {}

		@JsonProperty("workCompletedCount") public Builder setWorkCompletedCount(int workCompletedCount) {
			this.workCompletedCount = workCompletedCount;
			return this;
		}

		@JsonProperty("workCancelledCount") public Builder setWorkCancelledCount(int workCancelledCount) {
			this.workCancelledCount = workCancelledCount;
			return this;
		}

		@JsonProperty("workAbandonedCount") public Builder setWorkAbandonedCount(int workAbandonedCount) {
			this.workAbandonedCount = workAbandonedCount;
			return this;
		}

		@JsonProperty("onTimePercentage") public Builder setOnTimePercentage(int onTimePercentage) {
			this.onTimePercentage = onTimePercentage;
			return this;
		}

		@JsonProperty("deliverableOnTimePercentage") public Builder setDeliverableOnTimePercentage(int deliverableOnTimePercentage) {
			this.deliverableOnTimePercentage = deliverableOnTimePercentage;
			return this;
		}

		@JsonProperty("satisfactionRate") public Builder setSatisfactionRate(int satisfactionRate) {
			this.satisfactionRate = satisfactionRate;
			return this;
		}

		@Override
		public ScorecardDTO build() {
			return new ScorecardDTO(this);
		}
	}
}
