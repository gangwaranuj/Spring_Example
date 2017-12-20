package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.api.client.util.Sets;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

@JsonDeserialize(builder = MappingsDTO.Builder.class)
public class MappingsDTO {
	private final Long id;
	private final String name;
	private final Set<MappingDTO> mappings = Sets.newHashSet();

	private MappingsDTO(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;

		for (MappingDTO.Builder mapping : builder.mappings) {
			this.mappings.add(mapping.build());
		}
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

	@ApiModelProperty(name = "mappings")
	@JsonProperty("mappings")
	public Set<MappingDTO> getMappings() {
		return mappings;
	}

	public static class Builder implements AbstractBuilder<MappingsDTO> {
		private Long id;
		private String name;
		private Set<MappingDTO.Builder> mappings = Sets.newHashSet();

		public Builder(MappingsDTO mappingsDTO) {
			this.id = mappingsDTO.id;
			this.name = mappingsDTO.name;
		}

		public Builder() {}

		@JsonProperty("id") public Builder setId(Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("name") public Builder setName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("mappings") public Builder setMappings(Set<MappingDTO.Builder> mappings) {
			this.mappings = mappings;
			return this;
		}

		public Builder addMapping(MappingDTO.Builder mapping) {
			this.mappings.add(mapping);
			return this;
		}

		@Override
		public MappingsDTO build() {
			return new MappingsDTO(this);
		}
	}
}
