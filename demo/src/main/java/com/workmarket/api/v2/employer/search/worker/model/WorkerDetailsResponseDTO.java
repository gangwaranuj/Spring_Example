package com.workmarket.api.v2.employer.search.worker.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.employer.search.common.model.BaseDTO;

import java.util.List;

import io.swagger.annotations.ApiModel;

/**
 * Value object holding a set of workers.
 */
@ApiModel("WorkerDetailsResponse")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = WorkerDetailsResponseDTO.Builder.class)
public class WorkerDetailsResponseDTO extends BaseDTO {

	private final ImmutableList<Worker> workers;

	private WorkerDetailsResponseDTO(final Builder builder) {
		this.workers = builder.workersBuilder.build();
	}
	/**
	 * Gets the workers.
	 *
	 * @return ImmutableList The workers
	 */
	public ImmutableList<Worker> getWorkers() {
		return workers;
	}


	/**
	 * Builder used to construct instances of our Worker.
	 */
	public static class Builder {
		private ImmutableList.Builder<Worker> workersBuilder = new ImmutableList.Builder<>();

		/**
		 * Constructor.
		 */
		public Builder() {

		}

		/**
		 * Constructor.
		 * @param workerDetailsResponseDTO The builder to build this instance from
		 */
		public Builder(final WorkerDetailsResponseDTO workerDetailsResponseDTO) {
			this.workersBuilder.addAll(workerDetailsResponseDTO.workers);
		}

		/**
		 * Add a new worker to our set.
		 * @param worker The worker we are adding
		 * @return Builder The updated builder
		 */
		public Builder addWorker(final Worker worker) {
			workersBuilder.add(worker);
			return this;
		}

		/**
		 * Add a set of new workers to our set.
		 * @param workers The workers we are adding
		 * @return Builder The updated builder
		 */
		public Builder addWorkers(final List<Worker> workers) {
			workersBuilder.addAll(workers);
			return this;
		}

		/**
		 * Sets the set of workers on our response.
		 * @param workers The set of workers on our response
		 * @return Builder The updated builder
		 */
		@JsonProperty("workers") public Builder setWorkers(final List<Worker> workers) {
			this.workersBuilder = new ImmutableList.Builder<>();
			workersBuilder.addAll(workers);
			return this;
		}

		/**
		 * Builds a new WorkerDetailsResponseDTO instance.
		 * @return WorkerDetailsResponseDTO The new instance
		 */
		public WorkerDetailsResponseDTO build() {
			return new WorkerDetailsResponseDTO(this);
		}

	}
}
