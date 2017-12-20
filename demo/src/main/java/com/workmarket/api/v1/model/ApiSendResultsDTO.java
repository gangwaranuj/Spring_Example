package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

@ApiModel(value = "SendResults")
@JsonDeserialize(builder = ApiSendResultsDTO.Builder.class)
public class ApiSendResultsDTO {
	private final Map<WorkAuthorizationResponse, List<String>> mapOfRoutingResults;
	private final List<Long> invalidGroups;
	private final Long numberOfResourcesSent;
	private final Boolean successful;

	protected ApiSendResultsDTO(Builder builder) {
		mapOfRoutingResults = builder.mapOfRoutingResults;
		invalidGroups = builder.invalidGroups;
		numberOfResourcesSent = builder.numberOfResourcesSent;
		successful = builder.successful;
	}

	@ApiModelProperty(name = "resource_send_results")
	@JsonProperty("resource_send_results")
	public Map<WorkAuthorizationResponse, List<String>> getMapOfRoutingResults() {
		return mapOfRoutingResults;
	}

	@ApiModelProperty(name = "invalid_groups")
	@JsonProperty("invalid_groups")
	public List<Long> getInvalidGroups() {
		return invalidGroups;
	}

	@ApiModelProperty(name = "number_of_resources_sent")
	@JsonProperty("number_of_resources_sent")
	public Long getNumberOfResourcesSent() {
		return numberOfResourcesSent;
	}

	@ApiModelProperty(name = "successful")
	@JsonProperty("successful")
	public Boolean getSuccessful() {
		return successful;
	}

	public static class Builder {
		private Map<WorkAuthorizationResponse, List<String>> mapOfRoutingResults;
		private List<Long> invalidGroups;
		private Long numberOfResourcesSent;
		private Boolean successful;

		public Builder() {
		}

		public Builder(ApiSendResultsDTO copy) {
			this.mapOfRoutingResults = copy.mapOfRoutingResults;
			this.invalidGroups = copy.invalidGroups;
			this.numberOfResourcesSent = copy.numberOfResourcesSent;
			this.successful = copy.successful;
		}

		@JsonProperty("map_of_routing_results")
		public Builder withMapOfRoutingResults(Map<WorkAuthorizationResponse, List<String>> mapOfRoutingResults) {
			this.mapOfRoutingResults = mapOfRoutingResults;
			return this;
		}

		@JsonProperty("invalid_groups")
		public Builder withInvalidGroups(List<Long> invalidGroups) {
			this.invalidGroups = invalidGroups;
			return this;
		}

		@JsonProperty("number_of_resources_sent")
		public Builder withNumberOfResourcesSent(Long numberOfResourcesSent) {
			this.numberOfResourcesSent = numberOfResourcesSent;
			return this;
		}

		@JsonProperty("successful")
		public Builder withSuccessful(Boolean successful) {
			this.successful = successful;
			return this;
		}

		public ApiSendResultsDTO build() {
			return new ApiSendResultsDTO(this);
		}
	}
}
