package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@ApiModel(value = "CheckOut")
@JsonDeserialize(builder = CheckOutDTO.Builder.class)
public class CheckOutDTO {

	private final String noteText;

	//Coordinates
	@Min(-90)
	@Max(90)
	private final Double latitude;

	@Min(-180)
	@Max(180)
	private final Double longitude;

	private CheckOutDTO(Builder builder) {
		noteText = builder.noteText;
		latitude = builder.latitude;
		longitude = builder.longitude;
	}

	@ApiModelProperty(name = "noteText")
	@JsonProperty("noteText")
	public String getNoteText() {
		return noteText;
	}

	@ApiModelProperty(name = "latitude")
	@JsonProperty("latitude")
	public Double getLatitude() {
		return latitude;
	}

	@ApiModelProperty(name = "longitude")
	@JsonProperty("longitude")
	public Double getLongitude() {
		return longitude;
	}

	public static final class Builder {
		private String noteText;
		private Double latitude;
		private Double longitude;

		public Builder() {
		}

		public Builder(CheckOutDTO copy) {
			this.noteText = copy.noteText;
			this.latitude = copy.latitude;
			this.longitude = copy.longitude;
		}

		@JsonProperty("noteText")
		public Builder withNoteText(String noteText) {
			this.noteText = noteText;
			return this;
		}

		@JsonProperty("latitude")
		public Builder withLatitude(Double latitude) {
			this.latitude = latitude;
			return this;
		}

		@JsonProperty("longitude")
		public Builder withLongitude(Double longitude) {
			this.longitude = longitude;
			return this;
		}

		public CheckOutDTO build() {
			return new CheckOutDTO(this);
		}
	}
}
