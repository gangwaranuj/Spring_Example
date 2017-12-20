package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AttemptResponseRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private long assessmentId;
	private long itemId;
	private List<Response> responses;
	private long workId;

	public AttemptResponseRequest() {
	}

	public AttemptResponseRequest(long userId, long assessmentId, long itemId, List<Response> responses) {
		this();
		this.userId = userId;
		this.assessmentId = assessmentId;
		this.itemId = itemId;
		this.responses = responses;
	}

	public long getUserId() {
		return this.userId;
	}

	public AttemptResponseRequest setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return (userId > 0L);
	}

	public long getAssessmentId() {
		return this.assessmentId;
	}

	public AttemptResponseRequest setAssessmentId(long assessmentId) {
		this.assessmentId = assessmentId;
		return this;
	}

	public boolean isSetAssessmentId() {
		return (assessmentId > 0L);
	}

	public long getItemId() {
		return this.itemId;
	}

	public AttemptResponseRequest setItemId(long itemId) {
		this.itemId = itemId;
		return this;
	}

	public boolean isSetItemId() {
		return (itemId > 0L);
	}

	public int getResponsesSize() {
		return (this.responses == null) ? 0 : this.responses.size();
	}

	public java.util.Iterator<Response> getResponsesIterator() {
		return (this.responses == null) ? null : this.responses.iterator();
	}

	public void addToResponses(Response elem) {
		if (this.responses == null) {
			this.responses = new ArrayList<Response>();
		}
		this.responses.add(elem);
	}

	public List<Response> getResponses() {
		return this.responses;
	}

	public AttemptResponseRequest setResponses(List<Response> responses) {
		this.responses = responses;
		return this;
	}

	public boolean isSetResponses() {
		return this.responses != null;
	}

	public long getWorkId() {
		return this.workId;
	}

	public AttemptResponseRequest setWorkId(long workId) {
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
		if (that instanceof AttemptResponseRequest)
			return this.equals((AttemptResponseRequest) that);
		return false;
	}

	private boolean equals(AttemptResponseRequest that) {
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

		boolean this_present_itemId = true;
		boolean that_present_itemId = true;
		if (this_present_itemId || that_present_itemId) {
			if (!(this_present_itemId && that_present_itemId))
				return false;
			if (this.itemId != that.itemId)
				return false;
		}

		boolean this_present_responses = true && this.isSetResponses();
		boolean that_present_responses = true && that.isSetResponses();
		if (this_present_responses || that_present_responses) {
			if (!(this_present_responses && that_present_responses))
				return false;
			if (!this.responses.equals(that.responses))
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

		boolean present_itemId = true;
		builder.append(present_itemId);
		if (present_itemId)
			builder.append(itemId);

		boolean present_responses = true && (isSetResponses());
		builder.append(present_responses);
		if (present_responses)
			builder.append(responses);

		boolean present_workId = true && (isSetWorkId());
		builder.append(present_workId);
		if (present_workId)
			builder.append(workId);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AttemptResponseRequest(");
		boolean first = true;

		sb.append("userId:");
		sb.append(this.userId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("assessmentId:");
		sb.append(this.assessmentId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("itemId:");
		sb.append(this.itemId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("responses:");
		if (this.responses == null) {
			sb.append("null");
		} else {
			sb.append(this.responses);
		}
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