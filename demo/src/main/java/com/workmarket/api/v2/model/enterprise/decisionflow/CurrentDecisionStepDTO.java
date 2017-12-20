package com.workmarket.api.v2.model.enterprise.decisionflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("CurrentDecisionStep")
@JsonDeserialize(builder = CurrentDecisionStepDTO.Builder.class)
public class CurrentDecisionStepDTO {

	private final String decisionStepUuid;
	private final List<DecisionDTO> decisions;

	private CurrentDecisionStepDTO(final Builder builder) {
		this.decisionStepUuid = builder.decisionStepUuid;
		this.decisions = builder.decisions;
	}

	@ApiModelProperty(name = "decisionStepUuid")
	@JsonProperty("decisionStepUuid")
	public String getDecisionStepUuid() {
		return decisionStepUuid;
	}

	@ApiModelProperty(name = "decisions")
	@JsonProperty("decisions")
	public List<DecisionDTO> getDecisions() {
		return decisions;
	}

	public static final class Builder {
		private String decisionStepUuid;
		private List<DecisionDTO> decisions;

		public Builder() {
		}

		public Builder withDecisionStepUuid(final String decisionStepUuid) {
			this.decisionStepUuid = decisionStepUuid;
			return this;
		}

		public Builder withDecisions(final List<DecisionDTO> decisions) {
			this.decisions = decisions;
			return this;
		}

		public CurrentDecisionStepDTO build() {
			return new CurrentDecisionStepDTO(this);
		}
	}
}
