package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(Include.NON_NULL)
@ApiModel(value = "AssignmentCreationResponse")
@JsonDeserialize(builder = ApiAssignmentCreationResponseDTO.Builder.class)
public class ApiAssignmentCreationResponseDTO extends ApiSendResultsDTO {
	private final String id;
	private final String requestURL;

	private ApiAssignmentCreationResponseDTO(Builder builder) {
		super(builder);
		id = builder.id;
		requestURL = builder.requestURL;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@ApiModelProperty(name = "requestURL")
	@JsonProperty("requestURL")
	public String getRequestURL() {
		return requestURL;
	}

	public static final class Builder extends ApiSendResultsDTO.Builder {
		private String id;
		private String requestURL;

		public Builder() {
		}

		public Builder(ApiAssignmentCreationResponseDTO copy) {
			this.id = copy.id;
			this.requestURL = copy.requestURL;
		}

		@JsonProperty("id")
		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		@JsonProperty("requestURL")
		public Builder withRequestURL(String requestURL) {
			this.requestURL = requestURL;
			return this;
		}

		public ApiAssignmentCreationResponseDTO build() {
			return new ApiAssignmentCreationResponseDTO(this);
		}
	}
}
