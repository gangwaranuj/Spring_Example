package com.workmarket.api.v2.employer.search.worker.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.search.common.model.BaseDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * Value object holding our filter response details.
 */
@ApiModel("WorkerFilterResponse")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = WorkerFiltersResponseDTO.Builder.class)
public class WorkerFiltersResponseDTO extends BaseDTO {

	private final Long resultCount;
	private final Long queryTimeMillis;
	private final WorkerFilters filters;

	/**
	 * Constructor.
	 * @param builder The builder
	 */
	private WorkerFiltersResponseDTO(final Builder builder) {
		this.resultCount = builder.resultCount;
		this.queryTimeMillis = builder.queryTimeMillis;
		this.filters = builder.filters;
	}

	/**
	 * Gets the resultCount.
	 *
	 * @return java.lang.Long The resultCount
	 */
	@ApiModelProperty(name = "resultCount")
	@JsonProperty("resultCount")
	public Long getResultCount() {
		return resultCount;
	}

	/**
	 * Gets the queryTimeMillis.
	 *
	 * @return java.lang.Long The queryTimeMillis
	 */
	@ApiModelProperty(name = "queryTimeMillis")
	@JsonProperty("queryTimeMillis")
	public Long getQueryTimeMillis() {
		return queryTimeMillis;
	}

	/**
	 * Gets the filters.
	 *
	 * @return WorkerFilters The filters
	 */
	@ApiModelProperty(name = "filters")
	@JsonProperty("filters")
	public WorkerFilters getFilters() {
		return filters;
	}

	/**
	 * Our builder used to create new instances.
	 */
	public static class Builder {
		private Long resultCount;
		private Long queryTimeMillis;
		private WorkerFilters filters;

		/**
		 * Constructor.
		 */
		public Builder() {

		}

		/**
		 * Constructor.
		 * @param workerFiltersResponseDTO The WorkerFiltersResponseDTO to create this instance from
		 */
		public Builder(final WorkerFiltersResponseDTO workerFiltersResponseDTO) {
			this.resultCount = workerFiltersResponseDTO.resultCount;
			this.queryTimeMillis = workerFiltersResponseDTO.queryTimeMillis;
			this.filters = workerFiltersResponseDTO.filters;
		}

		/**
		 * Sets the resultCount.
		 *
		 * @param resultCount The resultCount to set
		 * @return Builder The builder
		 */
		@JsonProperty("resultCount") public Builder setResultCount(final Long resultCount) {
			this.resultCount = resultCount;
			return this;
		}

		/**
		 * Sets the queryTimeMillis.
		 *
		 * @param queryTimeMillis The queryTimeMillis to set
		 * @return Builder The builder
		 */
		@JsonProperty("queryTimeMillis") public Builder setQueryTimeMillis(final Long queryTimeMillis) {
			this.queryTimeMillis = queryTimeMillis;
			return this;
		}

		/**
		 * Sets the filter.
		 * @param filters The filters
		 * @return Builder The builder
		 */
		@JsonProperty("filters") public Builder setFilters(final WorkerFilters filters) {
			this.filters = filters;
			return this;
		}

		/**
		 * Builds our new dto instance.
		 * @return WorkerFiltersResponseDTO The new dto
		 */
		public WorkerFiltersResponseDTO build() {
			return new WorkerFiltersResponseDTO(this);
		}

	}

}
