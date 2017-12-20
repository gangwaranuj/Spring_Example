package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModelProperty;

@JsonDeserialize(builder = SettingsDTO.Builder.class)
public class SettingsDTO {
	private final String templateId;
	private final Long labelId;

	private SettingsDTO(Builder builder) {
		this.templateId = builder.templateId;
		this.labelId = builder.labelId;
	}

	@ApiModelProperty(name = "templateId")
	@JsonProperty("templateId")
	public String getTemplateId() {
		return templateId;
	}

	@ApiModelProperty(name = "labelId")
	@JsonProperty("labelId")
	public Long getLabelId() {
		return labelId;
	}

	public static class Builder implements AbstractBuilder<SettingsDTO> {
		private String templateId;
		private Long labelId;

		public Builder(SettingsDTO settingsDTO) {
			this.templateId = settingsDTO.templateId;
			this.labelId = settingsDTO.labelId;
		}

		public Builder() {}

		@JsonProperty("templateId")
		public Builder setTemplateId(String templateId) {
			this.templateId = templateId;
			return this;
		}

		@JsonProperty("labelId")
		public Builder setLabelId(Long labelId) {
			this.labelId = labelId;
			return this;
		}

		@Override
		public SettingsDTO build() {
			return new SettingsDTO(this);
		}
	}
}
