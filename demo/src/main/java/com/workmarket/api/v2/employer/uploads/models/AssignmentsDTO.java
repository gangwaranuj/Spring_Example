package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.api.client.util.Lists;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Assignments")
@JsonDeserialize(builder = AssignmentsDTO.Builder.class)
public class AssignmentsDTO {
	private final String uuid;
	private final long count;
	private final List<AssignmentDTO> assignments = Lists.newArrayList();

	private AssignmentsDTO(Builder builder) {
		this.uuid = builder.uuid;
		this.count = builder.count;
		for (AssignmentDTO.Builder assignmentBuilder : builder.assignments) {
			this.assignments.add(assignmentBuilder.build());
		}
	}

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "count")
	@JsonProperty("count")
	public long getCount() {
		return count;
	}

	@ApiModelProperty(name = "assignments")
	@JsonProperty("assignments")
	public List<AssignmentDTO> getAssignments() {
		return assignments;
	}

	public static class Builder implements AbstractBuilder<AssignmentsDTO> {
		private String uuid;
		private long count = 0L;
		private List<AssignmentDTO.Builder> assignments = Lists.newArrayList();

		public Builder(AssignmentsDTO assignmentsDTO) {
			this.uuid = assignmentsDTO.uuid;
			this.count = assignmentsDTO.count;
			for (AssignmentDTO assignment : assignmentsDTO.assignments) {
				this.assignments.add(new AssignmentDTO.Builder(assignment));
			}
		}

		public Builder() {}

		@JsonProperty("uuid") public Builder setUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("count") public Builder setCount(long count) {
			this.count = count;
			return this;
		}

		@JsonProperty("assignments") public Builder setAssignments(List<AssignmentDTO.Builder> assignments) {
			this.assignments = assignments;
			return this;
		}

		public Builder addAssignment(AssignmentDTO.Builder assignment) {
			this.assignments.add(assignment);
			return this;
		}

		@Override
		public AssignmentsDTO build() {
			return new AssignmentsDTO(this);
		}
	}
}
