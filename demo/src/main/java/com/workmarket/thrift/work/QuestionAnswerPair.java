package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class QuestionAnswerPair implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String question;
	private long questionerId;
	private long questionedOn;
	private String answer;
	private long answererId;
	private long answeredOn;

	public QuestionAnswerPair() {}

	public QuestionAnswerPair(
		long id,
		String question,
		long questionerId,
		long questionedOn,
		String answer,
		long answererId,
		long answeredOn) {
		this();
		this.id = id;
		this.question = question;
		this.questionerId = questionerId;
		this.questionedOn = questionedOn;
		this.answer = answer;
		this.answererId = answererId;
		this.answeredOn = answeredOn;
	}

	public long getId() {
		return this.id;
	}

	public QuestionAnswerPair setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getQuestion() {
		return this.question;
	}

	public QuestionAnswerPair setQuestion(String question) {
		this.question = question;
		return this;
	}

	public boolean isSetQuestion() {
		return this.question != null;
	}

	public long getQuestioner() {
		return this.questionerId;
	}

	public QuestionAnswerPair setQuestioner(long questionerId) {
		this.questionerId = questionerId;
		return this;
	}

	public boolean isSetQuestioner() {
		return this.questionerId > 0L;
	}

	public long getQuestionedOn() {
		return this.questionedOn;
	}

	public QuestionAnswerPair setQuestionedOn(long questionedOn) {
		this.questionedOn = questionedOn;
		return this;
	}

	public boolean isSetQuestionedOn() {
		return (questionedOn > 0L);
	}

	public String getAnswer() {
		return this.answer;
	}

	public QuestionAnswerPair setAnswer(String answer) {
		this.answer = answer;
		return this;
	}

	public boolean isSetAnswer() {
		return this.answer != null;
	}

	public long getAnswerer() {
		return this.answererId;
	}

	public QuestionAnswerPair setAnswerer(long answererId) {
		this.answererId = answererId;
		return this;
	}

	public boolean isSetAnswerer() {
		return this.answererId > 0L;
	}

	public long getAnsweredOn() {
		return this.answeredOn;
	}

	public QuestionAnswerPair setAnsweredOn(long answeredOn) {
		this.answeredOn = answeredOn;
		return this;
	}

	public boolean isSetAnsweredOn() {
		return (answeredOn > 0L);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof QuestionAnswerPair)
			return this.equals((QuestionAnswerPair) that);
		return false;
	}

	private boolean equals(QuestionAnswerPair that) {
		if (that == null)
			return false;

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
				return false;
		}

		boolean this_present_question = true && this.isSetQuestion();
		boolean that_present_question = true && that.isSetQuestion();
		if (this_present_question || that_present_question) {
			if (!(this_present_question && that_present_question))
				return false;
			if (!this.question.equals(that.question))
				return false;
		}

		boolean this_present_questioner = true && this.isSetQuestioner();
		boolean that_present_questioner = true && that.isSetQuestioner();
		if (this_present_questioner || that_present_questioner) {
			if (!(this_present_questioner && that_present_questioner))
				return false;
			if (this.questionerId != that.questionerId)
				return false;
		}

		boolean this_present_questionedOn = true;
		boolean that_present_questionedOn = true;
		if (this_present_questionedOn || that_present_questionedOn) {
			if (!(this_present_questionedOn && that_present_questionedOn))
				return false;
			if (this.questionedOn != that.questionedOn)
				return false;
		}

		boolean this_present_answer = true && this.isSetAnswer();
		boolean that_present_answer = true && that.isSetAnswer();
		if (this_present_answer || that_present_answer) {
			if (!(this_present_answer && that_present_answer))
				return false;
			if (!this.answer.equals(that.answer))
				return false;
		}

		boolean this_present_answerer = true && this.isSetAnswerer();
		boolean that_present_answerer = true && that.isSetAnswerer();
		if (this_present_answerer || that_present_answerer) {
			if (!(this_present_answerer && that_present_answerer))
				return false;
			if (this.answererId != that.answererId)
				return false;
		}

		boolean this_present_answeredOn = true;
		boolean that_present_answeredOn = true;
		if (this_present_answeredOn || that_present_answeredOn) {
			if (!(this_present_answeredOn && that_present_answeredOn))
				return false;
			if (this.answeredOn != that.answeredOn)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_question = true && (isSetQuestion());
		builder.append(present_question);
		if (present_question)
			builder.append(question);

		boolean present_questioner = true && (isSetQuestioner());
		builder.append(present_questioner);
		if (present_questioner)
			builder.append(questionerId);

		boolean present_questionedOn = true;
		builder.append(present_questionedOn);
		if (present_questionedOn)
			builder.append(questionedOn);

		boolean present_answer = true && (isSetAnswer());
		builder.append(present_answer);
		if (present_answer)
			builder.append(answer);

		boolean present_answerer = true && (isSetAnswerer());
		builder.append(present_answerer);
		if (present_answerer)
			builder.append(answererId);

		boolean present_answeredOn = true;
		builder.append(present_answeredOn);
		if (present_answeredOn)
			builder.append(answeredOn);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("QuestionAnswerPair(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("question:");
		if (this.question == null) {
			sb.append("null");
		} else {
			sb.append(this.question);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("questionerId:");
		sb.append(this.questionerId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("questionedOn:");
		sb.append(this.questionedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("answer:");
		if (this.answer == null) {
			sb.append("null");
		} else {
			sb.append(this.answer);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("answererId:");
		sb.append(this.answererId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("answeredOn:");
		sb.append(this.answeredOn);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

