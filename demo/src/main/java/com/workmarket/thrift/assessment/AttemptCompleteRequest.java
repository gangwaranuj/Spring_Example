package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class AttemptCompleteRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private long assessmentId;
	private long workId;

	public AttemptCompleteRequest() {
	}

	public AttemptCompleteRequest(long userId, long assessmentId) {
		this();
		this.userId = userId;
		this.assessmentId = assessmentId;
	}

	public long getUserId() {
		return this.userId;
	}

	public AttemptCompleteRequest setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return (userId > 0L);
	}

	public long getAssessmentId() {
		return this.assessmentId;
	}

	public AttemptCompleteRequest setAssessmentId(long assessmentId) {
		this.assessmentId = assessmentId;
		return this;
	}

	public boolean isSetAssessmentId() {
		return (assessmentId > 0L);
	}

	public long getWorkId() {
		return this.workId;
	}

	public AttemptCompleteRequest setWorkId(long workId) {
		this.workId = workId;
		return this;
	}

	public boolean isSetWorkId() {
		return (workId > 0L);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof AttemptCompleteRequest)
			return this.equals((AttemptCompleteRequest) that);
		return false;
	}

	private boolean equals(AttemptCompleteRequest that) {
		if (that == null)
			return false;

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

		boolean this_present_workId = true && this.isSetWorkId();
		boolean that_present_workId = true && that.isSetWorkId();
		if (this_present_workId || that_present_workId) {
			if (!(this_present_workId && that_present_workId))
				return false;
			if (this.workId != that.workId)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_userId = true;
		builder.append(present_userId);
		if (present_userId)
			builder.append(userId);

		boolean present_assessmentId = true;
		builder.append(present_assessmentId);
		if (present_assessmentId)
			builder.append(assessmentId);

		boolean present_workId = true && (isSetWorkId());
		builder.append(present_workId);
		if (present_workId)
			builder.append(workId);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AttemptCompleteRequest(");
		boolean first = true;

		sb.append("userId:");
		sb.append(this.userId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("assessmentId:");
		sb.append(this.assessmentId);
		first = false;
		if (isSetWorkId()) {
			if (!first) sb.append(", ");
			sb.append("workId:");
			sb.append(this.workId);
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}

