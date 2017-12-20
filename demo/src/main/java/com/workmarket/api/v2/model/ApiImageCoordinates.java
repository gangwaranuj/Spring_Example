package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.domains.model.ImageCoordinates;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.Min;

/**
 * Created by joshlevine on 2/15/17.
 */
@ApiModel("ImageCoordinates")
@JsonDeserialize(builder = ApiImageCoordinates.Builder.class)
public class ApiImageCoordinates {
	// crop top left point of crop rectangle, (0,0) is top left corner
	@Min(0) private Integer x;
	@Min(0) private Integer y;

	// crop bottom right point of crop rectangle, (0,0) is the top left corner
	@Min(1) private Integer x2;
	@Min(1) private Integer y2;

	private ApiImageCoordinates(Builder builder) {
		this.x = builder.x;
		this.y = builder.y;
		this.x2 = builder.x2;
		this.y2 = builder.y2;
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}

	public Integer getX2() {
		return x2;
	}

	public Integer getY2() {
		return y2;
	}


	public static class Builder {
		private Integer x;
		private Integer y;
		private Integer x2;
		private Integer y2;

		public Builder() {
		}

		public Builder(ImageCoordinates coordinates) {
			if(coordinates != null) {
				x = coordinates.getX();
				x = coordinates.getY();
				x2 = coordinates.getX2();
				y2 = coordinates.getY2();
			}
		}

		public Builder(ApiImageCoordinates prototype) {
			if(prototype != null) {
				x = prototype.x;
				y = prototype.y;
				x2 = prototype.x2;
				y2 = prototype.y2;
			}
		}

		@JsonProperty("x")
		public Builder x(Integer x) {
			this.x = x;
			return this;
		}

		@JsonProperty("y")
		public Builder y(Integer y) {
			this.y = y;
			return this;
		}

		@JsonProperty("x2")
		public Builder x2(Integer x2) {
			this.x2 = x2;
			return this;
		}

		@JsonProperty("y2")
		public Builder y2(Integer y2) {
			this.y2 = y2;
			return this;
		}

		public ApiImageCoordinates build() {
			return new ApiImageCoordinates(this);
		}
	}

	public ImageCoordinates asImageCoordinates() {
		return new ImageCoordinates(x, y, x2, y2);
	}
}
