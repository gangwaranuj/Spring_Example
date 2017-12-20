package com.workmarket.api.v2.model.enterprise.decisionflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("DeciderMatcher")
@JsonDeserialize(builder = DeciderMatcherDTO.Builder.class)
public class DeciderMatcherDTO {

	private String uuid;
	private String name;
	private String description;
	private DeciderMatcherType type;
	private String autocompleteTarget;

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

	@ApiModelProperty(name = "type")
	@JsonProperty("type")
	public DeciderMatcherType getType() {
		return type;
	}

	@ApiModelProperty(name = "autocompleteTarget")
	@JsonProperty("autocompleteTarget")
	public String getAutocompleteTarget() {
		return autocompleteTarget;
	}

	private DeciderMatcherDTO() {}

	private DeciderMatcherDTO(final Builder builder) {
		this.uuid = builder.uuid;
		this.name = builder.name;
		this.description = builder.description;
		this.type = builder.type;
		this.autocompleteTarget = builder.autocompleteTarget;
	}

	public static final class Builder {
		private String uuid;
		private String name;
		private String description;
		private DeciderMatcherType type;
		private String autocompleteTarget;

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

		public Builder withType(final DeciderMatcherType type) {
			this.type = type;
			return this;
		}

		public Builder withAutocompleteTarget(final String autocompleteTarget) {
			this.autocompleteTarget = autocompleteTarget;
			return this;
		}

		public DeciderMatcherDTO build() {
			return new DeciderMatcherDTO(this);
		}
	}
}
