package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

@ApiModel(value = "Address")
@JsonDeserialize(builder = ApiAddressDTO.Builder.class)
public class ApiAddressDTO {

	private final String address1;
	private final String address2;
	private final String city;
	private final String state;
	private final String zip;
	private final String country;

	private ApiAddressDTO(Builder builder) {
		address1 = builder.address1;
		address2 = builder.address2;
		city = builder.city;
		state = builder.state;
		zip = builder.zip;
		country = builder.country;
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

	public static final class Builder {
		private String address1;
		private String address2;
		private String city;
		private String state;
		private String zip;
		private String country;

		public Builder() {
		}

		public Builder(ApiAddressDTO copy) {
			this.address1 = copy.address1;
			this.address2 = copy.address2;
			this.city = copy.city;
			this.state = copy.state;
			this.zip = copy.zip;
			this.country = copy.country;
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

		public ApiAddressDTO build() {
			return new ApiAddressDTO(this);
		}
	}
}