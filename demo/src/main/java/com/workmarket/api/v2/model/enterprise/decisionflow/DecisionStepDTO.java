package com.workmarket.api.v2.model.enterprise.decisionflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("DecisionStep")
@JsonDeserialize(builder = DecisionStepDTO.Builder.class)
public class DecisionStepDTO {

	private String uuid;
	private String name;
	private int sequence;
	private QuorumType quorumType;
	private String decisionVerb;
	private List<DecisionDTO> decisions;

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

	@ApiModelProperty(name = "sequence")
	@JsonProperty("sequence")
	public int getSequence() {
		return sequence;
	}

	@ApiModelProperty(name = "quorumType")
	@JsonProperty("quorumType")
	public QuorumType getQuorumType() {
		return quorumType;
	}

	@ApiModelProperty(name = "decisionVerb")
	@JsonProperty("decisionVerb")
	public String getDecisionVerb() {
		return decisionVerb;
	}

	@ApiModelProperty(name = "decisions")
	@JsonProperty("decisions")
	public List<DecisionDTO> getDecisions() {
		return decisions;
	}

	private DecisionStepDTO() {}

	private DecisionStepDTO(final Builder builder) {
		this.uuid = builder.uuid;
		this.name = builder.name;
		this.sequence = builder.sequence;
		this.quorumType = builder.quorumType;
		this.decisionVerb = builder.decisionVerb;
		this.decisions = builder.decisions;
	}

	public static final class Builder {
		private String uuid;
		private String name;
		private int sequence;
		private QuorumType quorumType;
		private String decisionVerb;
		private List<DecisionDTO> decisions;

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

		public Builder withUuid(final int sequence) {
			this.sequence = sequence;
			return this;
		}

		public Builder withQuorumType(final QuorumType quorumType) {
			this.quorumType = quorumType;
			return this;
		}

		public Builder withDecisionVerb(final String decisionVerb) {
			this.decisionVerb = decisionVerb;
			return this;
		}

		public Builder withDecisions(final List<DecisionDTO> decisions) {
			this.decisions = decisions;
			return this;
		}

		public DecisionStepDTO build() {
			return new DecisionStepDTO(this);
		}
	}
}
