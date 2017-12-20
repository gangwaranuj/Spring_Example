package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by ianha on 4/10/15.
 */
@ApiModel("Address")
@JsonDeserialize(builder = Address.Builder.class)
public class Address {
	private final String addressLine1;
	private final String addressLine2;
	private final String city;
	private final String state;
	private final String zip;
	private final String country;
	private final GeoPoint geo;

	@ApiModelProperty(name = "addressLine1")
	@JsonProperty("addressLine1")
	public String getAddressLine1() {
		return addressLine1;
	}

	@ApiModelProperty(name = "addressLine2")
	@JsonProperty("addressLine2")
	public String getAddressLine2() {
		return addressLine2;
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

	@ApiModelProperty(name = "geo")
	@JsonProperty("geo")
	public GeoPoint getGeo() {
		return geo;
	}

	private Address(Address.Builder builder) {
		this.addressLine1 = builder.addressLine1;
		this.addressLine2 = builder.addressLine2;
		this.city = builder.city;
		this.state = builder.state;
		this.zip = builder.zip;
		this.country = builder.country;
		this.geo = builder.geo.build(); // TODO API - pls review this
	}

	public static final class Builder {
		private String addressLine1 = "";
		private String addressLine2 = "";
		private String city = "";
		private String state = "";
		private String zip = "";
		private String country = "";
		private GeoPoint.Builder geo = new GeoPoint.Builder();

		public Builder() {
		}

		public Builder(Address.Builder copy) {
			this.addressLine1 = copy.addressLine1;
			this.addressLine2 = copy.addressLine2;
			this.city = copy.city;
			this.state = copy.state;
			this.zip = copy.zip;
			this.geo = copy.geo;
		}

		@JsonProperty("addressLine1")
		public Builder withAddressLine1(String addressLine1) {
			this.addressLine1 = addressLine1;
			return this;
		}

		@JsonProperty("addressLine2")
		public Builder withAddressLine2(String addressLine2) {
			this.addressLine2 = addressLine2;
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

		@JsonProperty("geo")
		public Builder withGeo(GeoPoint.Builder geo) {
			this.geo = geo;
			return this;
		}

		public Address build() {
			return new Address(this);
		}

		public GeoPoint.Builder getGeo() {
			return geo;
		}
	}
}
