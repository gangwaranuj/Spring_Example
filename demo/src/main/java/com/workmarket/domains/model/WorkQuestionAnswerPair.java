package com.workmarket.domains.model;

import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;

@Entity(name="workQuestionAnswerPair")
@Table(name="work_question_answer_pair")
@NamedQueries({
	@NamedQuery(name="workQuestionAnswerPair.byWork", query="from workQuestionAnswerPair qa where qa.workId = :work_id"),
	@NamedQuery(name="workId.byQuestionId", query = "select qa.workId from workQuestionAnswerPair qa where qa.id = :question_id")
})
public class WorkQuestionAnswerPair extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private Long workId;
	private String question;
	private String answer;
	private Long questionerId;
	private Long answererId;
	private Calendar createdOn;
	private Calendar answeredOn;
	private Boolean flaggedForReview = Boolean.FALSE;
	private Boolean deletedFlag = Boolean.FALSE;

	@Column(name="work_id", nullable = false)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@Column(name="question", nullable=false)
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	@Column(name="answer", nullable=true)
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Column(name="questioner_user_id", nullable=false)
	public Long getQuestionerId() {
		return questionerId;
	}

	public void setQuestionerId(Long questionerId) {
		this.questionerId = questionerId;
	}

	@Column(name="answerer_user_id", nullable=true)
	public Long getAnswererId() {
		return answererId;
	}

	public void setAnswererId(Long answererId) {
		this.answererId = answererId;
	}

	@Column(name = "created_on")
	public Calendar getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
	}

	@Column(name = "answered_on", nullable=true)
	public Calendar getAnsweredOn() {
		return answeredOn;
	}

	public void setAnsweredOn(Calendar answeredOn) {
		this.answeredOn = answeredOn;
	}

	@Column(name="flagged_for_review_flag", nullable=false, length=1)
	@Type(type="yes_no")
	public Boolean getIsFlaggedForReview() {
		return flaggedForReview;
	}

	public void setIsFlaggedForReview(Boolean flaggedForReview) {
		this.flaggedForReview = flaggedForReview;
	}

	@Column(name="deleted_flag", nullable=false, length=1)
	public Boolean getDeleted() {
		return deletedFlag;
	}

	public void setDeleted(Boolean deletedFlag) {
		this.deletedFlag = deletedFlag;
	}

	@Transient
	public Boolean isAnswered() {
		return (answer != null);
	}
}
