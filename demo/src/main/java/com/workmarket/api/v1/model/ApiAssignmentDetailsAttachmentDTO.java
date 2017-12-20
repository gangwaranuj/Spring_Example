package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

// Slightly different than the ApiAttachmentDTO, ugh..
@ApiModel(value = "AssignmentDetailsAttachment")
@JsonDeserialize(builder = ApiAssignmentDetailsAttachmentDTO.Builder.class)
public class ApiAssignmentDetailsAttachmentDTO {
	private final String uuid;
	private final String name;
	private final String description;
	private final String relativeUri;

	private ApiAssignmentDetailsAttachmentDTO(Builder builder) {
		uuid = builder.uuid;
		name = builder.name;
		description = builder.description;
		relativeUri = builder.relativeUri;
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

	@ApiModelProperty(name = "relative_uri")
	@JsonProperty("relative_uri")
	public String getRelativeUri() {
		return relativeUri;
	}

	public static final class Builder {
		private String uuid;
		private String name;
		private String description;
		private String relativeUri;

		public Builder() {
		}

		public Builder(ApiAssignmentDetailsAttachmentDTO copy) {
			this.uuid = copy.uuid;
			this.name = copy.name;
			this.description = copy.description;
			this.relativeUri = copy.relativeUri;
		}

		@JsonProperty("uuid")
		public Builder withUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("name")
		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("description")
		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("relativeUri")
		public Builder withRelativeUri(String relativeUri) {
			this.relativeUri = relativeUri;
			return this;
		}

		public ApiAssignmentDetailsAttachmentDTO build() {
			return new ApiAssignmentDetailsAttachmentDTO(this);
		}
	}
}
