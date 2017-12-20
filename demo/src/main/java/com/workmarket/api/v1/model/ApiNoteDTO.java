package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Note")
@JsonDeserialize(builder = ApiNoteDTO.Builder.class)
public class ApiNoteDTO {

	private final Long date;
	private final String text;
	private final String createdBy;
	private final Boolean isPrivate;
	private final Boolean isPrivileged;

	private ApiNoteDTO(Builder builder) {
		date = builder.date;
		text = builder.text;
		createdBy = builder.createdBy;
		isPrivate = builder.isPrivate;
		isPrivileged = builder.isPrivileged;
	}

	@ApiModelProperty(name = "date")
	@JsonProperty("date")
	public Long getDate() {
		return date;
	}

	@ApiModelProperty(name = "text")
	@JsonProperty("text")
	public String getText() {
		return text;
	}

	@ApiModelProperty(name = "created_by")
	@JsonProperty("created_by")
	public String getCreatedBy() {
		return createdBy;
	}

	@ApiModelProperty(name = "is_private")
	@JsonProperty("is_private")
	public Boolean getPrivate() {
		return isPrivate;
	}

	@ApiModelProperty(name = "is_privileged")
	@JsonProperty("is_privileged")
	public Boolean getPrivileged() {
		return isPrivileged;
	}

	public static final class Builder {
		private Long date;
		private String text;
		private String createdBy;
		private Boolean isPrivate;
		private Boolean isPrivileged;

		public Builder() {
		}

		public Builder(ApiNoteDTO copy) {
			this.date = copy.date;
			this.text = copy.text;
			this.createdBy = copy.createdBy;
			this.isPrivate = copy.isPrivate;
			this.isPrivileged = copy.isPrivileged;
		}

		@JsonProperty("date")
		public Builder withDate(Long date) {
			this.date = date;
			return this;
		}

		@JsonProperty("text")
		public Builder withText(String text) {
			this.text = text;
			return this;
		}

		@JsonProperty("created_by")
		public Builder withCreatedBy(String createdBy) {
			this.createdBy = createdBy;
			return this;
		}

		@JsonProperty("is_private")
		public Builder withIsPrivate(Boolean isPrivate) {
			this.isPrivate = isPrivate;
			return this;
		}

		@JsonProperty("is_privileged")
		public Builder withIsPrivileged(Boolean isPrivileged) {
			this.isPrivileged = isPrivileged;
			return this;
		}

		public ApiNoteDTO build() {
			return new ApiNoteDTO(this);
		}
	}
}