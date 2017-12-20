package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "BonusRequest")
@JsonDeserialize(builder = ApiBonusRequestDTO.Builder.class)
public class ApiBonusRequestDTO {

	private final Double bonus;
	private final String note;
	private final Long requestedOn;

	private ApiBonusRequestDTO(Builder builder) {
		bonus = builder.bonus;
		note = builder.note;
		requestedOn = builder.requestedOn;
	}

	@ApiModelProperty(name = "bonus")
	@JsonProperty("bonus")
	public Double getBonus() {
		return bonus;
	}

	@ApiModelProperty(name = "note")
	@JsonProperty("note")
	public String getNote() {
		return note;
	}

	@ApiModelProperty(name = "requested_on")
	@JsonProperty("requested_on")
	public Long getRequestedOn() {
		return requestedOn;
	}

	public static final class Builder {
		private Double bonus;
		private String note;
		private Long requestedOn;

		public Builder() {
		}

		public Builder(ApiBonusRequestDTO copy) {
			this.bonus = copy.bonus;
			this.note = copy.note;
			this.requestedOn = copy.requestedOn;
		}

		@JsonProperty("bonus")
		public Builder withBonus(Double bonus) {
			this.bonus = bonus;
			return this;
		}

		@JsonProperty("note")
		public Builder withNote(String note) {
			this.note = note;
			return this;
		}

		@JsonProperty("requested_on")
		public Builder withRequestedOn(Long requestedOn) {
			this.requestedOn = requestedOn;
			return this;
		}

		public ApiBonusRequestDTO build() {
			return new ApiBonusRequestDTO(this);
		}
	}
}
