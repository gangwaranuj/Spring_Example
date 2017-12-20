package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "RescheduleRequest")
@JsonDeserialize(builder = ApiRescheduleRequestDTO.Builder.class)
public class ApiRescheduleRequestDTO {

	private final String note;
	private final Boolean isRequestedByResource;
	private final Long requestedOn;
	private final Long requestWindowStart;
	private final Long requestWindowEnd;
	private final Long requestScheduledTime;

	private ApiRescheduleRequestDTO(Builder builder) {
		note = builder.note;
		isRequestedByResource = builder.isRequestedByResource;
		requestedOn = builder.requestedOn;
		requestWindowStart = builder.requestWindowStart;
		requestWindowEnd = builder.requestWindowEnd;
		requestScheduledTime = builder.requestScheduledTime;
	}

	@ApiModelProperty(name = "note")
	@JsonProperty("note")
	public String getNote() {
		return note;
	}

	@ApiModelProperty(name = "requested_by_resource")
	@JsonProperty("requested_by_resource")
	public Boolean getRequestedByResource() {
		return isRequestedByResource;
	}

	@ApiModelProperty(name = "requested_on")
	@JsonProperty("requested_on")
	public Long getRequestedOn() {
		return requestedOn;
	}

	@ApiModelProperty(name = "request_window_start")
	@JsonProperty("request_window_start")
	public Long getRequestWindowStart() {
		return requestWindowStart;
	}

	@ApiModelProperty(name = "request_window_end")
	@JsonProperty("request_window_end")
	public Long getRequestWindowEnd() {
		return requestWindowEnd;
	}

	@ApiModelProperty(name = "request_scheduled_time")
	@JsonProperty("request_scheduled_time")
	public Long getRequestScheduledTime() {
		return requestScheduledTime;
	}

	public static final class Builder {
		private String note;
		private Boolean isRequestedByResource;
		private Long requestedOn;
		private Long requestWindowStart;
		private Long requestWindowEnd;
		private Long requestScheduledTime;

		public Builder() {
		}

		public Builder(ApiRescheduleRequestDTO copy) {
			this.note = copy.note;
			this.isRequestedByResource = copy.isRequestedByResource;
			this.requestedOn = copy.requestedOn;
			this.requestWindowStart = copy.requestWindowStart;
			this.requestWindowEnd = copy.requestWindowEnd;
			this.requestScheduledTime = copy.requestScheduledTime;
		}

		@JsonProperty("note")
		public Builder withNote(String note) {
			this.note = note;
			return this;
		}

		@JsonProperty("is_requested_by_resource")
		public Builder withIsRequestedByResource(Boolean isRequestedByResource) {
			this.isRequestedByResource = isRequestedByResource;
			return this;
		}

		@JsonProperty("requested_on")
		public Builder withRequestedOn(Long requestedOn) {
			this.requestedOn = requestedOn;
			return this;
		}

		@JsonProperty("request_window_start")
		public Builder withRequestWindowStart(Long requestWindowStart) {
			this.requestWindowStart = requestWindowStart;
			return this;
		}

		@JsonProperty("request_window_end")
		public Builder withRequestWindowEnd(Long requestWindowEnd) {
			this.requestWindowEnd = requestWindowEnd;
			return this;
		}

		@JsonProperty("request_scheduled_time")
		public Builder withRequestScheduledTime(Long requestScheduledTime) {
			this.requestScheduledTime = requestScheduledTime;
			return this;
		}

		public ApiRescheduleRequestDTO build() {
			return new ApiRescheduleRequestDTO(this);
		}
	}
}
