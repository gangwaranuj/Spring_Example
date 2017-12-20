package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("AddLabel")
@JsonDeserialize(builder = AddLabelDTO.Builder.class)
public class AddLabelDTO {

	private final String message;
	private final RescheduleDTO schedule;

	private AddLabelDTO(Builder builder) {
		message = builder.message;
		schedule = builder.schedule;
	}

	@ApiModelProperty(name = "message")
	@JsonProperty("message")
	public String getMessage() {
		return message;
	}

	@ApiModelProperty(name = "schedule")
	@JsonProperty("schedule")
	public RescheduleDTO getSchedule() {
		return schedule;
	}

	public static final class Builder {
		private String message;
		private RescheduleDTO schedule;

		public Builder() {
		}

		public Builder(AddLabelDTO copy) {
			this.message = copy.message;
			this.schedule = copy.schedule;
		}

		@JsonProperty("message")
		public Builder withMessage(String message) {
			this.message = message;
			return this;
		}

		@JsonProperty("schedule")
		public Builder withSchedule(RescheduleDTO schedule) {
			this.schedule = schedule;
			return this;
		}

		public AddLabelDTO build() {
			return new AddLabelDTO(this);
		}
	}
}
