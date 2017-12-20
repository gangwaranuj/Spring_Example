package com.workmarket.api.v2.employer.search.worker.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.search.common.model.BaseDTO;
import com.workmarket.api.v2.employer.search.common.model.Highlights;
import com.workmarket.data.solr.model.SolrUserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Value object holding the results of a worker search.
 */
@ApiModel("WorkerSearchRecord")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = WorkerSearchRecord.Builder.class)
public class WorkerSearchRecord extends BaseDTO {

	private Highlights highlights;
	private Double score;
	private String uuid;
	private SolrUserType userType;

	/**
	 * Constructor.
	 * @param builder The builder
	 */
	private WorkerSearchRecord(final Builder builder) {
		this.highlights = builder.highlights;
		this.score = builder.score;
		this.uuid = builder.uuid;
		this.userType = builder.userType;
	}

	/**
	 * Gets the user uuid.
	 *
	 * @return java.lang.String The user uuid
	 */
	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	/**
	 * Gets the highlights.
	 *
	 * @return com.workmarket.api.employer.v2.search.common.model.Highlights The highlights
	 */
	@ApiModelProperty(name = "highlights")
	@JsonProperty("highlights")
	public Highlights getHighlights() {
		return highlights;
	}

	/**
	 * Gets the score.
	 *
	 * @return java.lang.Double The score
	 */
	@ApiModelProperty(name = "score")
	@JsonProperty("score")
	public Double getScore() {
		return score;
	}

	/**
	 * Gets the userType.
	 *
	 * @return
	 */
	@ApiModelProperty(name = "userType")
	@JsonProperty("userType")
	public SolrUserType getUserType() {
		return userType;
	}

	/**
	 * Builder used to construct our instances.
	 */
	public static class Builder {
		private Highlights highlights;
		private Double score;
		private String uuid;
		private SolrUserType userType;

		/**
		 * Constructor.
		 */
		public Builder() {

		}

		/**
		 * Constructor.
		 * @param workerSearchRecord The WorkerSearchRecord to build this instance from
		 */
		public Builder(final WorkerSearchRecord workerSearchRecord) {
			this.highlights = workerSearchRecord.highlights;
			this.score = workerSearchRecord.score;
			this.uuid = workerSearchRecord.uuid;
			this.userType = workerSearchRecord.userType;
		}

		/**
		 * Sets the highlights.
		 *
		 * @param highlights The highlights to set
		 * @return Builder The updated builder
		 */
		@JsonProperty("highlights") public Builder setHighlights(final Highlights highlights) {
			this.highlights = highlights;
			return this;
		}

		/**
		 * Sets the score.
		 *
		 * @param score The score to set
		 * @return Builder The updated builder
		 */
		@JsonProperty("score") public Builder setScore(final Double score) {
			this.score = score;
			return this;
		}

		/**
		 * Sets the user uuid.
		 *
		 * @param uuid The user uuid to set
		 * @return Builder The updated builder
		 */
		@JsonProperty("uuid") public Builder setUuid(final String uuid) {
			this.uuid = uuid;
			return this;
		}

		/**
		 * Sets the user type.
		 *
		 * @param userType the user type
		 * @return Builder The updated builder
		 */
		@JsonProperty("userType") public Builder setUserType(final SolrUserType userType) {
			this.userType = userType;
			return this;
		}

		/**
		 * Construct the new instance.
		 * @return WorkerSearchRecord The new instance
		 */
		public WorkerSearchRecord build() {
			return new WorkerSearchRecord(this);
		}
	}

}
