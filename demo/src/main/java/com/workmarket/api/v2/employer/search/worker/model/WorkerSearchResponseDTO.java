package com.workmarket.api.v2.employer.search.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.employer.search.common.model.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("WorkerSearchResponse")
@JsonDeserialize(builder = WorkerSearchResponseDTO.Builder.class)
public class WorkerSearchResponseDTO extends BaseDTO {
	private final Long offset;
	private final Long size;
	private final Long resultCount;
	private final Long queryTimeMillis;
	private final List<WorkerSearchRecord> searchResults;

	/**
	 * Constructor.
	 * @param builder The builder used to create this instance.
	 */
	private WorkerSearchResponseDTO(final Builder builder) {
		this.offset = builder.offset;
		this.size = builder.size;
		this.resultCount = builder.resultCount;
		this.queryTimeMillis = builder.queryTimeMillis;
		this.searchResults = builder.searchResultsBuilder.build();
	}

	/**
	 * Gets the offset.
	 *
	 * @return java.lang.Long The offset
	 */
	@ApiModelProperty(name = "offset")
	@JsonProperty("offset")
	public Long getOffset() {
		return offset;
	}

	/**
	 * Gets the size.
	 *
	 * @return java.lang.Long The size
	 */
	@ApiModelProperty(name = "size")
	@JsonProperty("size")
	public Long getSize() {
		return size;
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
	 * Gets the searchResults.
	 *
	 * @return java.util.List<com.workmarket.api.v2.employer.search.worker.model.WorkerSearchRecord> The searchResults
	 */
	@ApiModelProperty(name = "searchResults")
	@JsonProperty("searchResults")
	public List<WorkerSearchRecord> getSearchResults() {
		return searchResults;
	}

	/**
	 * Builder class used to construct instances of our WorkerSearchResponseDTO.
	 */
	public static class Builder {
		private Long offset;
		private Long size;
		private Long resultCount;
		private Long queryTimeMillis;
		private ImmutableList.Builder<WorkerSearchRecord> searchResultsBuilder = new ImmutableList.Builder<>();

		/**
		 * Constructor.
		 */
		public Builder() {

		}

		/**
		 * Constructor.
		 * @param workerSearchResponseDTO The WorkerSearchResponseDTO to build from
		 */
		public Builder(final WorkerSearchResponseDTO workerSearchResponseDTO) {
			this.offset = workerSearchResponseDTO.offset;
			this.size = workerSearchResponseDTO.size;
			this.resultCount = workerSearchResponseDTO.resultCount;
			this.queryTimeMillis = workerSearchResponseDTO.queryTimeMillis;
			this.searchResultsBuilder.addAll(workerSearchResponseDTO.searchResults);
		}

		/**
		 * Sets the offset.
		 *
		 * @param offset The offset to set
		 * @return Builder The updated builder
		 */
		@JsonProperty("offset") public Builder setOffset(final Long offset) {
			this.offset = offset;
			return this;
		}

		/**
		 * Sets the size.
		 *
		 * @param size The size to set
		 * @return Builder The updated builder
		 */
		@JsonProperty("size") public Builder setSize(final Long size) {
			this.size = size;
			return this;
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
		 * Adds a new search result.
		 * @param searchResult The result to add
		 * @return Builder The builder
		 */
		public Builder addSearchResult(final WorkerSearchRecord searchResult) {
			this.searchResultsBuilder.add(searchResult);
			return this;
		}

		/**
		 * Adds a set of search result.
		 * @param searchResults The results to add
		 * @return Builder The builder
		 */
		public Builder addSearchResult(final List<WorkerSearchRecord> searchResults) {
			this.searchResultsBuilder.addAll(searchResults);
			return this;
		}

		/**
		 * Sets our search results to the given set.
		 * @param searchResults The set of search results
		 * @return Builder The builder
		 */
		@JsonProperty("searchResults") public Builder setSearchResults(final List<WorkerSearchRecord> searchResults) {
			this.searchResultsBuilder = new ImmutableList.Builder<>();
			this.searchResultsBuilder.addAll(searchResults);
			return this;
		}

		/**
		 * Construct our instance.
		 * @return WorkerSearchResponseDTO The response created by this builder
		 */
		public WorkerSearchResponseDTO build() {
			return new WorkerSearchResponseDTO(this);
		}
	}

}
