package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Template")
@JsonDeserialize(builder = TemplateDTO.Builder.class)
public class TemplateDTO {
	private final String id;
	private final String name;
	private final String description;
	private final AssignmentDTO assignment;

	private TemplateDTO(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.description = builder.description;
		this.assignment = builder.assignment.build();
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

	@ApiModelProperty(name = "description")
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@ApiModelProperty(name = "assignment")
	@JsonProperty("assignment")
	public AssignmentDTO getAssignment() {
		return assignment;
	}

	public static class Builder implements AbstractBuilder<TemplateDTO> {
		private String id;
		private String name;
		private String description;
		private AssignmentDTO.Builder assignment = new AssignmentDTO.Builder();

		public Builder() {}

		public Builder(TemplateDTO templateDTO) {
			this.id = templateDTO.id;
			this.name = templateDTO.name;
			this.description = templateDTO.description;
			this.assignment = new AssignmentDTO.Builder(templateDTO.assignment);
		}

		@JsonProperty("id") public Builder setId(String id) {
			this.id = id;
			return this;
		}

		@JsonProperty("name") public Builder setName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("description") public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("assignment") public Builder setAssignment(AssignmentDTO.Builder assignment) {
			this.assignment = assignment;
			return this;
		}

		public TemplateDTO build() {
			return new TemplateDTO(this);
		}
	}
}
