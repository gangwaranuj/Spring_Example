package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Authorization")
@JsonDeserialize(builder = ApiAuthorizationDTO.Builder.class)
public class ApiAuthorizationDTO {
	private final String accessToken;

	private ApiAuthorizationDTO(Builder builder) {
		accessToken = builder.accessToken;
	}

	@ApiModelProperty(name = "access_token")
	@JsonProperty("access_token")
	public String getAccessToken() {
		return accessToken;
	}

	public static final class Builder {
		private String accessToken;

		public Builder() {
		}

		public Builder(ApiAuthorizationDTO copy) {
			this.accessToken = copy.accessToken;
		}

		@JsonProperty("access_token")
		public Builder withAccessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}

		public ApiAuthorizationDTO build() {
			return new ApiAuthorizationDTO(this);
		}
	}
}
