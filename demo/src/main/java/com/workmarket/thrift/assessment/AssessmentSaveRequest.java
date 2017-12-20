package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class AssessmentSaveRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private Assessment assessment;

	public AssessmentSaveRequest() {
	}

	public AssessmentSaveRequest(long userId, Assessment assessment) {
		this();
		this.userId = userId;
		this.assessment = assessment;
	}

	public long getUserId() {
		return this.userId;
	}

	public AssessmentSaveRequest setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return (userId > 0L);
	}

	public Assessment getAssessment() {
		return this.assessment;
	}

	public AssessmentSaveRequest setAssessment(Assessment assessment) {
		this.assessment = assessment;
		return this;
	}

	public boolean isSetAssessment() {
		return this.assessment != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof AssessmentSaveRequest)
			return this.equals((AssessmentSaveRequest) that);
		return false;
	}

	private boolean equals(AssessmentSaveRequest that) {
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

		boolean this_present_assessment = true && this.isSetAssessment();
		boolean that_present_assessment = true && that.isSetAssessment();
		if (this_present_assessment || that_present_assessment) {
			if (!(this_present_assessment && that_present_assessment))
				return false;
			if (!this.assessment.equals(that.assessment))
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

		boolean present_assessment = true && (isSetAssessment());
		builder.append(present_assessment);
		if (present_assessment)
			builder.append(assessment);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AssessmentSaveRequest(");
		boolean first = true;

		sb.append("userId:");
		sb.append(this.userId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("assessment:");
		if (this.assessment == null) {
			sb.append("null");
		} else {
			sb.append(this.assessment);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}