package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModelProperty;

@JsonDeserialize(builder = StatusDTO.Builder.class)
public class StatusDTO {
	private final String step;
	private final String remaining;

	private StatusDTO(Builder builder) {
		this.step = builder.step;
		this.remaining = builder.remaining;
	}

	@ApiModelProperty(name = "step")
	@JsonProperty("step")
	public String getStep() {
		return step;
	}

	@ApiModelProperty(name = "remaining")
	@JsonProperty("remaining")
	public String getRemaining() {
		return remaining;
	}

	public static class Builder implements AbstractBuilder<StatusDTO> {
		private String step;
		private String remaining;

		public Builder(StatusDTO statusDTO) {
			this.step = statusDTO.step;
			this.remaining = statusDTO.remaining;
		}

		public Builder() {}

		@JsonProperty("step")
		public Builder setStep(String step) {
			this.step = step;
			return this;
		}

		@JsonProperty("remaining")
		public Builder setRemaining(String remaining) {
			this.remaining = remaining;
			return this;
		}

		@Override
		public StatusDTO build() {
			return new StatusDTO(this);
		}
	}

	@Override
	public String toString() {
		return "StatusDTO{" +
			"step='" + step + '\'' +
			", remaining='" + remaining + '\'' +
			'}';
	}
}
