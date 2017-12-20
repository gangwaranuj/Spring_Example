package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Reschedule")
@JsonDeserialize(builder = RescheduleDTO.Builder.class)
public class RescheduleDTO {

	private final Long start;
	private final Long startWindowBegin;
	private final Long startWindowEnd;
	private final Long end;
	private final String note;

	private RescheduleDTO(Builder builder) {
		start = builder.start;
		startWindowBegin = builder.startWindowBegin;
		startWindowEnd = builder.startWindowEnd;
		end = builder.end;
		note = builder.note;
	}

	@ApiModelProperty(name = "start")
	@JsonProperty("start")
	public Long getStart() {
		return start;
	}

	@ApiModelProperty(name = "startWindowBegin")
	@JsonProperty("startWindowBegin")
	public Long getStartWindowBegin() {
		return startWindowBegin;
	}

	@ApiModelProperty(name = "startWindowEnd")
	@JsonProperty("startWindowEnd")
	public Long getStartWindowEnd() {
		return startWindowEnd;
	}

	@ApiModelProperty(name = "end")
	@JsonProperty("end")
	public Long getEnd() {
		return end;
	}

	@ApiModelProperty(name = "note")
	@JsonProperty("note")
	public String getNote() {
		return note;
	}

	public static final class Builder {
		private Long start;
		private Long startWindowBegin;
		private Long startWindowEnd;
		private Long end;
		private String note;

		public Builder() {
		}

		public Builder(RescheduleDTO copy) {
			this.start = copy.start;
			this.startWindowBegin = copy.startWindowBegin;
			this.startWindowEnd = copy.startWindowEnd;
			this.end = copy.end;
			this.note = copy.note;
		}

		@JsonProperty("start")
		public Builder withStart(Long start) {
			this.start = start;
			return this;
		}

		@JsonProperty("startWindowBegin")
		public Builder withStartWindowBegin(Long startWindowBegin) {
			this.startWindowBegin = startWindowBegin;
			return this;
		}

		@JsonProperty("startWindowEnd")
		public Builder withStartWindowEnd(Long startWindowEnd) {
			this.startWindowEnd = startWindowEnd;
			return this;
		}

		@JsonProperty("end")
		public Builder withEnd(Long end) {
			this.end = end;
			return this;
		}

		@JsonProperty("note")
		public Builder withNote(String note) {
			this.note = note;
			return this;
		}

		public RescheduleDTO build() {
			return new RescheduleDTO(this);
		}
	}
}
