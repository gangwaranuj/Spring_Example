package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by joshlevine on 2/2/17.
 */
@ApiModel("DeclineWork")
@JsonDeserialize(builder = ApiDeclineWorkDTO.Builder.class)
public class ApiDeclineWorkDTO {
	private boolean successful;

	private ApiDeclineWorkDTO(Builder builder) {
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

		public Builder(boolean successful) {
			this.successful = successful;
		}

		@JsonProperty("successful")
		public Builder successful(boolean successful) {
			this.successful = successful;
			return this;
		}

		public Builder fromPrototype(ApiDeclineWorkDTO prototype) {
			successful = prototype.successful;
			return this;
		}

		public ApiDeclineWorkDTO build() {
			return new ApiDeclineWorkDTO(this);
		}
	}
}
