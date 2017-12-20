package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("Question")
@JsonDeserialize(builder = QuestionDTO.Builder.class)
public class QuestionDTO {

	@NotNull
	private final String text;

	private QuestionDTO(Builder builder) {
		text = builder.text;
	}

	@ApiModelProperty(name = "text")
	@JsonProperty("text")
	public String getText() {
		return text;
	}

	public static final class Builder {
		private String text;

		public Builder() {
		}

		public Builder(QuestionDTO copy) {
			this.text = copy.text;
		}

		@JsonProperty("text")
		public Builder withText(String text) {
			this.text = text;
			return this;
		}

		public QuestionDTO build() {
			return new QuestionDTO(this);
		}
	}
}
