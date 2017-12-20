package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class GradeAttemptRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private long currentUserId;
	private long userId;
	private long assessmentId;

	public GradeAttemptRequest() {
	}

	public GradeAttemptRequest(long currentUserId, long userId, long assessmentId) {
		this();
		this.currentUserId = currentUserId;
		this.userId = userId;
		this.assessmentId = assessmentId;
	}

	public long getCurrentUserId() {
		return this.currentUserId;
	}

	public GradeAttemptRequest setCurrentUserId(long currentUserId) {
		this.currentUserId = currentUserId;
		return this;
	}

	public boolean isSetCurrentUserId() {
		return (currentUserId > 0L);
	}

	public long getUserId() {
		return this.userId;
	}

	public GradeAttemptRequest setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return (userId > 0L);
	}

	public long getAssessmentId() {
		return this.assessmentId;
	}

	public GradeAttemptRequest setAssessmentId(long assessmentId) {
		this.assessmentId = assessmentId;
		return this;
	}

	public boolean isSetAssessmentId() {
		return (assessmentId > 0);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof GradeAttemptRequest)
			return this.equals((GradeAttemptRequest) that);
		return false;
	}

	private boolean equals(GradeAttemptRequest that) {
		if (that == null)
			return false;

		boolean this_present_currentUserId = true;
		boolean that_present_currentUserId = true;
		if (this_present_currentUserId || that_present_currentUserId) {
			if (!(this_present_currentUserId && that_present_currentUserId))
				return false;
			if (this.currentUserId != that.currentUserId)
				return false;
		}

		boolean this_present_userId = true;
		boolean that_present_userId = true;
		if (this_present_userId || that_present_userId) {
			if (!(this_present_userId && that_present_userId))
				return false;
			if (this.userId != that.userId)
				return false;
		}

		boolean this_present_assessmentId = true;
		boolean that_present_assessmentId = true;
		if (this_present_assessmentId || that_present_assessmentId) {
			if (!(this_present_assessmentId && that_present_assessmentId))
				return false;
			if (this.assessmentId != that.assessmentId)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_currentUserId = true;
		builder.append(present_currentUserId);
		if (present_currentUserId)
			builder.append(currentUserId);

		boolean present_userId = true;
		builder.append(present_userId);
		if (present_userId)
			builder.append(userId);

		boolean present_assessmentId = true;
		builder.append(present_assessmentId);
		if (present_assessmentId)
			builder.append(assessmentId);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("GradeAttemptRequest(");
		boolean first = true;

		sb.append("currentUserId:");
		sb.append(this.currentUserId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("userId:");
		sb.append(this.userId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("assessmentId:");
		sb.append(this.assessmentId);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

