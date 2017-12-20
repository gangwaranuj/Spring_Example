package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Sets;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

@ApiModel(value = "CustomFieldGroup")
@JsonDeserialize(builder = CustomFieldGroupDTO.Builder.class)
public class CustomFieldGroupDTO {
	private final long id;
	private final String name;
	private final boolean required;
	private final Integer position;
	//changed to LinkedHashSet to avoid random ordering of the CustomFieldDTO objects
	private final Set<CustomFieldDTO> fields = Sets.newLinkedHashSet();

	private CustomFieldGroupDTO(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.required = builder.required;
		this.position = builder.position;

		for (CustomFieldDTO.Builder field : builder.fields) {
			this.fields.add(field.build());
		}
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public long getId() {
		return id;
	}

	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@ApiModelProperty(name = "required")
	@JsonProperty("required")
	public boolean isRequired() {
		return required;
	}

	@ApiModelProperty(name = "position")
	@JsonProperty("position")
	public Integer getPosition() {
		return position;
	}

	@ApiModelProperty(name = "fields")
	@JsonProperty("fields")
	public Set<CustomFieldDTO> getFields() {
		return fields;
	}

	public static class Builder implements AbstractBuilder<CustomFieldGroupDTO> {
		private long id;
		private String name;
		private boolean required = false;
		private Integer position;
		private Set<CustomFieldDTO.Builder> fields = Sets.newLinkedHashSet();

		public Builder() {}

		public Builder(CustomFieldGroupDTO customFieldGroupDTO) {
			this.id = customFieldGroupDTO.id;
			this.name = customFieldGroupDTO.name;
			this.required = customFieldGroupDTO.required;
			this.position = customFieldGroupDTO.position;

			for (CustomFieldDTO field : customFieldGroupDTO.fields) {
				this.fields.add(new CustomFieldDTO.Builder(field));
			}
		}

		@JsonProperty("id") public Builder setId(long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("name") public Builder setName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("required") public Builder setRequired(boolean required) {
			this.required = required;
			return this;
		}

		@JsonProperty("position") public Builder setPosition(Integer position) {
			this.position = position;
			return this;
		}

		@JsonProperty("fields") public Builder setFields(Set<CustomFieldDTO.Builder> fields) {
			this.fields.addAll(fields);
			return this;
		}

		public Builder addField(CustomFieldDTO.Builder field) {
			this.fields.add(field);
			return this;
		}

		public CustomFieldGroupDTO build() {
			return new CustomFieldGroupDTO(this);
		}
	}
}
