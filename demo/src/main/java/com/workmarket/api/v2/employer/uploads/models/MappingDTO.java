package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@JsonDeserialize(builder = MappingDTO.Builder.class)
public class MappingDTO implements Serializable {
	private static final long serialVersionUID = -6660397464835926281L;

	private final Long id;
	private final String property;
	private final String header;
	private final int position;

	private MappingDTO(Builder builder) {
		this.id = builder.id;
		this.property = builder.property;
		this.header = builder.header;
		this.position = builder.position;
	}

	@ApiModelProperty(name = "serialVersionUID")
	@JsonProperty("serialVersionUID")
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "property")
	@JsonProperty("property")
	public String getProperty() {
		return property;
	}

	@ApiModelProperty(name = "header")
	@JsonProperty("header")
	public String getHeader() {
		return header;
	}

	@ApiModelProperty(name = "position")
	@JsonProperty("position")
	public int getPosition() {
		return position;
	}

	public static class Builder implements AbstractBuilder<MappingDTO> {
		private Long id;
		private String property;
		private String header;
		private int position;

		public Builder(MappingDTO mappingDTO) {
			this.id = mappingDTO.id;
			this.property = mappingDTO.property;
			this.header = mappingDTO.header;
			this.position = mappingDTO.position;
		}

		public Builder() {}

		@JsonProperty("id") public Builder setId(Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("property") public Builder setProperty(String property) {
			this.property = property;
			return this;
		}

		@JsonProperty("header") public Builder setHeader(String header) {
			this.header = header;
			return this;
		}

		@JsonProperty("position") public Builder setPosition(int position) {
			this.position = position;
			return this;
		}

		@Override
		public MappingDTO build() {
			return new MappingDTO(this);
		}
	}
}
