package com.workmarket.thrift.assessment;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public final class AssessmentGradingRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private long userId;
	private long assessmentId;
	private long attemptId;

	public AssessmentGradingRequest() {}

	public AssessmentGradingRequest(long userId, long assessmentId) {
		this.userId = userId;
		this.assessmentId = assessmentId;
	}

	public long getUserId() {
		return this.userId;
	}

	public AssessmentGradingRequest setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return (userId > 0L);
	}

	public long getAssessmentId() {
		return this.assessmentId;
	}

	public AssessmentGradingRequest setAssessmentId(long assessmentId) {
		this.assessmentId = assessmentId;
		return this;
	}

	public boolean isSetAssessmentId() {
		return (assessmentId > 0L);
	}

	public long getAttemptId() {
		return this.attemptId;
	}

	public AssessmentGradingRequest setAttemptId(long attemptId) {
		this.attemptId = attemptId;
		return this;
	}

	public boolean isSetAttemptId() {
		return (attemptId > 0L);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AssessmentGradingRequest(");
		boolean first = true;

		sb.append("userId:");
		sb.append(this.userId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("assessmentId:");
		sb.append(this.assessmentId);
		first = false;
		if (isSetAttemptId()) {
			if (!first) sb.append(", ");
			sb.append("attemptId:");
			sb.append(this.attemptId);
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof AssessmentGradingRequest)) {
			return false;
		}

		AssessmentGradingRequest request = (AssessmentGradingRequest) o;

		return new EqualsBuilder()
			.append(assessmentId, request.getAssessmentId())
			.append(attemptId, request.getAttemptId())
			.append(userId, request.getUserId())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(assessmentId)
			.append(attemptId)
			.append(userId)
			.toHashCode();
	}
}

