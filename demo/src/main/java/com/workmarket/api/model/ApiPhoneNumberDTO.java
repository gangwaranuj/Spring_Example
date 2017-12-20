package com.workmarket.api.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.onboarding.model.PhoneInfoDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "PhoneNumber")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = ApiPhoneNumberDTO.Builder.class)
public class ApiPhoneNumberDTO {

	private final String countryCode;
	private final String phone;
	private final ContactContextType type;
	private final String extension;

	private ApiPhoneNumberDTO(Builder builder) {
		countryCode = builder.countryCode;
		phone = builder.phone;
		type = builder.type;
		extension = builder.extension;
	}

	@ApiModelProperty(name = "country_code")
	@JsonProperty("country_code")
	public String getCountryCode() {
		return countryCode;
	}

	@ApiModelProperty(name = "phone")
	@JsonProperty("phone")
	public String getPhone() {
		return phone;
	}

	@ApiModelProperty(hidden = true)
	@JsonProperty("number")
	public String getNumber() {
		return phone;
	}
	@ApiModelProperty(name = "type")
	@JsonProperty("type")
	public ContactContextType getType() {
		return type;
	}

	@ApiModelProperty(name = "extension")
	@JsonProperty("extension")
	public String getExtension() {
		return extension;
	}

	public PhoneInfoDTO asPhoneInfoDTO() {
		return new PhoneInfoDTO(type.toString(), countryCode, phone);
	}

	public static final class Builder {
		private String countryCode;
		private String phone;
		private ContactContextType type;
		private String extension;

		public Builder() {
		}

		public Builder(ApiPhoneNumberDTO copy) {
			this.countryCode = copy.countryCode;
			this.phone = copy.phone;
			this.type = copy.type;
			this.extension = copy.extension;
		}

		@JsonProperty("country_code")
		public Builder withCountryCode(String countryCode) {
			this.countryCode = countryCode;
			return this;
		}

		@JsonProperty("phone")
		public Builder withPhone(String phone) {
			this.phone = phone;
			return this;
		}

		@JsonProperty("number")
		public Builder withNumber(String number) {
			this.phone = number;
			return this;
		}

		@JsonProperty("type")
		public Builder withType(ContactContextType type) {
			this.type = type;
			return this;
		}

		@JsonProperty("extension")
		public Builder withExtension(String extension) {
			this.extension = extension;
			return this;
		}

		public ApiPhoneNumberDTO build() {
			return new ApiPhoneNumberDTO(this);
		}
	}
}
