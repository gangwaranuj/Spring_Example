package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class AttemptStartRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private long assessmentId;
	private long workId;
	private long behalfOfId;

	public AttemptStartRequest() {
	}

	public AttemptStartRequest(long userId, long assessmentId) {
		this();
		this.userId = userId;
		this.assessmentId = assessmentId;
	}

	public AttemptStartRequest(AttemptStartRequest other) {
		this.userId = other.userId;
		this.assessmentId = other.assessmentId;
		this.workId = other.workId;
		this.behalfOfId = other.behalfOfId;
	}

	public AttemptStartRequest deepCopy() {
		return new AttemptStartRequest(this);
	}

	public long getUserId() {
		return this.userId;
	}

	public AttemptStartRequest setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return (userId > 0L);
	}

	public long getAssessmentId() {
		return this.assessmentId;
	}

	public AttemptStartRequest setAssessmentId(long assessmentId) {
		this.assessmentId = assessmentId;
		return this;
	}

	public boolean isSetAssessmentId() {
		return (assessmentId > 0L);
	}
	
	public long getWorkId() {
		return this.workId;
	}

	public AttemptStartRequest setWorkId(long workId) {
		this.workId = workId;
		return this;
	}

	public boolean isSetWorkId() {
		return (workId > 0L);
	}
	
	public long getBehalfOfId() {
		return this.behalfOfId;
	}

	public AttemptStartRequest setBehalfOfId(long behalfOfId) {
		this.behalfOfId = behalfOfId;
		return this;
	}

	public boolean isSetBehalfOfId() {
		return (behalfOfId > 0L);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof AttemptStartRequest)
			return this.equals((AttemptStartRequest) that);
		return false;
	}

	private boolean equals(AttemptStartRequest that) {
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
		
		boolean this_present_behalfOfId = true && this.isSetBehalfOfId();
		boolean that_present_behalfOfId = true && that.isSetBehalfOfId();
		if (this_present_behalfOfId || that_present_behalfOfId) {
			if (!(this_present_behalfOfId && that_present_behalfOfId))
				return false;
			if (this.behalfOfId != that.behalfOfId)
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
		
		boolean present_behalfOfId = true && (isSetBehalfOfId());
		builder.append(present_behalfOfId);
		if (present_behalfOfId)
			builder.append(behalfOfId);
		
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AttemptStartRequest(");
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
		if (isSetBehalfOfId()) {
			if (!first) sb.append(", ");
			sb.append("behalfOfId:");
			sb.append(this.behalfOfId);
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}

