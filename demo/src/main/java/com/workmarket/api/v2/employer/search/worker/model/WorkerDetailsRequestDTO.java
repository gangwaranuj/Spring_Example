package com.workmarket.api.v2.employer.search.worker.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.employer.search.common.model.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Request object (from REST API) used to hydrate a set of workers for a given view.
 *
 */
@ApiModel("WorkDetailsRequest")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = WorkerDetailsRequestDTO.Builder.class)
public class WorkerDetailsRequestDTO extends BaseDTO {
	private final ViewType viewType;
	private final List<String> uuids;

	public WorkerDetailsRequestDTO(final Builder builder) {
		this.viewType = builder.viewType;
		this.uuids = builder.uuidsBuilder.build();
	}

	/**
	 * Gets the viewType.
	 *
	 * @return com.workmarket.api.search.v2.worker.model.ViewType The viewType
	 */

	@ApiModelProperty(name = "viewType")
	@JsonProperty("viewType")
	public ViewType getViewType() {
		return viewType;
	}

	/**
	 * Gets the user uuid.
	 *
	 * @return ImmutableList The user uuids
	 */
	@ApiModelProperty(name = "uuids")
	@JsonProperty("uuids")
	public List<String> getUuids() {
		return uuids;
	}

	/**
	 * Builder used to create instances of our WorkerDetailsRequestDTO.
	 */
	public static class Builder {
		private ViewType viewType;
		private ImmutableList.Builder<String> uuidsBuilder = new ImmutableList.Builder<>();


		/**
		 * Constructor.
		 */
		public Builder() {

		}

		/**
		 * Constructor (copy).
		 * @param workerDetailsRequestDTO The worker details request to create this instance from
		 */
		public Builder(final WorkerDetailsRequestDTO workerDetailsRequestDTO) {
			this.viewType = workerDetailsRequestDTO.viewType;
			this.uuidsBuilder.addAll(workerDetailsRequestDTO.uuids);
		}

		/**
		 * Sets the viewType.
		 *
		 * @param viewType The viewType to set
		 * @return Builder The updated builder
		 */
		@JsonProperty("viewType")
		public Builder setViewType(final ViewType viewType) {
			this.viewType = viewType;
			return this;
		}

		/**
		 * Adds the userNumbers to our set of worker numbers
		 *
		 * @param uuids The user uuids to add
		 * @return Builder The updated builder
		 */
		public Builder addUuids(final List<String> uuids) {
			uuidsBuilder.addAll(uuids);
			return this;
		}

		/**
		 * Adds a new userNumber to our set of worker numbers
		 *
		 * @param uuid The user uuid to add
		 * @return Builder The updated builder
		 */
		public Builder addUuid(final String uuid) {
			uuidsBuilder.add(uuid);
			return this;
		}

		/**
		 * Sets the userNumbers.
		 *
		 * @param uuids The uuids to set
		 * @return Builder The updated builder
		 */
		@JsonProperty("uuids")
		public Builder setUuids(final List<String> uuids) {
			this.uuidsBuilder = new ImmutableList.Builder<>();
			this.uuidsBuilder.addAll(uuids);
			return this;
		}

		/**
		 * Construct our DTO.
		 * @return WorkerDetailsRequestDTO The new dto instance
		 */
		public WorkerDetailsRequestDTO build() {
			return new WorkerDetailsRequestDTO(this);
		}
	}
}
