package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

// This class exists only bc we mistakenly return a DTO that uses
// the actual DB ID instead of the "code" for /labels/list... ugh...
// Other places, like /get return the code as the ID instead.
@Deprecated
@ApiModel(value = "ListLabel")
@JsonDeserialize(builder = ApiListLabelDTO.Builder.class)
public class ApiListLabelDTO {
	private final Long id;
	private final String name;

	private ApiListLabelDTO(Builder builder) {
		id = builder.id;
		name = builder.name;
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

	public static final class Builder {
		private Long id;
		private String name;

		public Builder() {
		}

		public Builder(ApiListLabelDTO copy) {
			this.id = copy.id;
			this.name = copy.name;
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

		public ApiListLabelDTO build() {
			return new ApiListLabelDTO(this);
		}
	}
}
