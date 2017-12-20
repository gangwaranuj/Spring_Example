package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.api.client.util.Sets;

import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "DeliverablesGroup")
@JsonDeserialize(builder = DeliverablesGroupDTO.Builder.class)
public class DeliverablesGroupDTO {
	private final Long id;
	private final String instructions;
	private final int hoursToComplete;
	private final Set<DeliverableDTO> deliverables = Sets.newHashSet();

	private DeliverablesGroupDTO(Builder builder) {
		this.id = builder.id;
		this.instructions = builder.instructions;
		this.hoursToComplete = builder.hoursToComplete;

		for (DeliverableDTO.Builder deliverable : builder.deliverables) {
			this.deliverables.add(deliverable.build());
		}
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "instructions")
	@JsonProperty("instructions")
	public String getInstructions() {
		return instructions;
	}

	@ApiModelProperty(name = "hoursToComplete")
	@JsonProperty("hoursToComplete")
	public int getHoursToComplete() {
		return hoursToComplete;
	}

	@ApiModelProperty(name = "deliverables")
	@JsonProperty("deliverables")
	public Set<DeliverableDTO> getDeliverables() {
		return deliverables;
	}

	public static class Builder implements AbstractBuilder<DeliverablesGroupDTO> {
		private Long id;
		private String instructions;
		private int hoursToComplete;
		public Set<DeliverableDTO.Builder> deliverables = Sets.newHashSet();

		public Builder(DeliverablesGroupDTO deliverablesGroupDTO) {
			this.id = deliverablesGroupDTO.id;
			this.instructions = deliverablesGroupDTO.instructions;
			this.hoursToComplete = deliverablesGroupDTO.hoursToComplete;

			for(DeliverableDTO deliverable : deliverablesGroupDTO.deliverables) {
				this.deliverables.add(new DeliverableDTO.Builder(deliverable));
			}
		}

		public Builder() {}

		@JsonProperty("id") public Builder setId(Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("instructions") public Builder setInstructions(String instructions) {
			this.instructions = instructions;
			return this;
		}

		@JsonProperty("hoursToComplete") public Builder setHoursToComplete(int hoursToComplete) {
			this.hoursToComplete = hoursToComplete;
			return this;
		}

		@JsonProperty("deliverables") public Builder setDeliverables(Set<DeliverableDTO.Builder> deliverables) {
			this.deliverables = deliverables;
			return this;
		}

		public Builder addDeliverable(DeliverableDTO.Builder deliverable) {
			this.deliverables.add(deliverable);
			return this;
		}

		@Override
		public DeliverablesGroupDTO build() {
			return new DeliverablesGroupDTO(this);
		}
	}
}
