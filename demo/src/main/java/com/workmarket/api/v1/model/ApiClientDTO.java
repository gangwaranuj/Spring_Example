package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Client")
@JsonDeserialize(builder = ApiClientDTO.Builder.class)
public class ApiClientDTO {

	private final Long id;
	private final String name;
	private final String customerId;

	private ApiClientDTO(Builder builder) {
		id = builder.id;
		name = builder.name;
		customerId = builder.customerId;
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

	@ApiModelProperty(name = "customer_id")
	@JsonProperty("customer_id")
	public String getCustomerId() {
		return customerId;
	}

	public static final class Builder {
		private Long id;
		private String name;
		private String customerId;

		public Builder() {
		}

		public Builder(ApiClientDTO copy) {
			this.id = copy.id;
			this.name = copy.name;
			this.customerId = copy.customerId;
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

		@JsonProperty("customer_id")
		public Builder withCustomerId(String customerId) {
			this.customerId = customerId;
			return this;
		}

		public ApiClientDTO build() {
			return new ApiClientDTO(this);
		}
	}
}
