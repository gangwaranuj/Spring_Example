package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@ApiModel(value = "CheckIn")
@JsonDeserialize(builder = CheckInDTO.Builder.class)
public class CheckInDTO {

	@Min(-90)
	@Max(90)
	private final Double latitude;

	@Min(-180)
	@Max(180)
	private final Double longitude;

	private CheckInDTO(Builder builder) {
		latitude = builder.latitude;
		longitude = builder.longitude;
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

	public String toString() {
		return latitude + "][" + longitude;
	}

	public static final class Builder {
		private Double latitude;
		private Double longitude;

		public Builder() {
		}

		public Builder(CheckInDTO copy) {
			this.latitude = copy.latitude;
			this.longitude = copy.longitude;
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

		public CheckInDTO build() {
			return new CheckInDTO(this);
		}
	}
}
