package com.workmarket.api.v2.model.enterprise.decisionflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Decider")
@JsonDeserialize(builder = DeciderDTO.Builder.class)
public class DeciderDTO {

	private String uuid;
	private String criteria;
	private DeciderMatcherDTO deciderMatcher;

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "criteria")
	@JsonProperty("criteria")
	public String getCriteria() {
		return criteria;
	}

	@ApiModelProperty(name = "deciderMatcher")
	@JsonProperty("deciderMatcher")
	public DeciderMatcherDTO getDeciderMatcher() {
		return deciderMatcher;
	}

	private DeciderDTO () {}

	private DeciderDTO(final Builder builder) {
		this.uuid = builder.uuid;
		this.criteria = builder.criteria;
		this.deciderMatcher = builder.deciderMatcher;
	}

	public static final class Builder {
		private String uuid;
		private String criteria;
		private DeciderMatcherDTO deciderMatcher;

		public Builder() {
		}

		public Builder withUuid(final String uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder withCriteria(final String criteria) {
			this.criteria = criteria;
			return this;
		}

		public Builder withDeciderMatcher(final DeciderMatcherDTO deciderMatcher) {
			this.deciderMatcher = deciderMatcher;
			return this;
		}

		public DeciderDTO build() {
			return new DeciderDTO(this);
		}
	}
}
