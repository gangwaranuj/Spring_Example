package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "InternalOwnerDetails")
@JsonDeserialize(builder = ApiInternalOwnerDetailsDTO.Builder.class)
public class ApiInternalOwnerDetailsDTO {

	private final String firstName;
	private final String lastName;

	private ApiInternalOwnerDetailsDTO(Builder builder) {
		firstName = builder.firstName;
		lastName = builder.lastName;
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

	public static final class Builder {
		private String firstName;
		private String lastName;

		public Builder() {
		}

		public Builder(ApiInternalOwnerDetailsDTO copy) {
			this.firstName = copy.firstName;
			this.lastName = copy.lastName;
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

		public ApiInternalOwnerDetailsDTO build() {
			return new ApiInternalOwnerDetailsDTO(this);
		}
	}
}
