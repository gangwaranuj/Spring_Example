package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.model.ApiPhoneNumberDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "SupportContact")
@JsonDeserialize(builder = ApiSupportContactDTO.Builder.class)
public class ApiSupportContactDTO {

	private final String id; // String here, Long for LocationContact.. go figure..
	private final String firstName;
	private final String lastName;
	private final String email;
	private final List<ApiPhoneNumberDTO> phoneNumbers;

	private ApiSupportContactDTO(Builder builder) {
		id = builder.id;
		firstName = builder.firstName;
		lastName = builder.lastName;
		email = builder.email;
		phoneNumbers = builder.phoneNumbers;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@ApiModelProperty(name = "first_name")
	@JsonProperty("first_name")
	public String getFirstName() {
		return firstName;
	}

	@ApiModelProperty(name = "last_name")
	@JsonProperty("last_name")
	public String getLastName() {
		return lastName;
	}

	@ApiModelProperty(name = "email")
	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@ApiModelProperty(name = "phone_numbers")
	@JsonProperty("phone_numbers")
	public List<ApiPhoneNumberDTO> getPhoneNumbers() {
		return phoneNumbers;
	}


	public static final class Builder {
		private String id;
		private String firstName;
		private String lastName;
		private String email;
		private List<ApiPhoneNumberDTO> phoneNumbers;

		public Builder() {
		}

		public Builder(ApiSupportContactDTO copy) {
			this.id = copy.id;
			this.firstName = copy.firstName;
			this.lastName = copy.lastName;
			this.email = copy.email;
			this.phoneNumbers = copy.phoneNumbers;
		}

		@JsonProperty("id")
		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		@JsonProperty("first_name")
		public Builder withFirstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		@JsonProperty("last_name")
		public Builder withLastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		@JsonProperty("email")
		public Builder withEmail(String email) {
			this.email = email;
			return this;
		}

		@JsonProperty("phone_numbers")
		public Builder withPhoneNumbers(List<ApiPhoneNumberDTO> phoneNumbers) {
			this.phoneNumbers = phoneNumbers;
			return this;
		}

		public ApiSupportContactDTO build() {
			return new ApiSupportContactDTO(this);
		}
	}
}
