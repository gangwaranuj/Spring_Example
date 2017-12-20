package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class AssessmentAttemptPair implements Serializable {
	private static final long serialVersionUID = 1L;

	private Assessment assessment;
	private Attempt latestAttempt;

	public AssessmentAttemptPair() {
	}

	public AssessmentAttemptPair(Assessment assessment, Attempt latestAttempt) {
		this();
		this.assessment = assessment;
		this.latestAttempt = latestAttempt;
	}

	public Assessment getAssessment() {
		return this.assessment;
	}

	public AssessmentAttemptPair setAssessment(Assessment assessment) {
		this.assessment = assessment;
		return this;
	}

	public boolean isSetAssessment() {
		return this.assessment != null;
	}

	public Attempt getLatestAttempt() {
		return this.latestAttempt;
	}

	public AssessmentAttemptPair setLatestAttempt(Attempt latestAttempt) {
		this.latestAttempt = latestAttempt;
		return this;
	}

	public boolean isSetLatestAttempt() {
		return this.latestAttempt != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof AssessmentAttemptPair)
			return this.equals((AssessmentAttemptPair) that);
		return false;
	}

	private boolean equals(AssessmentAttemptPair that) {
		if (that == null)
			return false;

		boolean this_present_assessment = true && this.isSetAssessment();
		boolean that_present_assessment = true && that.isSetAssessment();
		if (this_present_assessment || that_present_assessment) {
			if (!(this_present_assessment && that_present_assessment))
				return false;
			if (!this.assessment.equals(that.assessment))
				return false;
		}

		boolean this_present_latestAttempt = true && this.isSetLatestAttempt();
		boolean that_present_latestAttempt = true && that.isSetLatestAttempt();
		if (this_present_latestAttempt || that_present_latestAttempt) {
			if (!(this_present_latestAttempt && that_present_latestAttempt))
				return false;
			if (!this.latestAttempt.equals(that.latestAttempt))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_assessment = true && (isSetAssessment());
		builder.append(present_assessment);
		if (present_assessment)
			builder.append(assessment);

		boolean present_latestAttempt = true && (isSetLatestAttempt());
		builder.append(present_latestAttempt);
		if (present_latestAttempt)
			builder.append(latestAttempt);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AssessmentAttemptPair(");
		boolean first = true;

		sb.append("assessment:");
		if (this.assessment == null) {
			sb.append("null");
		} else {
			sb.append(this.assessment);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("latestAttempt:");
		if (this.latestAttempt == null) {
			sb.append("null");
		} else {
			sb.append(this.latestAttempt);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

