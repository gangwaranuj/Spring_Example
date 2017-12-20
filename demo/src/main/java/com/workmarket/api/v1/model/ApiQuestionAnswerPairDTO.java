package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "QuestionAnswerPair")
@JsonDeserialize(builder = ApiQuestionAnswerPairDTO.Builder.class)
public class ApiQuestionAnswerPairDTO {
	private final Long id;
	private final String question;
	private final String answer;

	private ApiQuestionAnswerPairDTO(Builder builder) {
		id = builder.id;
		question = builder.question;
		answer = builder.answer;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "question")
	@JsonProperty("question")
	public String getQuestion() {
		return question;
	}

	@ApiModelProperty(name = "answer")
	@JsonProperty("answer")
	public String getAnswer() {
		return answer;
	}

	public static final class Builder {
		private Long id;
		private String question;
		private String answer;

		public Builder() {
		}

		public Builder(ApiQuestionAnswerPairDTO copy) {
			this.id = copy.id;
			this.question = copy.question;
			this.answer = copy.answer;
		}

		@JsonProperty("id")
		public Builder withId(Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("question")
		public Builder withQuestion(String question) {
			this.question = question;
			return this;
		}

		@JsonProperty("answer")
		public Builder withAnswer(String answer) {
			this.answer = answer;
			return this;
		}

		public ApiQuestionAnswerPairDTO build() {
			return new ApiQuestionAnswerPairDTO(this);
		}
	}
}
