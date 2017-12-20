package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Attachment")
@JsonDeserialize(builder = ApiAttachmentDTO.Builder.class)
public class ApiAttachmentDTO {
	private final String uuid;
	private final String filename;
	private final String description;
	private final Integer size;
	private final String attachment;

	private ApiAttachmentDTO(Builder builder) {
		uuid = builder.uuid;
		filename = builder.filename;
		description = builder.description;
		size = builder.size;
		attachment = builder.attachment;
	}

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "filename")
	@JsonProperty("filename")
	public String getFilename() {
		return filename;
	}

	@ApiModelProperty(name = "description")
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@ApiModelProperty(name = "size")
	@JsonProperty("size")
	public Integer getSize() {
		return size;
	}

	@ApiModelProperty(name = "attachment")
	@JsonProperty("attachment")
	public String getAttachment() {
		return attachment;
	}

	public static final class Builder {
		private String uuid;
		private String filename;
		private String description;
		private Integer size;
		private String attachment;

		public Builder() {
		}

		public Builder(ApiAttachmentDTO copy) {
			this.uuid = copy.uuid;
			this.filename = copy.filename;
			this.description = copy.description;
			this.size = copy.size;
			this.attachment = copy.attachment;
		}

		@JsonProperty("uuid")
		public Builder withUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("filename")
		public Builder withFilename(String filename) {
			this.filename = filename;
			return this;
		}

		@JsonProperty("description")
		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("size")
		public Builder withSize(Integer size) {
			this.size = size;
			return this;
		}

		@JsonProperty("attachment")
		public Builder withAttachment(String attachment) {
			this.attachment = attachment;
			return this;
		}

		public ApiAttachmentDTO build() {
			return new ApiAttachmentDTO(this);
		}
	}
}
