package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "CustomFieldGroup")
@JsonDeserialize(builder = ApiCustomFieldGroupDTO.Builder.class)
public class ApiCustomFieldGroupDTO {

	private final Long id;
	private final String name;
	private final List<ApiCustomFieldDTO> fields;
	private final Boolean required;

	private ApiCustomFieldGroupDTO(Builder builder) {
		id = builder.id;
		name = builder.name;
		fields = builder.fields;
		required = builder.required;
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

	@ApiModelProperty(name = "fields")
	@JsonProperty("fields")
	public List<ApiCustomFieldDTO> getFields() {
		return fields;
	}

	@ApiModelProperty(name = "required")
	@JsonProperty("required")
	public Boolean getRequired() {
		return required;
	}

	public static final class Builder {
		private Long id;
		private String name;
		private List<ApiCustomFieldDTO> fields;
		private Boolean required;

		public Builder() {
		}

		public Builder(ApiCustomFieldGroupDTO copy) {
			this.id = copy.id;
			this.name = copy.name;
			this.fields = copy.fields;
			this.required = copy.required;
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

		@JsonProperty("fields")
		public Builder withFields(List<ApiCustomFieldDTO> fields) {
			this.fields = fields;
			return this;
		}

		@JsonProperty("required")
		public Builder withRequired(Boolean required) {
			this.required = required;
			return this;
		}

		public ApiCustomFieldGroupDTO build() {
			return new ApiCustomFieldGroupDTO(this);
		}
	}
}
