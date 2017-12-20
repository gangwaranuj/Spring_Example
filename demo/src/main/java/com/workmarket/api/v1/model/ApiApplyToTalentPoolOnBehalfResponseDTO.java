package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "ApplyToTalentPoolOnBehalfResponse")
@JsonDeserialize(builder = ApiApplyToTalentPoolOnBehalfResponseDTO.Builder.class)
public class ApiApplyToTalentPoolOnBehalfResponseDTO {

	private final List<String> success;
	private final List<String> failure;
	
	public ApiApplyToTalentPoolOnBehalfResponseDTO(List<String> success, List<String> failure) {
		this.success = success;
		this.failure = failure;
	}

	private ApiApplyToTalentPoolOnBehalfResponseDTO(Builder builder) {
		success = builder.success;
		failure = builder.failure;
	}
	
	public static final class Builder {
		private List<String> success;
		private List<String> failure;

		public Builder() {
		}

		public Builder(ApiApplyToTalentPoolOnBehalfResponseDTO copy) {
			this.success = copy == null ? null : ImmutableList.copyOf(copy.success);
			this.failure = copy == null ? null : ImmutableList.copyOf(copy.failure);
		}

		@ApiModelProperty(name = "success")
		@JsonProperty("success")
		public Builder withSuccess(List<String> success) {
			this.success = success;
			return this;
		}

		@ApiModelProperty(name = "failure")
		@JsonProperty("failure")
		public Builder withFailure(List<String> failure) {
			this.failure = failure;
			return this;
		}

		public ApiApplyToTalentPoolOnBehalfResponseDTO build() {
			return new ApiApplyToTalentPoolOnBehalfResponseDTO(this);
		}
	}

	@ApiModelProperty(name = "success")
	@JsonProperty("success")
	public List<String> getSuccess() {
		return success;
	}

	@ApiModelProperty(name = "failure")
	@JsonProperty("failure")
	public List<String> getFailure() {
		return failure;
	}
}
