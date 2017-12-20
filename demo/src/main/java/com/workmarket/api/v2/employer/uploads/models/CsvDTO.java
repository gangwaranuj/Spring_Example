package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModelProperty;

@JsonDeserialize(builder = CsvDTO.Builder.class)
public class CsvDTO {
	private final Long id;
	private final String uuid;
	private final String name;

	private CsvDTO(Builder builder) {
		this.id = builder.id;
		this.uuid = builder.uuid;
		this.name = builder.name;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public static class Builder implements AbstractBuilder<CsvDTO> {
		private Long id;
		private String uuid;
		private String name;

		public Builder(CsvDTO csvDTO) {
			this.id = csvDTO.id;
			this.uuid = csvDTO.uuid;
			this.name = csvDTO.name;
		}

		public Builder() {}

		@JsonProperty("id")
		public Builder setId(Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("uuid")
		public Builder setUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("name")
		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		@Override
		public CsvDTO build() {
			return new CsvDTO(this);
		}
	}
}
