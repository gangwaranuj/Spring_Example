package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by ianha on 4/13/15.
 */
@ApiModel("Scorecard")
@JsonDeserialize(builder = ScoreCard.Builder.class)
public class ScoreCard {
	private final short rating;
	private final double onTimePercentage;
	private final Integer workCancelledCount;
	private final Integer workAbandonedCount;
	private final double deliverableOnTimePercentage;

	private ScoreCard(Builder builder) {
		rating = builder.rating;
		onTimePercentage = builder.onTimePercentage;
		workCancelledCount = builder.workCancelledCount;
		workAbandonedCount = builder.workAbandonedCount;
		deliverableOnTimePercentage = builder.deliverableOnTimePercentage;
	}


	public static final class Builder {
		private short rating;
		private double onTimePercentage;
		private Integer workCancelledCount = 0;
		private Integer workAbandonedCount = 0;
		private double deliverableOnTimePercentage;

		@ApiModelProperty(name = "rating")
		@JsonProperty("rating")
		public short getRating() {
			return rating;
		}

		@ApiModelProperty(name = "onTimePercentage")
		@JsonProperty("onTimePercentage")
		public double getOnTimePercentage() {
			return onTimePercentage;
		}

		@ApiModelProperty(name = "workCancelledCount")
		@JsonProperty("workCancelledCount")
		public Integer getWorkCancelledCount() {
			return workCancelledCount;
		}

		@ApiModelProperty(name = "workAbandonedCount")
		@JsonProperty("workAbandonedCount")
		public Integer getWorkAbandonedCount() {
			return workAbandonedCount;
		}

		@ApiModelProperty(name = "deliverableOnTimePercentage")
		@JsonProperty("deliverableOnTimePercentage")
		public double getDeliverableOnTimePercentage() {
			return deliverableOnTimePercentage;
		}

		public Builder() {
		}

		public Builder(ScoreCard copy) {
			this.rating = copy.rating;
			this.onTimePercentage = copy.onTimePercentage;
			this.workCancelledCount = copy.workCancelledCount;
			this.workAbandonedCount = copy.workAbandonedCount;
			this.deliverableOnTimePercentage = copy.deliverableOnTimePercentage;
		}

		@JsonProperty("rating") public Builder withRating(short rating) {
			this.rating = rating;
			return this;
		}

		@JsonProperty("onTimePercentage") public Builder withOnTimePercentage(double onTimePercentage) {
			this.onTimePercentage = onTimePercentage;
			return this;
		}

		@JsonProperty("workCancelledCount") public Builder withWorkCancelledCount(Integer workCancelledCount) {
			this.workCancelledCount = workCancelledCount;
			return this;
		}

		@JsonProperty("workAbandonedCount") public Builder withWorkAbandonedCount(Integer workAbandonedCount) {
			this.workAbandonedCount = workAbandonedCount;
			return this;
		}

		@JsonProperty("deliverableOnTimePercentage") public Builder withDeliverableOnTimePercentage(double deliverableOnTimePercentage) {
			this.deliverableOnTimePercentage = deliverableOnTimePercentage;
			return this;
		}

		public ScoreCard build() {
			return new ScoreCard(this);
		}
	}
}
