package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import com.workmarket.dto.AddressDTO;
import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;

@ApiModel("Address")
@JsonDeserialize(builder = AddressApiDTO.Builder.class)
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
public class AddressApiDTO {
	private final Long id;
	private final String addressLine1;
	private final String addressLine2;
	private final String city;
	private final String state;
	private final String postalCode;
	private final String country;
	private final BigDecimal longitude;
	private final BigDecimal latitude;
	private final String addressTypeCode;

	public AddressApiDTO(final Builder builder) {
		this.id = builder.id;
		this.addressLine1 = builder.addressLine1;
		this.addressLine2 = builder.addressLine2;
		this.city = builder.city;
		this.state = builder.state;
		this.postalCode = builder.postalCode;
		this.country = builder.country;
		this.longitude = builder.longitude;
		this.latitude = builder.latitude;
		this.addressTypeCode = builder.addressTypeCode;
	}

	@JsonIgnore
	public Long getId() {
		return id;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCountry() {
		return country;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public String getAddressTypeCode() {
		return addressTypeCode;
	}

	public static class Builder implements AbstractBuilder<AddressApiDTO> {
		private Long id;
		private String addressLine1;
		private String addressLine2;
		private String city;
		private String state;
		private String postalCode;
		private String country;
		private BigDecimal longitude;
		private BigDecimal latitude;
		private String addressTypeCode;

		public Builder() {
		}

		public Builder(final AddressDTO addressDTO) {
			if(addressDTO == null) {
				return;
			}
			this.id = addressDTO.getAddressId();
			this.addressLine1 = addressDTO.getAddress1();
			this.addressLine2 = addressDTO.getAddress2();
			this.city = addressDTO.getCity();
			this.state = addressDTO.getState();
			this.postalCode = addressDTO.getPostalCode();
			this.country = addressDTO.getCountry();
			this.longitude = addressDTO.getLongitude();
			this.latitude = addressDTO.getLatitude();
			this.addressTypeCode = addressDTO.getAddressTypeCode();
		}

		public Builder(final AddressApiDTO addressApiDTO) {
			if(addressApiDTO == null) {
				return;
			}
			this.id = addressApiDTO.id;
			this.addressLine1 = addressApiDTO.addressLine1;
			this.addressLine2 = addressApiDTO.addressLine2;
			this.city = addressApiDTO.city;
			this.state = addressApiDTO.state;
			this.postalCode = addressApiDTO.postalCode;
			this.country = addressApiDTO.country;
			this.longitude = addressApiDTO.longitude;
			this.latitude = addressApiDTO.latitude;
			this.addressTypeCode = addressApiDTO.addressTypeCode;
		}

		@JsonProperty("id") public Builder setId(final Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("addressLine1") public Builder setAddressLine1(final String addressLine1) {
			this.addressLine1 = addressLine1;
			return this;
		}

		@JsonProperty("addressLine2") public Builder setAddressLine2(final String addressLine2) {
			this.addressLine2 = addressLine2;
			return this;
		}

		@JsonProperty("city") public Builder setCity(final String city) {
			this.city = city;
			return this;
		}

		@JsonProperty("state") public Builder setState(final String state) {
			this.state = state;
			return this;
		}

		@JsonProperty("postalCode") public Builder setPostalCode(final String postalCode) {
			this.postalCode = postalCode;
			return this;
		}

		@JsonProperty("country") public Builder setCountry(final String country) {
			this.country = country;
			return this;
		}

		@JsonProperty("longitude") public Builder setLongitude(final BigDecimal longitude) {
			this.longitude = longitude;
			return this;
		}

		@JsonProperty("latitude") public Builder setLatitude(final BigDecimal latitude) {
			this.latitude = latitude;
			return this;
		}

		@JsonProperty("addressTypeCode") public Builder setAddressTypeCode(final String addressTypeCode) {
			this.addressTypeCode = addressTypeCode;
			return this;
		}

		public AddressApiDTO build() {
			return new AddressApiDTO(this);
		}
	}

	public AddressDTO asAddressDTO() {
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddressId(getId());
		addressDTO.setAddressTypeCode(getAddressTypeCode());
		addressDTO.setAddress1(getAddressLine1());
		addressDTO.setAddress2(getAddressLine2());
		addressDTO.setCity(getCity());
		addressDTO.setState(getState());
		addressDTO.setPostalCode(getPostalCode());
		addressDTO.setCountry(getCountry());
		addressDTO.setLongitude(getLongitude());
		addressDTO.setLatitude(getLatitude());

		return addressDTO;
	}
}
