package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.domains.model.ImageCoordinates;
import com.workmarket.domains.model.ImageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by joshlevine on 2/15/17.
 */
@ApiModel("Image")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = ApiImageDTO.Builder.class)
public class ApiImageDTO {
	private String url; // CDN url
	private String image; // Base 64 string
	private String filename;
	private ApiImageCoordinates coordinates;

	private ApiImageDTO(Builder builder) {
		this.url = builder.url;
		this.image = builder.image;
		this.filename = builder.filename;
		this.coordinates = builder.coordinates != null ? builder.coordinates.build() : null;
	}

	@ApiModelProperty(name = "url", notes = "URL to the image")
	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	@ApiModelProperty(name = "image", notes = "Base64 encoded string")
	@JsonProperty("image")
	public String getImage() {
		return image;
	}

	@ApiModelProperty(name = "filename", notes = "Filename of the image")
	@JsonProperty("filename")
	public String getFilename() {
		return filename;
	}

	@ApiModelProperty(name = "coordinates", notes = "Coordinates used to crop the image")
	@JsonProperty("coordinates")
	public ApiImageCoordinates getCoordinates() {
		return coordinates;
	}

	public static class Builder {
		private String url;
		private String image;
		private String filename;
		private ApiImageCoordinates.Builder coordinates;

		public Builder() {
		}

		public Builder(ApiImageDTO prototype) {
			if(prototype != null) {
				url = prototype.url;
				image = prototype.image;
				filename = prototype.filename;
				coordinates = new ApiImageCoordinates.Builder(prototype.coordinates);
			}
		}

		public Builder(ImageDTO prototype) {
			if(prototype != null) {
				url = prototype.getUrl();
				image = prototype.getImage();
				filename = prototype.getFilename();
				coordinates = new ApiImageCoordinates.Builder(prototype.getCoordinates());
			}
		}

		@JsonProperty("url")
		public Builder url(String url) {
			this.url = url;
			return this;
		}

		@JsonProperty("image")
		public Builder image(String image) {
			this.image = image;
			return this;
		}

		@JsonProperty("filename")
		public Builder filename(String filename) {
			this.filename = filename;
			return this;
		}

		@JsonProperty("coordinates")
		public Builder coordinates(ApiImageCoordinates.Builder coordinates) {
			this.coordinates = coordinates;
			return this;
		}

		public ApiImageDTO build() {
			return new ApiImageDTO(this);
		}
	}

	public ImageDTO asImageDTO() {
		ImageCoordinates coordinates = this.coordinates != null ? this.coordinates.asImageCoordinates() : null;
		return new ImageDTO(url, image, filename, coordinates);
	}
}
