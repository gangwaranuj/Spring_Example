package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Location")
@JsonDeserialize(builder = ApiLocationDTO.Builder.class)
public class ApiLocationDTO {

	private final Long id;
	private final String name;
	private final String locationNumber;
	private final String instructions;
	private final String address1;
	private final String address2;
	private final String city;
	private final String state;
	private final String zip;
	private final String country;
	private final String latitude;
	private final String longitude;

	private ApiLocationDTO(Builder builder) {
		id = builder.id;
		name = builder.name;
		locationNumber = builder.locationNumber;
		instructions = builder.instructions;
		address1 = builder.address1;
		address2 = builder.address2;
		city = builder.city;
		state = builder.state;
		zip = builder.zip;
		country = builder.country;
		latitude = builder.latitude;
		longitude = builder.longitude;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@ApiModelProperty(name = "location_number")
	@JsonProperty("location_number")
	public String getLocationNumber() {
		return locationNumber;
	}

	@ApiModelProperty(name = "instructions")
	@JsonProperty("instructions")
	public String getInstructions() {
		return instructions;
	}

	@ApiModelProperty(name = "address_1")
	@JsonProperty("address_1")
	public String getAddress1() {
		return address1;
	}

	@ApiModelProperty(name = "address_2")
	@JsonProperty("address_2")
	public String getAddress2() {
		return address2;
	}

	@ApiModelProperty(name = "city")
	@JsonProperty("city")
	public String getCity() {
		return city;
	}

	@ApiModelProperty(name = "state")
	@JsonProperty("state")
	public String getState() {
		return state;
	}

	@ApiModelProperty(name = "zip")
	@JsonProperty("zip")
	public String getZip() {
		return zip;
	}

	@ApiModelProperty(name = "country")
	@JsonProperty("country")
	public String getCountry() {
		return country;
	}

	@ApiModelProperty(name = "latitude")
	@JsonProperty("latitude")
	public String getLatitude() {
		return latitude;
	}

	@ApiModelProperty(name = "longitude")
	@JsonProperty("longitude")
	public String getLongitude() {
		return longitude;
	}

	public static final class Builder {
		private Long id;
		private String name;
		private String locationNumber;
		private String instructions;
		private String address1;
		private String address2;
		private String city;
		private String state;
		private String zip;
		private String country;
		private String latitude;
		private String longitude;

		public Builder() {
		}

		public Builder(ApiLocationDTO copy) {
			this.id = copy.id;
			this.name = copy.name;
			this.locationNumber = copy.locationNumber;
			this.instructions = copy.instructions;
			this.address1 = copy.address1;
			this.address2 = copy.address2;
			this.city = copy.city;
			this.state = copy.state;
			this.zip = copy.zip;
			this.country = copy.country;
			this.latitude = copy.latitude;
			this.longitude = copy.longitude;
		}

		@JsonProperty("id")
		public Builder withId(Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("name")
		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("location_number")
		public Builder withLocationNumber(String locationNumber) {
			this.locationNumber = locationNumber;
			return this;
		}

		@JsonProperty("instructions")
		public Builder withInstructions(String instructions) {
			this.instructions = instructions;
			return this;
		}

		@JsonProperty("address_1")
		public Builder withAddress1(String address1) {
			this.address1 = address1;
			return this;
		}

		@JsonProperty("address_2")
		public Builder withAddress2(String address2) {
			this.address2 = address2;
			return this;
		}

		@JsonProperty("city")
		public Builder withCity(String city) {
			this.city = city;
			return this;
		}

		@JsonProperty("state")
		public Builder withState(String state) {
			this.state = state;
			return this;
		}

		@JsonProperty("zip")
		public Builder withZip(String zip) {
			this.zip = zip;
			return this;
		}

		@JsonProperty("country")
		public Builder withCountry(String country) {
			this.country = country;
			return this;
		}

		@JsonProperty("latitude")
		public Builder withLatitude(String latitude) {
			this.latitude = latitude;
			return this;
		}

		@JsonProperty("longitude")
		public Builder withLongitude(String longitude) {
			this.longitude = longitude;
			return this;
		}

		public ApiLocationDTO build() {
			return new ApiLocationDTO(this);
		}
	}
}
