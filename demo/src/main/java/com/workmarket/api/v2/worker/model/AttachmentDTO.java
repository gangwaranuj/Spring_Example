package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Attachment")
@JsonDeserialize(builder = AttachmentDTO.Builder.class)
public class AttachmentDTO {
	private final String uuid;
	private final String uri;
	private final String description;
	private final String name;
	private final String mimeType;
	private final long byteSize;

	public AttachmentDTO(
		final String uuid,
		final String uri,
		final String description,
		final String name,
		final String mimeType,
		final long byteSize) {
		this.uuid = uuid;
		this.uri = uri;
		this.description = description;
		this.name = name;
		this.mimeType = mimeType;
		this.byteSize = byteSize;
	}

	private AttachmentDTO(Builder builder) {
		uuid = builder.uuid;
		uri = builder.uri;
		description = builder.description;
		name = builder.name;
		mimeType = builder.mimeType;
		byteSize = builder.byteSize;
	}

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "uri")
	@JsonProperty("uri")
	public String getUri() {
		return uri;
	}

	@ApiModelProperty(name = "description")
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@ApiModelProperty(name = "mimeType")
	@JsonProperty("mimeType")
	public String getMimeType() {
		return mimeType;
	}

	@ApiModelProperty(name = "byteSize")
	@JsonProperty("byteSize")
	public long getByteSize() {
		return byteSize;
	}

	public static final class Builder {
		private String uuid;
		private String uri;
		private String description;
		private String name;
		private String mimeType;
		private long byteSize;

		public Builder() {
		}

		public Builder(AttachmentDTO copy) {
			this.uuid = copy.uuid;
			this.uri = copy.uri;
			this.description = copy.description;
			this.name = copy.name;
			this.mimeType = copy.mimeType;
			this.byteSize = copy.byteSize;
		}

		@JsonProperty("uuid")
		public Builder withUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("uri")
		public Builder withUri(String uri) {
			this.uri = uri;
			return this;
		}

		@JsonProperty("description")
		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("name")
		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("mimeType")
		public Builder withMimeType(String mimeType) {
			this.mimeType = mimeType;
			return this;
		}

		@JsonProperty("byteSize")
		public Builder withByteSize(long byteSize) {
			this.byteSize = byteSize;
			return this;
		}

		public AttachmentDTO build() {
			return new AttachmentDTO(this);
		}
	}
}
