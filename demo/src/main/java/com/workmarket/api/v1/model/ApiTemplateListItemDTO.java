package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "TemplateListItem")
@JsonDeserialize(builder = ApiTemplateListItemDTO.Builder.class)
public class ApiTemplateListItemDTO {
	private final String templateId;
	private final String name;
	private final Long clientId;

	private ApiTemplateListItemDTO(Builder builder) {
		templateId = builder.templateId;
		name = builder.name;
		clientId = builder.clientId;
	}

	@ApiModelProperty(name = "template_id")
	@JsonProperty("template_id")
	public String getTemplateId() {
		return templateId;
	}

	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@ApiModelProperty(name = "client_id")
	@JsonProperty("client_id")
	public Long getClientId() {
		return clientId;
	}

	public static final class Builder {
		private String templateId;
		private String name;
		private Long clientId;

		public Builder() {
		}

		public Builder(ApiTemplateListItemDTO copy) {
			this.templateId = copy.templateId;
			this.name = copy.name;
			this.clientId = copy.clientId;
		}

		@JsonProperty("template_id")
		public Builder withTemplateId(String templateId) {
			this.templateId = templateId;
			return this;
		}

		@JsonProperty("name")
		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("client_id")
		public Builder withClientId(final Long clientId) {
			this.clientId = clientId;
			return this;
		}


		public ApiTemplateListItemDTO build() {
			return new ApiTemplateListItemDTO(this);
		}
	}
}
