package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class AssessmentStatistics implements Serializable {
	private static final long serialVersionUID = 1L;

	private int numberOfInvited;
	private int numberOfPassed;
	private int numberOfFailed;
	private double averageScore;

	public AssessmentStatistics() {
	}

	public AssessmentStatistics(int numberOfInvited, int numberOfPassed, int numberOfFailed, double averageScore) {
		this();
		this.numberOfInvited = numberOfInvited;
		this.numberOfPassed = numberOfPassed;
		this.numberOfFailed = numberOfFailed;
		this.averageScore = averageScore;
	}

	public int getNumberOfInvited() {
		return this.numberOfInvited;
	}

	public AssessmentStatistics setNumberOfInvited(int numberOfInvited) {
		this.numberOfInvited = numberOfInvited;
		return this;
	}

	public boolean isSetNumberOfInvited() {
		return (numberOfInvited > 0);
	}

	public int getNumberOfPassed() {
		return this.numberOfPassed;
	}

	public AssessmentStatistics setNumberOfPassed(int numberOfPassed) {
		this.numberOfPassed = numberOfPassed;
		return this;
	}

	public boolean isSetNumberOfPassed() {
		return (numberOfPassed > 0);
	}

	public int getNumberOfFailed() {
		return this.numberOfFailed;
	}

	public AssessmentStatistics setNumberOfFailed(int numberOfFailed) {
		this.numberOfFailed = numberOfFailed;
		return this;
	}

	public boolean isSetNumberOfFailed() {
		return (numberOfFailed > 0);
	}

	public double getAverageScore() {
		return this.averageScore;
	}

	public AssessmentStatistics setAverageScore(double averageScore) {
		this.averageScore = averageScore;
		return this;
	}

	public boolean isSetAverageScore() {
		return (averageScore > 0);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof AssessmentStatistics)
			return this.equals((AssessmentStatistics) that);
		return false;
	}

	private boolean equals(AssessmentStatistics that) {
		if (that == null)
			return false;

		boolean this_present_numberOfInvited = true;
		boolean that_present_numberOfInvited = true;
		if (this_present_numberOfInvited || that_present_numberOfInvited) {
			if (!(this_present_numberOfInvited && that_present_numberOfInvited))
				return false;
			if (this.numberOfInvited != that.numberOfInvited)
				return false;
		}

		boolean this_present_numberOfPassed = true;
		boolean that_present_numberOfPassed = true;
		if (this_present_numberOfPassed || that_present_numberOfPassed) {
			if (!(this_present_numberOfPassed && that_present_numberOfPassed))
				return false;
			if (this.numberOfPassed != that.numberOfPassed)
				return false;
		}

		boolean this_present_numberOfFailed = true;
		boolean that_present_numberOfFailed = true;
		if (this_present_numberOfFailed || that_present_numberOfFailed) {
			if (!(this_present_numberOfFailed && that_present_numberOfFailed))
				return false;
			if (this.numberOfFailed != that.numberOfFailed)
				return false;
		}

		boolean this_present_averageScore = true;
		boolean that_present_averageScore = true;
		if (this_present_averageScore || that_present_averageScore) {
			if (!(this_present_averageScore && that_present_averageScore))
				return false;
			if (this.averageScore != that.averageScore)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_numberOfInvited = true;
		builder.append(present_numberOfInvited);
		if (present_numberOfInvited)
			builder.append(numberOfInvited);

		boolean present_numberOfPassed = true;
		builder.append(present_numberOfPassed);
		if (present_numberOfPassed)
			builder.append(numberOfPassed);

		boolean present_numberOfFailed = true;
		builder.append(present_numberOfFailed);
		if (present_numberOfFailed)
			builder.append(numberOfFailed);

		boolean present_averageScore = true;
		builder.append(present_averageScore);
		if (present_averageScore)
			builder.append(averageScore);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AssessmentStatistics(");
		boolean first = true;

		sb.append("numberOfInvited:");
		sb.append(this.numberOfInvited);
		first = false;
		if (!first) sb.append(", ");
		sb.append("numberOfPassed:");
		sb.append(this.numberOfPassed);
		first = false;
		if (!first) sb.append(", ");
		sb.append("numberOfFailed:");
		sb.append(this.numberOfFailed);
		first = false;
		if (!first) sb.append(", ");
		sb.append("averageScore:");
		sb.append(this.averageScore);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}