package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Deliverable")
@JsonDeserialize(builder = DeliverableDTO.Builder.class)
public class DeliverableDTO {
	private final Long id;
	private final String type;
	private final String description;
	private final int numberOfFiles;
	private final int priority;

	private DeliverableDTO(Builder builder) {
		this.id = builder.id;
		this.type = builder.type;
		this.description = builder.description;
		this.numberOfFiles = builder.numberOfFiles;
		this.priority = builder.priority;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "type")
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@ApiModelProperty(name = "description")
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@ApiModelProperty(name = "numberOfFiles")
	@JsonProperty("numberOfFiles")
	public int getNumberOfFiles() {
		return numberOfFiles;
	}

	@ApiModelProperty(name = "priority")
	@JsonProperty("priority")
	public int getPriority() {
		return priority;
	}

	public static class Builder implements AbstractBuilder<DeliverableDTO> {
		private Long id;
		private String type;
		private String description;
		private int numberOfFiles;
		private int priority;

		public Builder(DeliverableDTO deliverableDTO) {
			this.id = deliverableDTO.id;
			this.type = deliverableDTO.type;
			this.description = deliverableDTO.description;
			this.numberOfFiles = deliverableDTO.numberOfFiles;
			this.priority = deliverableDTO.priority;
		}

		public Builder() {
		}

		@JsonProperty("id") public Builder setId(Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("type") public Builder setType(String type) {
			this.type = type;
			return this;
		}

		@JsonProperty("description") public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("numberOfFiles") public Builder setNumberOfFiles(int numberOfFiles) {
			this.numberOfFiles = numberOfFiles;
			return this;
		}

		@JsonProperty("priority") public Builder setPriority(int priority) {
			this.priority = priority;
			return this;
		}

		@Override
		public DeliverableDTO build() {
			return new DeliverableDTO(this);
		}
	}
}
