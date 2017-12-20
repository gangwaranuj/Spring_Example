package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "CustomField")
@JsonDeserialize(builder = CustomFieldDTO.Builder.class)
public class CustomFieldDTO {
	private final long id;
	private final String name;
	private final String value;
	private final String defaultValue;
	private final boolean visibleToResource;
	private final boolean visibleToOwner;
	private final boolean required;
	private final String type;
	private final boolean readOnly;
	private final boolean showOnPrintout;
	private final boolean showInAssignmentHeader;
	private final boolean showOnSentStatus;

	private CustomFieldDTO(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.value = builder.value;
		this.defaultValue = builder.defaultValue;
		this.visibleToResource = builder.visibleToResource;
		this.visibleToOwner = builder.visibleToOwner;
		this.required = builder.required;
		this.type = builder.type;
		this.readOnly = builder.readOnly;
		this.showOnPrintout = builder.showOnPrintout;
		this.showInAssignmentHeader = builder.showInAssignmentHeader;
		this.showOnSentStatus = builder.showOnSentStatus;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@ApiModelProperty(name = "value")
	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	@ApiModelProperty(name = "defaultValue")
	@JsonProperty("defaultValue")
	public String getDefaultValue() {
		return defaultValue;
	}

	@ApiModelProperty(name = "visibleToResource")
	@JsonProperty("visibleToResource")
	public Boolean isVisibleToResource() {
		return visibleToResource;
	}

	@ApiModelProperty(name = "visibleToOwner")
	@JsonProperty("visibleToOwner")
	public Boolean isVisibleToOwner() {
		return visibleToOwner;
	}

	@ApiModelProperty(name = "required")
	@JsonProperty("required")
	public Boolean isRequired() {
		return required;
	}

	@ApiModelProperty(name = "type")
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@ApiModelProperty(name = "readOnly")
	@JsonProperty("readOnly")
	public Boolean isReadOnly() {
		return readOnly;
	}

	@ApiModelProperty(name = "showOnPrintout")
	@JsonProperty("showOnPrintout")
	public Boolean isShowOnPrintout() {
		return showOnPrintout;
	}

	@ApiModelProperty(name = "showInAssignmentHeader")
	@JsonProperty("showInAssignmentHeader")
	public Boolean isShowInAssignmentHeader() {
		return showInAssignmentHeader;
	}

	@ApiModelProperty(name = "showOnSentStatus")
	@JsonProperty("showOnSentStatus")
	public Boolean isShowOnSentStatus() {
		return showOnSentStatus;
	}

	public static class Builder implements AbstractBuilder<CustomFieldDTO> {
		private long id;
		private String name;
		private String value;
		private String defaultValue;
		private boolean visibleToResource = false;
		private boolean visibleToOwner = false;
		private boolean required = false;
		private String type;
		private boolean readOnly = false;
		private boolean showOnPrintout = false;
		private boolean showInAssignmentHeader = false;
		private boolean showOnSentStatus = false;

		public Builder() {}

		public Builder(CustomFieldDTO field) {
			this.id = field.id;
			this.name = field.name;
			this.value = field.value;
			this.defaultValue = field.defaultValue;
			this.visibleToResource = field.visibleToResource;
			this.visibleToOwner = field.visibleToOwner;
			this.required = field.required;
			this.type = field.type;
			this.readOnly = field.readOnly;
			this.showOnPrintout = field.showOnPrintout;
			this.showInAssignmentHeader = field.showInAssignmentHeader;
			this.showOnSentStatus = field.showOnSentStatus;
		}

		@JsonProperty("id") public Builder setId(long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("name") public Builder setName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("value") public Builder setValue(String value) {
			this.value = value;
			return this;
		}

		@JsonProperty("defaultValue") public Builder setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		@JsonProperty("visibleToResource") public Builder setVisibleToResource(boolean visibleToResource) {
			this.visibleToResource = visibleToResource;
			return this;
		}

		@JsonProperty("visibleToOwner") public Builder setVisibleToOwner(boolean visibleToOwner) {
			this.visibleToOwner = visibleToOwner;
			return this;
		}

		@JsonProperty("required") public Builder setRequired(boolean required) {
			this.required = required;
			return this;
		}

		@JsonProperty("type") public Builder setType(String type) {
			this.type = type;
			return this;
		}

		@JsonProperty("readOnly") public Builder setReadOnly(boolean readOnly) {
			this.readOnly = readOnly;
			return this;
		}

		@JsonProperty("showOnPrintout") public Builder setShowOnPrintout(boolean showOnPrintout) {
			this.showOnPrintout = showOnPrintout;
			return this;
		}

		@JsonProperty("showInAssignmentHeader") public Builder setShowInAssignmentHeader(boolean showInAssignmentHeader) {
			this.showInAssignmentHeader = showInAssignmentHeader;
			return this;
		}

		@JsonProperty("showOnSentStatus") public Builder setShowOnSentStatus(boolean showOnSentStatus) {
			this.showOnSentStatus = showOnSentStatus;
			return this;
		}

		public CustomFieldDTO build() {
			return new CustomFieldDTO(this);
		}
	}
}
