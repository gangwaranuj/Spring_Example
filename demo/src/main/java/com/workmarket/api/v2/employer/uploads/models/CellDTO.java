package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Cell")
@JsonDeserialize(builder = CellDTO.Builder.class)
public class CellDTO {
	private final String header;
	private final String value;
	private final int index;

	private CellDTO(Builder builder) {
		this.header = builder.header;
		this.value = builder.value;
		this.index = builder.index;
	}

	@ApiModelProperty(name = "header")
	@JsonProperty("header")
	public String getHeader() {
		return header;
	}

	@ApiModelProperty(name = "value")
	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	@ApiModelProperty(name = "index")
	@JsonProperty("index")
	public int getIndex() {
		return index;
	}

	public static class Builder implements AbstractBuilder<CellDTO> {
		private String header;
		private String value;
		private int index;

		public Builder(CellDTO cellDTO) {
			this.header = cellDTO.header;
			this.value = cellDTO.value;
			this.index = cellDTO.index;
		}

		public Builder() {}

		@JsonProperty("header") public Builder setHeader(String header) {
			this.header = header;
			return this;
		}

		@JsonProperty("value") public Builder setValue(String value) {
			this.value = value;
			return this;
		}

		@JsonProperty("index") public Builder setIndex(int index) {
			this.index = index;
			return this;
		}

		@Override
		public CellDTO build() {
			return new CellDTO(this);
		}
	}
}
