package com.workmarket.thrift.work;

import com.workmarket.domains.work.service.audit.WorkActionRequest;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class WorkQuestionRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private WorkActionRequest workAction;
	private String question;

	public WorkQuestionRequest() {
	}

	public WorkQuestionRequest(WorkActionRequest workAction, String question) {
		this();
		this.workAction = workAction;
		this.question = question;
	}

	public WorkActionRequest getWorkAction() {
		return this.workAction;
	}

	public WorkQuestionRequest setWorkAction(WorkActionRequest workAction) {
		this.workAction = workAction;
		return this;
	}

	public boolean isSetWorkAction() {
		return this.workAction != null;
	}

	public String getQuestion() {
		return this.question;
	}

	public WorkQuestionRequest setQuestion(String question) {
		this.question = question;
		return this;
	}

	public boolean isSetQuestion() {
		return this.question != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkQuestionRequest)
			return this.equals((WorkQuestionRequest) that);
		return false;
	}

	private boolean equals(WorkQuestionRequest that) {
		if (that == null)
			return false;

		boolean this_present_workAction = true && this.isSetWorkAction();
		boolean that_present_workAction = true && that.isSetWorkAction();
		if (this_present_workAction || that_present_workAction) {
			if (!(this_present_workAction && that_present_workAction))
				return false;
			if (!this.workAction.equals(that.workAction))
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

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_workAction = true && (isSetWorkAction());
		builder.append(present_workAction);
		if (present_workAction)
			builder.append(workAction);

		boolean present_question = true && (isSetQuestion());
		builder.append(present_question);
		if (present_question)
			builder.append(question);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkQuestionRequest(");
		boolean first = true;

		sb.append("workAction:");
		if (this.workAction == null) {
			sb.append("null");
		} else {
			sb.append(this.workAction);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("question:");
		if (this.question == null) {
			sb.append("null");
		} else {
			sb.append(this.question);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}

}