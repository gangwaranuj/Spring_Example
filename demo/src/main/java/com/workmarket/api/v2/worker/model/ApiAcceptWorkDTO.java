package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by joshlevine on 2/2/17.
 */
@ApiModel("AcceptWork")
@JsonDeserialize(builder = ApiAcceptWorkDTO.Builder.class)
public class ApiAcceptWorkDTO {
	private boolean successful;

	private ApiAcceptWorkDTO(Builder builder) {
		this.successful = builder.successful;
	}

	@ApiModelProperty("successful")
	@JsonProperty("successful")
	public boolean isSuccessful() {
		return successful;
	}


	public static class Builder {
		private boolean successful;

		public Builder() {
		}

		public Builder(AcceptWorkResponse acceptWorkResponse) {
			this.successful = acceptWorkResponse.isSuccessful();
		}

		@JsonProperty("successful")
		public Builder successful(boolean successful) {
			this.successful = successful;
			return this;
		}

		public Builder fromPrototype(ApiAcceptWorkDTO prototype) {
			successful = prototype.successful;
			return this;
		}

		public ApiAcceptWorkDTO build() {
			return new ApiAcceptWorkDTO(this);
		}
	}
}
