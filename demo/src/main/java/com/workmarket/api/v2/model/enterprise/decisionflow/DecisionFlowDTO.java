package com.workmarket.api.v2.model.enterprise.decisionflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

@ApiModel("DecisionFlow")
@JsonDeserialize(builder = DecisionFlowDTO.Builder.class)
public class DecisionFlowDTO {

	private String uuid;
	private String namespace;
	private String name;
	private String description;
	private Date activatedOn;
	private List<DecisionStepDTO> decisionSteps;

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "namespace")
	@JsonProperty("namespace")
	public String getNamespace() {
		return namespace;
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

	@ApiModelProperty(name = "activatedOn")
	@JsonProperty("activatedOn")
	public Date getActivatedOn() {
		return activatedOn;
	}

	@ApiModelProperty(name = "decisionSteps")
	@JsonProperty("decisionSteps")
	public List<DecisionStepDTO> getDecisionSteps() {
		return decisionSteps;
	}

	private DecisionFlowDTO() {}

	private DecisionFlowDTO(final Builder builder) {
		this.uuid = builder.uuid;
		this.namespace = builder.namespace;
		this.name = builder.name;
		this.description = builder.description;
		this.activatedOn = builder.activatedOn;
		this.decisionSteps = builder.decisionSteps;
	}

	public static final class Builder {
		private String uuid;
		private String namespace;
		private String name;
		private String description;
		private Date activatedOn;
		private List<DecisionStepDTO> decisionSteps;

		public Builder() {
		}

		public Builder withUuid(final String uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder withNamespace(final String namespace) {
			this.namespace = namespace;
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

		public Builder withActivatedOn(final Date activatedOn) {
			this.activatedOn = activatedOn;
			return this;
		}

		public Builder withDecisionSteps(final List<DecisionStepDTO> decisionSteps) {
			this.decisionSteps = decisionSteps;
			return this;
		}

		public DecisionFlowDTO build() {
			return new DecisionFlowDTO(this);
		}
	}
}
