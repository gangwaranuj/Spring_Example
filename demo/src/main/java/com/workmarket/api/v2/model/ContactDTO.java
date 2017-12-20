package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Contact")
@JsonDeserialize(builder = ContactDTO.Builder.class)
public class ContactDTO {
	// id indicates existing contact, when present, other fields are ignored and
	// the identified contact is used
	private final Long id;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final String workPhone;
	private final String workPhoneExtension;
	private final String mobilePhone;

	private ContactDTO(Builder builder) {
		this.id = builder.id;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.email = builder.email;
		this.workPhone = builder.workPhone;
		this.workPhoneExtension = builder.workPhoneExtension;
		this.mobilePhone = builder.mobilePhone;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "firstName")
	@JsonProperty("firstName")
	public String getFirstName() {
		return firstName;
	}

	@ApiModelProperty(name = "lastName")
	@JsonProperty("lastName")
	public String getLastName() {
		return lastName;
	}

	@ApiModelProperty(name = "email")
	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@ApiModelProperty(name = "workPhone")
	@JsonProperty("workPhone")
	public String getWorkPhone() {
		return workPhone;
	}

	@ApiModelProperty(name = "workPhoneExtension")
	@JsonProperty("workPhoneExtension")
	public String getWorkPhoneExtension() {
		return workPhoneExtension;
	}

	@ApiModelProperty(name = "mobilePhone")
	@JsonProperty("mobilePhone")
	public String getMobilePhone() {
		return mobilePhone;
	}

	public static class Builder implements AbstractBuilder<ContactDTO> {
		private Long id;
		private String firstName;
		private String lastName;
		private String email;
		private String workPhone;
		private String workPhoneExtension;
		private String mobilePhone;

		public Builder() {}

		public Builder(ContactDTO contactDTO) {
			this.id = contactDTO.id;
			this.firstName = contactDTO.firstName;
			this.lastName = contactDTO.lastName;
			this.email = contactDTO.email;
			this.workPhone = contactDTO.workPhone;
			this.workPhoneExtension = contactDTO.workPhoneExtension;
			this.mobilePhone = contactDTO.mobilePhone;
		}

		@JsonProperty("id") public Builder setId(Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("firstName") public Builder setFirstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		@JsonProperty("lastName") public Builder setLastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		@JsonProperty("email") public Builder setEmail(String email) {
			this.email = email;
			return this;
		}

		@JsonProperty("workPhone") public Builder setWorkPhone(String workPhone) {
			this.workPhone = workPhone;
			return this;
		}

		@JsonProperty("workPhoneExtension") public Builder setWorkPhoneExtension(String workPhoneExtension) {
			this.workPhoneExtension = workPhoneExtension;
			return this;
		}

		@JsonProperty("mobilePhone") public Builder setMobilePhone(String mobilePhone) {
			this.mobilePhone = mobilePhone;
			return this;
		}

		public ContactDTO build() {
			return new ContactDTO(this);
		}
	}
}
