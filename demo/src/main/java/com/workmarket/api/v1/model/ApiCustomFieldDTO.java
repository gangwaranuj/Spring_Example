package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "CustomField")
@JsonDeserialize(builder = ApiCustomFieldDTO.Builder.class)
public class ApiCustomFieldDTO {

	private final Long id;
	private final String name;
	private final String defaultValue;
	private final Boolean required;
	private final String value;

	private ApiCustomFieldDTO(Builder builder) {
		id = builder.id;
		name = builder.name;
		defaultValue = builder.defaultValue;
		required = builder.required;
		value = builder.value;
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

	@ApiModelProperty(name = "default")
	@JsonProperty("default")
	public String getDefaultValue() {
		return defaultValue;
	}

	@ApiModelProperty(name = "required")
	@JsonProperty("required")
	public Boolean getRequired() {
		return required;
	}

	@ApiModelProperty(name = "value")
	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	public static final class Builder {
		private Long id;
		private String name;
		private String defaultValue;
		private Boolean required;
		private String value;

		public Builder() {
		}

		public Builder(ApiCustomFieldDTO copy) {
			this.id = copy.id;
			this.name = copy.name;
			this.defaultValue = copy.defaultValue;
			this.required = copy.required;
			this.value = copy.value;
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

		@JsonProperty("default_value")
		public Builder withDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		@JsonProperty("required")
		public Builder withRequired(Boolean required) {
			this.required = required;
			return this;
		}

		@JsonProperty("value")
		public Builder withValue(String value) {
			this.value = value;
			return this;
		}

		public ApiCustomFieldDTO build() {
			return new ApiCustomFieldDTO(this);
		}
	}
}
