package com.workmarket.api.v2.model.enterprise.decisionflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Decision")
@JsonDeserialize(builder = DecisionDTO.Builder.class)
public class DecisionDTO {

	private String uuid;
	private DeciderDTO decider;
	private DecisionResult decisionResult;

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "decider")
	@JsonProperty("decider")
	public DeciderDTO getDecider() {
		return decider;
	}

	@ApiModelProperty(name = "decisionResult")
	@JsonProperty("decisionResult")
	public DecisionResult getDecisionResult() {
		return decisionResult;
	}

	private DecisionDTO() {}

	private DecisionDTO(final Builder builder) {
		this.uuid = builder.uuid;
		this.decider = builder.decider;
		this.decisionResult = builder.decisionResult;
	}

	public static final class Builder {
		private String uuid;
		private DeciderDTO decider;
		private DecisionResult decisionResult;

		public Builder() {
		}

		public Builder withUuid(final String uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder withDecider(final DeciderDTO decider) {
			this.decider = decider;
			return this;
		}

		public Builder withDecisionResult(final DecisionResult decisionResult) {
			this.decisionResult = decisionResult;
			return this;
		}

		public DecisionDTO build() {
			return new DecisionDTO(this);
		}
	}
}
