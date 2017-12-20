package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("SaveCustomFields")
@JsonDeserialize(builder = SaveCustomFieldsDTO.Builder.class)
public class SaveCustomFieldsDTO {

	private final List<CustomFieldGroupDTO> customFields;

	private SaveCustomFieldsDTO(Builder builder) {
		customFields = builder.customFields;
	}

	@ApiModelProperty(name = "customFields")
	@JsonProperty("customFields")
	public List<CustomFieldGroupDTO> getCustomFields() {
		return customFields;
	}

	public static final class Builder {
		private List<CustomFieldGroupDTO> customFields;

		public Builder() {
		}

		public Builder(SaveCustomFieldsDTO copy) {
			this.customFields = copy.customFields;
		}

		@JsonProperty("customFields")
		public Builder withCustomFields(List<CustomFieldGroupDTO> customFields) {
			this.customFields = customFields;
			return this;
		}

		public SaveCustomFieldsDTO build() {
			return new SaveCustomFieldsDTO(this);
		}
	}
}
