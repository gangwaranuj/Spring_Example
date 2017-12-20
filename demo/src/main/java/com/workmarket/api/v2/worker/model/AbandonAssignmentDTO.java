package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by michaelrothbaum on 7/26/17.
 */

@ApiModel("AbandonAssignment")
@JsonDeserialize(builder = AbandonAssignmentDTO.Builder.class)
public class AbandonAssignmentDTO {

	private final String message;

	private AbandonAssignmentDTO(Builder builder) {
		message = builder.message;
	}

	@ApiModelProperty(name = "message")
	@JsonProperty("message")
	public String getMessage() {
		return message;
	}

	public static final class Builder {
		private String message;

		public Builder() {
		}

		public Builder(AbandonAssignmentDTO copy) {
			this.message = copy.message;
		}

		@JsonProperty("message")
		public Builder withMessage(String message) {
			this.message = message;
			return this;
		}

		public AbandonAssignmentDTO build() {
			return new AbandonAssignmentDTO(this);
		}
	}
}
