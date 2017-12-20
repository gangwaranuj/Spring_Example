package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "AddProjectResponse")
@JsonDeserialize(builder = ApiAddProjectResponseDTO.Builder.class)
public class ApiAddProjectResponseDTO {
	private final Boolean successful;
	private final Long id;

	private ApiAddProjectResponseDTO(Builder builder) {
		successful = builder.successful;
		id = builder.id;
	}

	@ApiModelProperty(name = "successful")
	@JsonProperty("successful")
	public Boolean getSuccessful() {
		return successful;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}


	public static final class Builder {
		private Boolean successful;
		private Long id;

		public Builder() {
		}

		public Builder(ApiAddProjectResponseDTO copy) {
			this.successful = copy.successful;
			this.id = copy.id;
		}

		@JsonProperty("successful")
		public Builder withSuccessful(Boolean successful) {
			this.successful = successful;
			return this;
		}

		@JsonProperty("id")
		public Builder withId(Long id) {
			this.id = id;
			return this;
		}

		public ApiAddProjectResponseDTO build() {
			return new ApiAddProjectResponseDTO(this);
		}
	}
}
