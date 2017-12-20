package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by ianha on 4/10/15.
 */
@ApiModel("GeoPoint")
@JsonDeserialize(builder = GeoPoint.Builder.class)
public class GeoPoint {
	private final double latitude;
	private final double longitude;

	private GeoPoint(Builder builder) {
		latitude = builder.latitude;
		longitude = builder.longitude;
	}

	@ApiModelProperty(name = "latitude")
	@JsonProperty("latitude")
	public double getLatitude() {
		return latitude;
	}

	@ApiModelProperty(name = "longitude")
	@JsonProperty("longitude")
	public double getLongitude() {
		return longitude;
	}

	public static final class Builder {
		private double latitude;
		private double longitude;

		public Builder() {
		}

		public Builder(GeoPoint copy) {
			this.latitude = copy.latitude;
			this.longitude = copy.longitude;
		}

		@JsonProperty("latitude")
		public Builder withLatitude(double latitude) {
			this.latitude = latitude;
			return this;
		}

		@JsonProperty("longitude")
		public Builder withLongitude(double longitude) {
			this.longitude = longitude;
			return this;
		}

		public GeoPoint build() {
			return new GeoPoint(this);
		}
	}
}
