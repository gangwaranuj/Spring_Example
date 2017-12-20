package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("AddMessage")
@JsonDeserialize(builder = AddMessageDTO.Builder.class)
public class AddMessageDTO {

	@NotNull
	private final String message;

	private AddMessageDTO(Builder builder) {
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

		public Builder(AddMessageDTO copy) {
			this.message = copy.message;
		}

		@JsonProperty("message")
		public Builder withMessage(String message) {
			this.message = message;
			return this;
		}

		public AddMessageDTO build() {
			return new AddMessageDTO(this);
		}
	}
}

