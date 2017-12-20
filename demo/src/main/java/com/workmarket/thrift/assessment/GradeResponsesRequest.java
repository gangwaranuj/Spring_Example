package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class GradeResponsesRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private long currentUserId;
	private long attemptId;
	private long itemId;
	private boolean passed;

	public GradeResponsesRequest() {
	}

	public GradeResponsesRequest(long currentUserId, long attemptId, long itemId, boolean passed) {
		this();
		this.currentUserId = currentUserId;
		this.attemptId = attemptId;
		this.itemId = itemId;
		this.passed = passed;
	}

	public long getCurrentUserId() {
		return this.currentUserId;
	}

	public GradeResponsesRequest setCurrentUserId(long currentUserId) {
		this.currentUserId = currentUserId;
		return this;
	}

	public boolean isSetCurrentUserId() {
		return (currentUserId > 0L);
	}

	public long getAttemptId() {
		return this.attemptId;
	}

	public GradeResponsesRequest setAttemptId(long attemptId) {
		this.attemptId = attemptId;
		return this;
	}

	public boolean isSetAttemptId() {
		return (attemptId > 0L);
	}

	public long getItemId() {
		return this.itemId;
	}

	public GradeResponsesRequest setItemId(long itemId) {
		this.itemId = itemId;
		return this;
	}

	public boolean isSetItemId() {
		return (itemId > 0L);
	}

	public boolean isPassed() {
		return this.passed;
	}

	public GradeResponsesRequest setPassed(boolean passed) {
		this.passed = passed;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof GradeResponsesRequest)
			return this.equals((GradeResponsesRequest) that);
		return false;
	}

	private boolean equals(GradeResponsesRequest that) {
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

		boolean this_present_attemptId = true;
		boolean that_present_attemptId = true;
		if (this_present_attemptId || that_present_attemptId) {
			if (!(this_present_attemptId && that_present_attemptId))
				return false;
			if (this.attemptId != that.attemptId)
				return false;
		}

		boolean this_present_itemId = true;
		boolean that_present_itemId = true;
		if (this_present_itemId || that_present_itemId) {
			if (!(this_present_itemId && that_present_itemId))
				return false;
			if (this.itemId != that.itemId)
				return false;
		}

		boolean this_present_passed = true;
		boolean that_present_passed = true;
		if (this_present_passed || that_present_passed) {
			if (!(this_present_passed && that_present_passed))
				return false;
			if (this.passed != that.passed)
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

		boolean present_attemptId = true;
		builder.append(present_attemptId);
		if (present_attemptId)
			builder.append(attemptId);

		boolean present_itemId = true;
		builder.append(present_itemId);
		if (present_itemId)
			builder.append(itemId);

		boolean present_passed = true;
		builder.append(present_passed);
		if (present_passed)
			builder.append(passed);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("GradeResponsesRequest(");
		boolean first = true;

		sb.append("currentUserId:");
		sb.append(this.currentUserId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("attemptId:");
		sb.append(this.attemptId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("itemId:");
		sb.append(this.itemId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("passed:");
		sb.append(this.passed);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

