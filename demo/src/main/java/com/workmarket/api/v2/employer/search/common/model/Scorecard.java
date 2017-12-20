package com.workmarket.api.v2.employer.search.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

/**
 * Value object representing a scorecard (or set of ratings).
 */
@ApiModel("Scorecard")
public class Scorecard extends BaseDTO {
	private final Integer workCompletedCount;
	private final Integer workCancelledCount;
	private final Integer workAbandonedCount;
	private final Integer onTimePercentage;
	private final Integer deliverableOnTimePercentage;
	private final Integer satisfactionRate;

	/**
	 * Constructor.
	 * @param workCompletedCount The number of work assignments completed
	 * @param workCancelledCount The number of cancelled work assignments
	 * @param workAbandonedCount The number of abandoned assignments
	 * @param onTimePercentage The on time percentage rating
	 * @param deliverableOnTimePercentage The deliverable on time percentage rating
	 * @param satisfactionRate The satisfaction rating
	 */
	private Scorecard(@JsonProperty("workCompletedCount") final Integer workCompletedCount,
	                  @JsonProperty("workCancelledCount") final Integer workCancelledCount,
	                  @JsonProperty("workAbandonedCount") final Integer workAbandonedCount,
	                  @JsonProperty("onTimePercentage") final Integer onTimePercentage,
	                  @JsonProperty("deliverableOnTimePercentage") final Integer deliverableOnTimePercentage,
	                  @JsonProperty("satisfactionRate") final Integer satisfactionRate) {
		this.workCompletedCount = workCompletedCount;
		this.workCancelledCount = workCancelledCount;
		this.workAbandonedCount = workAbandonedCount;
		this.onTimePercentage = onTimePercentage;
		this.deliverableOnTimePercentage = deliverableOnTimePercentage;
		this.satisfactionRate = satisfactionRate;
	}

	/**
	 * Constructor.
	 * @param builder The builder to construct from
	 */
	private Scorecard(final Builder builder) {
		this.workCompletedCount = builder.workCompletedCount;
		this.workCancelledCount = builder.workCancelledCount;
		this.workAbandonedCount = builder.workAbandonedCount;
		this.onTimePercentage = builder.onTimePercentage;
		this.deliverableOnTimePercentage = builder.deliverableOnTimePercentage;
		this.satisfactionRate = builder.satisfactionRate;
	}

	/**
	 * Gets the workCompletedCount.
	 *
	 * @return java.lang.Integer The workCompletedCount
	 */
	public Integer getWorkCompletedCount() {
		return workCompletedCount;
	}

	/**
	 * Gets the workCancelledCount.
	 *
	 * @return java.lang.Integer The workCancelledCount
	 */
	public Integer getWorkCancelledCount() {
		return workCancelledCount;
	}

	/**
	 * Gets the workAbandonedCount.
	 *
	 * @return java.lang.Integer The workAbandonedCount
	 */
	public Integer getWorkAbandonedCount() {
		return workAbandonedCount;
	}

	/**
	 * Gets the onTimePercentage.
	 *
	 * @return java.lang.Integer The onTimePercentage
	 */
	public Integer getOnTimePercentage() {
		return onTimePercentage;
	}

	/**
	 * Gets the deliverableOnTimePercentage.
	 *
	 * @return java.lang.Integer The deliverableOnTimePercentage
	 */
	public Integer getDeliverableOnTimePercentage() {
		return deliverableOnTimePercentage;
	}

	/**
	 * Gets the satisfactionRate.
	 *
	 * @return java.lang.Integer The satisfactionRate
	 */
	public Integer getSatisfactionRate() {
		return satisfactionRate;
	}

	/**
	 * Builder used to construct our instances.
	 */
	public static class Builder {
		private Integer workCompletedCount;
		private Integer workCancelledCount;
		private Integer workAbandonedCount;
		private Integer onTimePercentage;
		private Integer deliverableOnTimePercentage;
		private Integer satisfactionRate;

		/**
		 * Constructor.
		 */
		public Builder() {

		}

		/**
		 * Constructor (copy) Seeds this builder from the given Scorecard.
		 * @param scorecard The scorecard to seed this instance from
		 */
		public Builder(final Scorecard scorecard) {
			this.workCompletedCount = scorecard.workCompletedCount;
			this.workCancelledCount = scorecard.workCancelledCount;
			this.workAbandonedCount = scorecard.workAbandonedCount;
			this.onTimePercentage = scorecard.onTimePercentage;
			this.deliverableOnTimePercentage = scorecard.deliverableOnTimePercentage;
			this.satisfactionRate = scorecard.satisfactionRate;
		}

		/**
		 * Sets the workCompletedCount.
		 *
		 * @param workCompletedCount The workCompletedCount to set
		 * @return Builder The builder
		 */
		public Builder setWorkCompletedCount(final Integer workCompletedCount) {
			this.workCompletedCount = workCompletedCount;
			return this;
		}

		/**
		 * Sets the workCancelledCount.
		 *
		 * @param workCancelledCount The workCancelledCount to set
		 * @return Builder The builder
		 */
		public Builder setWorkCancelledCount(final Integer workCancelledCount) {
			this.workCancelledCount = workCancelledCount;
			return this;
		}

		/**
		 * Sets the workAbandonedCount.
		 *
		 * @param workAbandonedCount The workAbandonedCount to set
		 * @return Builder The builder
		 */
		public Builder setWorkAbandonedCount(final Integer workAbandonedCount) {
			this.workAbandonedCount = workAbandonedCount;
			return this;
		}

		/**
		 * Sets the onTimePercentage.
		 *
		 * @param onTimePercentage The onTimePercentage to set
		 * @return Builder The builder
		 */
		public Builder setOnTimePercentage(final Integer onTimePercentage) {
			this.onTimePercentage = onTimePercentage;
			return this;
		}

		/**
		 * Sets the deliverableOnTimePercentage.
		 *
		 * @param deliverableOnTimePercentage The deliverableOnTimePercentage to set
		 * @return Builder The builder
		 */
		public Builder setDeliverableOnTimePercentage(final Integer deliverableOnTimePercentage) {
			this.deliverableOnTimePercentage = deliverableOnTimePercentage;
			return this;
		}

		/**
		 * Sets the satisfactionRate.
		 *
		 * @param satisfactionRate The satisfactionRate to set
		 * @return Builder The builder
		 */
		public Builder setSatisfactionRate(final Integer satisfactionRate) {
			this.satisfactionRate = satisfactionRate;
			return this;
		}

		/**
		 * Build our Scorecard.
		 * @return Scorecard The new instance
		 */
		public Scorecard build() {
			return new Scorecard(this);
		}
	}

}
