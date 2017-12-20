package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Sets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.workmarket.domains.model.settings.SettingsActionTypes;

import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "SettingsCompleteness")
@JsonDeserialize(builder = SettingsCompletenessDTO.Builder.class)
public class SettingsCompletenessDTO {

	private Float percentage;
	private Set<SettingsActionTypes> completedActions;
	private Set<SettingsActionTypes> missingActions;

	public SettingsCompletenessDTO(SettingsCompletenessDTO.Builder builder) {
		this.completedActions = builder.completedActions;
		this.missingActions = builder.missingActions;
		this.percentage = builder.percentage;
	}

	@ApiModelProperty(name = "percentage")
	@JsonProperty("percentage")
	public Float getPercentage() {
		return percentage;
	}

	@ApiModelProperty(name = "completedActions")
	@JsonProperty("completedActions")
	public Set<SettingsActionTypes> getCompletedActions() {
		return completedActions;
	}

	@ApiModelProperty(name = "missingActions")
	@JsonProperty("missingActions")
	public Set<SettingsActionTypes> getMissingActions() {
		return missingActions;
	}

	public static class Builder {

		Float percentage;
		private Set<SettingsActionTypes> completedActions = Sets.newHashSet();
		private Set<SettingsActionTypes> missingActions = Sets.newHashSet();

		public Builder() {}

		public Builder(SettingsCompletenessDTO settingsCompletenessDTO) {
			this.completedActions = settingsCompletenessDTO.getCompletedActions();
			this.missingActions = settingsCompletenessDTO.getMissingActions();
		}

		@JsonProperty("percentage") public Builder setPercentage(Float percentage) {
			this.percentage = percentage;
			return this;
		}

		public Builder addToCompletedActions(SettingsActionTypes type) {
			completedActions.add(type);
			return this;
		}

		@JsonProperty("completedActions") public Builder setCompletedActions(final Set<SettingsActionTypes> completedActions) {
			this.completedActions = completedActions;
			return this;
		}

		public Builder addToMissingActions(SettingsActionTypes type) {
			missingActions.add(type);
			return this;
		}

		@JsonProperty("missingActions") public Builder setMissingActions(final Set<SettingsActionTypes> missingActions) {
			this.missingActions = missingActions;
			return this;
		}

		public SettingsCompletenessDTO build() {
			/*if (CollectionUtils.isNotEmpty(this.completed) || CollectionUtils.isNotEmpty(this.missing)) {
				percentage = 100f * this.completed.size() / (this.completed.size() + this.missing.size());
			}*/
			return new SettingsCompletenessDTO(this);
		}
	}
}
