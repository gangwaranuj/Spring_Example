package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "History")
@JsonDeserialize(builder = ApiHistoryDTO.Builder.class)
public class ApiHistoryDTO {

	private final Long date;
	private final String text;
	private final String setBy;
	private final String resolvedBy;
	private final Long labelId;
	private final String labelName;
	private final Long createdOn;

	private ApiHistoryDTO(Builder builder) {
		date = builder.date;
		text = builder.text;
		setBy = builder.setBy;
		resolvedBy = builder.resolvedBy;
		labelId = builder.labelId;
		labelName = builder.labelName;
		createdOn = builder.createdOn;
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

	@ApiModelProperty(name = "set_by")
	@JsonProperty("set_by")
	public String getSetBy() {
		return setBy;
	}

	@ApiModelProperty(name = "resolved_by")
	@JsonProperty("resolved_by")
	public String getResolvedBy() {
		return resolvedBy;
	}

	@ApiModelProperty(name = "label_id")
	@JsonProperty("label_id")
	public Long getLabelId() {
		return labelId;
	}

	@ApiModelProperty(name = "label_name")
	@JsonProperty("label_name")
	public String getLabelName() {
		return labelName;
	}

	@ApiModelProperty(name = "created_on")
	@JsonProperty("created_on")
	public Long getCreatedOn() {
		return createdOn;
	}

	public static final class Builder {
		private Long date;
		private String text;
		private String setBy;
		private String resolvedBy;
		private Long labelId;
		private String labelName;
		private Long createdOn;

		public Builder() {
		}

		public Builder(ApiHistoryDTO copy) {
			this.date = copy.date;
			this.text = copy.text;
			this.setBy = copy.setBy;
			this.resolvedBy = copy.resolvedBy;
			this.labelId = copy.labelId;
			this.labelName = copy.labelName;
			this.createdOn = copy.createdOn;
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

		@JsonProperty("set_by")
		public Builder withSetBy(String setBy) {
			this.setBy = setBy;
			return this;
		}

		@JsonProperty("resolved_by")
		public Builder withResolvedBy(String resolvedBy) {
			this.resolvedBy = resolvedBy;
			return this;
		}

		@JsonProperty("label_id")
		public Builder withLabelId(Long labelId) {
			this.labelId = labelId;
			return this;
		}

		@JsonProperty("label_name")
		public Builder withLabelName(String labelName) {
			this.labelName = labelName;
			return this;
		}

		@JsonProperty("created_on")
		public Builder withCreatedOn(Long createdOn) {
			this.createdOn = createdOn;
			return this;
		}

		public ApiHistoryDTO build() {
			return new ApiHistoryDTO(this);
		}
	}
}