package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class RoutingStrategySummary implements Serializable {
	private static final long serialVersionUID = 1L;

	private int sent;
	private int failed;
	private int failedFunds;
	private int failedSpendLimit;
	private int failedCredit;
	private int failedValidation;

	public RoutingStrategySummary() {
	}

	public RoutingStrategySummary(
			int sent,
			int failed,
			int failedFunds,
			int failedSpendLimit,
			int failedCredit,
			int failedValidation) {
		this();
		this.sent = sent;
		this.failed = failed;
		this.failedFunds = failedFunds;
		this.failedSpendLimit = failedSpendLimit;
		this.failedCredit = failedCredit;
		this.failedValidation = failedValidation;
	}

	public int getSent() {
		return this.sent;
	}

	public RoutingStrategySummary setSent(int sent) {
		this.sent = sent;
		return this;
	}

	public int getFailed() {
		return this.failed;
	}

	public RoutingStrategySummary setFailed(int failed) {
		this.failed = failed;
		return this;
	}

	public int getFailedFunds() {
		return this.failedFunds;
	}

	public RoutingStrategySummary setFailedFunds(int failedFunds) {
		this.failedFunds = failedFunds;
		return this;
	}

	public int getFailedSpendLimit() {
		return this.failedSpendLimit;
	}

	public RoutingStrategySummary setFailedSpendLimit(int failedSpendLimit) {
		this.failedSpendLimit = failedSpendLimit;
		return this;
	}

	public int getFailedCredit() {
		return this.failedCredit;
	}

	public RoutingStrategySummary setFailedCredit(int failedCredit) {
		this.failedCredit = failedCredit;
		return this;
	}

	public int getFailedValidation() {
		return this.failedValidation;
	}

	public RoutingStrategySummary setFailedValidation(int failedValidation) {
		this.failedValidation = failedValidation;
		return this;
	}

	public boolean isSetFailedValidation() {
		return (failedValidation > 0);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RoutingStrategySummary)
			return this.equals((RoutingStrategySummary) that);
		return false;
	}

	private boolean equals(RoutingStrategySummary that) {
		if (that == null)
			return false;

		boolean this_present_sent = true;
		boolean that_present_sent = true;
		if (this_present_sent || that_present_sent) {
			if (!(this_present_sent && that_present_sent))
				return false;
			if (this.sent != that.sent)
				return false;
		}

		boolean this_present_failed = true;
		boolean that_present_failed = true;
		if (this_present_failed || that_present_failed) {
			if (!(this_present_failed && that_present_failed))
				return false;
			if (this.failed != that.failed)
				return false;
		}

		boolean this_present_failedFunds = true;
		boolean that_present_failedFunds = true;
		if (this_present_failedFunds || that_present_failedFunds) {
			if (!(this_present_failedFunds && that_present_failedFunds))
				return false;
			if (this.failedFunds != that.failedFunds)
				return false;
		}

		boolean this_present_failedSpendLimit = true;
		boolean that_present_failedSpendLimit = true;
		if (this_present_failedSpendLimit || that_present_failedSpendLimit) {
			if (!(this_present_failedSpendLimit && that_present_failedSpendLimit))
				return false;
			if (this.failedSpendLimit != that.failedSpendLimit)
				return false;
		}

		boolean this_present_failedCredit = true;
		boolean that_present_failedCredit = true;
		if (this_present_failedCredit || that_present_failedCredit) {
			if (!(this_present_failedCredit && that_present_failedCredit))
				return false;
			if (this.failedCredit != that.failedCredit)
				return false;
		}

		boolean this_present_failedValidation = true;
		boolean that_present_failedValidation = true;
		if (this_present_failedValidation || that_present_failedValidation) {
			if (!(this_present_failedValidation && that_present_failedValidation))
				return false;
			if (this.failedValidation != that.failedValidation)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_sent = true;
		builder.append(present_sent);
		if (present_sent)
			builder.append(sent);

		boolean present_failed = true;
		builder.append(present_failed);
		if (present_failed)
			builder.append(failed);

		boolean present_failedFunds = true;
		builder.append(present_failedFunds);
		if (present_failedFunds)
			builder.append(failedFunds);

		boolean present_failedSpendLimit = true;
		builder.append(present_failedSpendLimit);
		if (present_failedSpendLimit)
			builder.append(failedSpendLimit);

		boolean present_failedCredit = true;
		builder.append(present_failedCredit);
		if (present_failedCredit)
			builder.append(failedCredit);

		boolean present_failedValidation = true;
		builder.append(present_failedValidation);
		if (present_failedValidation)
			builder.append(failedValidation);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RoutingStrategySummary(");
		boolean first = true;

		sb.append("sent:");
		sb.append(this.sent);
		first = false;
		if (!first) sb.append(", ");
		sb.append("failed:");
		sb.append(this.failed);
		first = false;
		if (!first) sb.append(", ");
		sb.append("failedFunds:");
		sb.append(this.failedFunds);
		first = false;
		if (!first) sb.append(", ");
		sb.append("failedSpendLimit:");
		sb.append(this.failedSpendLimit);
		first = false;
		if (!first) sb.append(", ");
		sb.append("failedCredit:");
		sb.append(this.failedCredit);
		first = false;
		if (!first) sb.append(", ");
		sb.append("failedValidation:");
		sb.append(this.failedValidation);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}