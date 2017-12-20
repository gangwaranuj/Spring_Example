package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Document")
@JsonDeserialize(builder = DocumentDTO.Builder.class)
public class DocumentDTO {
	private long id;
	private String uuid;
	private String name;
	private String description;
	private boolean uploaded;
	private String visibilityType;

	private DocumentDTO(Builder builder) {
		this.id = builder.id;
		this.uuid = builder.uuid;
		this.name = builder.name;
		this.description = builder.description;
		this.uploaded = builder.uploaded;
		this.visibilityType = builder.visibilityType;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public long getId() {
		return id;
	}

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

	@ApiModelProperty(name = "uploaded")
	@JsonProperty("uploaded")
	public boolean isUploaded() {
		return uploaded;
	}

	@ApiModelProperty(name = "visibilityType")
	@JsonProperty("visibilityType")
	public String getVisibilityType() {
		return visibilityType;
	}

	public static class Builder implements AbstractBuilder<DocumentDTO> {
		private long id;
		private String uuid;
		private String name;
		private String description;
		private boolean uploaded = false;
		private String visibilityType;

		public Builder(DocumentDTO documentDTO) {
			this.id = documentDTO.id;
			this.uuid = documentDTO.uuid;
			this.name = documentDTO.name;
			this.description = documentDTO.description;
			this.uploaded = documentDTO.uploaded;
			this.visibilityType = documentDTO.visibilityType;
		}

		public Builder() {}

		@JsonProperty("id") public Builder setId(long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("uuid") public Builder setUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("name") public Builder setName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("description") public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("uploaded") public Builder setUploaded(boolean uploaded) {
			this.uploaded = uploaded;
			return this;
		}

		@JsonProperty("visibilityType") public Builder setVisibilityType(String visibilityType) {
			this.visibilityType = visibilityType;
			return this;
		}

		@Override
		public DocumentDTO build() {
			return new DocumentDTO(this);
		}
	}
}
