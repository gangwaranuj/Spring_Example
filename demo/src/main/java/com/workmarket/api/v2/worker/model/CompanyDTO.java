package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by ianha on 4/13/15.
 */
@ApiModel(value = "Company")
@JsonDeserialize(builder = CompanyDTO.Builder.class)
public class CompanyDTO {
	private final String name;

	private CompanyDTO(Builder builder) {
		name = builder.name;
	}

	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public static final class Builder {
		private String name;

		public Builder() {
		}

		public Builder(CompanyDTO copy) {
			this.name = copy.name;
		}

		@JsonProperty("name")
		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public CompanyDTO build() {
			return new CompanyDTO(this);
		}
	}
}
