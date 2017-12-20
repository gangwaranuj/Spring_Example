package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.search.worker.model.Worker;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO for company workers.
 */
@ApiModel("CompanyWorkers")
@JsonDeserialize(builder = CompanyWorkersDTO.Builder.class)
public class CompanyWorkersDTO {
    private final List<Worker> workers;

    public CompanyWorkersDTO(final Builder builder) {
        this.workers = builder.workers;
    }

		@ApiModelProperty(name = "workers")
		@JsonProperty("workers")
    public List<Worker> getWorkers() {
        return workers;
    }

    public static class Builder {
        private List<Worker> workers;

        public Builder() {}

        public Builder(final CompanyWorkersDTO companyWorkersDTO) {
            this.workers = companyWorkersDTO.getWorkers();
        }

        @JsonProperty("workers") public Builder setWorkers(final List<Worker> workers) {
            this.workers = workers;
            return this;
        }

        public Builder addToWorkers(final Worker worker) {
            this.workers.add(worker);
            return this;
        }

        public CompanyWorkersDTO build() {
            return new CompanyWorkersDTO(this);
        }
    }
}
