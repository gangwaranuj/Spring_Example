package com.workmarket.api.v2.model.enterprise.decisionflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("DecisionStepInstance")
@JsonDeserialize(builder = DecisionStepInstanceDTO.Builder.class)
public class DecisionStepInstanceDTO {

	private String uuid;
	private QuorumType quorumType;
	private DecisionResult decisionResult;
	private List<DecisionDTO> decisions;

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "quorumType")
	@JsonProperty("quorumType")
	public QuorumType getQuorumType() {
		return quorumType;
	}

	@ApiModelProperty(name = "decisionResult")
	@JsonProperty("decisionResult")
	public DecisionResult getDecisionResult() {
		return decisionResult;
	}

	@ApiModelProperty(name = "decisions")
	@JsonProperty("decisions")
	public List<DecisionDTO> getDecisions() {
		return decisions;
	}

	private DecisionStepInstanceDTO() {}

	private DecisionStepInstanceDTO(final Builder builder) {
		this.uuid = builder.uuid;
		this.quorumType = builder.quorumType;
		this.decisionResult = builder.decisionResult;
		this.decisions = builder.decisions;
	}

	public static final class Builder {
		private String uuid;
		private QuorumType quorumType;
		private DecisionResult decisionResult;
		private List<DecisionDTO> decisions;

		public Builder() {
		}

		public Builder withUuid(final String uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder withQuorumType(final QuorumType quorumType) {
			this.quorumType = quorumType;
			return this;
		}

		public Builder withDecisionResult(final DecisionResult decisionResult) {
			this.decisionResult = decisionResult;
			return this;
		}

		public Builder withDecisions(final List<DecisionDTO> decisions) {
			this.decisions = decisions;
			return this;
		}

		public DecisionStepInstanceDTO build() {
			return new DecisionStepInstanceDTO(this);
		}
	}
}
