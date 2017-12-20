package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Label")
@JsonDeserialize(builder = ApiLabelDTO.Builder.class)
public class ApiLabelDTO {
	private final String id;
	private final String name;

	private ApiLabelDTO(Builder builder) {
		id = builder.id;
		name = builder.name;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public static final class Builder {
		private String id;
		private String name;

		public Builder() {
		}

		public Builder(ApiLabelDTO copy) {
			this.id = copy.id;
			this.name = copy.name;
		}

		@JsonProperty("id")
		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		@JsonProperty("name")
		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public ApiLabelDTO build() {
			return new ApiLabelDTO(this);
		}
	}
}
