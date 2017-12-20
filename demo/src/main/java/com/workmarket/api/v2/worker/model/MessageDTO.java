package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Message")
@JsonDeserialize(builder = MessageDTO.Builder.class)
public class MessageDTO {

	private final Long createdDate;
	private final String text;
	private final String createdBy;
	private final Boolean isQuestion;
	private final String visibility; // activeWorkerOnly | public

	private MessageDTO(Builder builder) {
		createdDate = builder.createdDate;
		text = builder.text;
		createdBy = builder.createdBy;
		isQuestion = builder.isQuestion;
		visibility = builder.visibility;
	}

	@ApiModelProperty(name = "createdDate")
	@JsonProperty("createdDate")
	public Long getCreatedDate() {
		return createdDate;
	}

	@ApiModelProperty(name = "text")
	@JsonProperty("text")
	public String getText() {
		return text;
	}

	@ApiModelProperty(name = "createdBy")
	@JsonProperty("createdBy")
	public String getCreatedBy() {
		return createdBy;
	}

	@ApiModelProperty(name = "question")
	@JsonProperty("question")
	public Boolean getQuestion() {
		return isQuestion;
	}

	@ApiModelProperty(name = "visibility")
	@JsonProperty("visibility")
	public String getVisibility() {
		return visibility;
	}

	public static final class Builder {
		private Long createdDate;
		private String text;
		private String createdBy;
		private Boolean isQuestion;
		private String visibility;

		public Builder() {
		}

		public Builder(MessageDTO copy) {
			this.createdDate = copy.createdDate;
			this.text = copy.text;
			this.createdBy = copy.createdBy;
			this.isQuestion = copy.isQuestion;
			this.visibility = copy.visibility;
		}

		@JsonProperty("createdDate")
		public Builder withCreatedDate(Long createdDate) {
			this.createdDate = createdDate;
			return this;
		}

		@JsonProperty("text")
		public Builder withText(String text) {
			this.text = text;
			return this;
		}

		@JsonProperty("createdBy")
		public Builder withCreatedBy(String createdBy) {
			this.createdBy = createdBy;
			return this;
		}

		@JsonProperty("isQuestion")
		public Builder withIsQuestion(Boolean isQuestion) {
			this.isQuestion = isQuestion;
			return this;
		}

		@JsonProperty("visibility")
		public Builder withVisibility(String visibility) {
			this.visibility = visibility;
			return this;
		}

		public MessageDTO build() {
			return new MessageDTO(this);
		}
	}
}
