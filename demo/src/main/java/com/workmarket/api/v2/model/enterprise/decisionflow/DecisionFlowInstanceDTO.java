package com.workmarket.api.v2.model.enterprise.decisionflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("DecisionFlowInstance")
@JsonDeserialize(builder = DecisionFlowInstanceDTO.Builder.class)
public class DecisionFlowInstanceDTO {

	private final String uuid;
	private final String name;
	private final String description;
	private final List<DecisionStepInstanceDTO> completedSteps;
	private final DecisionStepDTO currentStep;
	private final List<DecisionFlowInstanceDTO> futureSteps;

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

	@ApiModelProperty(name = "description")
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@ApiModelProperty(name = "completedSteps")
	@JsonProperty("completedSteps")
	public List<DecisionStepInstanceDTO> getCompletedSteps() {
		return completedSteps;
	}

	@ApiModelProperty(name = "currentStep")
	@JsonProperty("currentStep")
	public DecisionStepDTO getCurrentStep() {
		return currentStep;
	}

	@ApiModelProperty(name = "futureSteps")
	@JsonProperty("futureSteps")
	public List<DecisionFlowInstanceDTO> getFutureSteps() {
		return futureSteps;
	}

	private DecisionFlowInstanceDTO(final Builder builder) {
		this.uuid = builder.uuid;
		this.name = builder.name;
		this.description = builder.description;
		this.completedSteps = builder.completedSteps;
		this.currentStep = builder.currentStep;
		this.futureSteps = builder.futureSteps;
	}

	public static final class Builder {
		private String uuid;
		private String name;
		private String description;
		private List<DecisionStepInstanceDTO> completedSteps;
		private DecisionStepDTO currentStep;
		private List<DecisionFlowInstanceDTO> futureSteps;

		public Builder() {
		}

		public Builder withUuid(final String uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder withName(final String name) {
			this.name = name;
			return this;
		}

		public Builder withDescription(final String description) {
			this.description = description;
			return this;
		}

		public Builder withCompletedSteps(final List<DecisionStepInstanceDTO> completedSteps) {
			this.completedSteps = completedSteps;
			return this;
		}

		public Builder withCurrentStep(final DecisionStepDTO currentStep) {
			this.currentStep = currentStep;
			return this;
		}

		public Builder withFutureSteps(final List<DecisionFlowInstanceDTO> futureSteps) {
			this.futureSteps = futureSteps;
			return this;
		}

		public DecisionFlowInstanceDTO build() {
			return new DecisionFlowInstanceDTO(this);
		}
	}
}
