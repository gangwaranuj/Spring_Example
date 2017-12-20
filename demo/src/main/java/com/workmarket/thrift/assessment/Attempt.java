package com.workmarket.thrift.assessment;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Attempt implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private com.workmarket.thrift.core.User user;
	private List<Response> responses;
	private com.workmarket.thrift.core.Status status;
	private long createdOn;
	private long completeOn;
	private long gradedOn;
	private com.workmarket.thrift.core.User grader;
	private double score;
	private boolean passed;
	private long duration;
	private int totalAttemptsCount;
	private boolean scopedToWork;
	private AttemptWorkScope work;
	private boolean respondedToAllItems;

	public Attempt() {
	}

	public Attempt(
			long id,
			com.workmarket.thrift.core.User user,
			List<Response> responses,
			com.workmarket.thrift.core.Status status,
			long createdOn,
			long completeOn,
			long gradedOn,
			com.workmarket.thrift.core.User grader,
			double score,
			boolean passed,
			long duration,
			int totalAttemptsCount,
			boolean scopedToWork,
			AttemptWorkScope work,
			boolean respondedToAllItems) {
		this();
		this.id = id;
		this.user = user;
		this.responses = responses;
		this.status = status;
		this.createdOn = createdOn;
		this.completeOn = completeOn;
		this.gradedOn = gradedOn;
		this.grader = grader;
		this.score = score;
		this.passed = passed;
		this.duration = duration;
		this.totalAttemptsCount = totalAttemptsCount;
		this.scopedToWork = scopedToWork;
		this.work = work;
		this.respondedToAllItems = respondedToAllItems;
	}

	public long getId() {
		return this.id;
	}

	public Attempt setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public com.workmarket.thrift.core.User getUser() {
		return this.user;
	}

	public Attempt setUser(com.workmarket.thrift.core.User user) {
		this.user = user;
		return this;
	}

	public boolean isSetUser() {
		return this.user != null;
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

	public Attempt setResponses(List<Response> responses) {
		this.responses = responses;
		return this;
	}

	public boolean isSetResponses() {
		return this.responses != null;
	}

	public com.workmarket.thrift.core.Status getStatus() {
		return this.status;
	}

	public Attempt setStatus(com.workmarket.thrift.core.Status status) {
		this.status = status;
		return this;
	}

	public boolean isSetStatus() {
		return this.status != null;
	}

	public long getCreatedOn() {
		return this.createdOn;
	}

	public Attempt setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public boolean isSetCreatedOn() {
		return (createdOn > 0L);
	}

	public long getCompleteOn() {
		return this.completeOn;
	}

	public Attempt setCompleteOn(long completeOn) {
		this.completeOn = completeOn;
		return this;
	}

	public boolean isSetCompleteOn() {
		return (completeOn > 0L);
	}

	public long getGradedOn() {
		return this.gradedOn;
	}

	public Attempt setGradedOn(long gradedOn) {
		this.gradedOn = gradedOn;
		return this;
	}

	public boolean isSetGradedOn() {
		return (gradedOn > 0L);
	}

	public com.workmarket.thrift.core.User getGrader() {
		return this.grader;
	}

	public Attempt setGrader(com.workmarket.thrift.core.User grader) {
		this.grader = grader;
		return this;
	}

	public boolean isSetGrader() {
		return this.grader != null;
	}

	public double getScore() {
		return this.score;
	}

	public Attempt setScore(double score) {
		this.score = score;
		return this;
	}

	public boolean isSetScore() {
		return (score > 0D);
	}

	public boolean isPassed() {
		return this.passed;
	}

	public Attempt setPassed(boolean passed) {
		this.passed = passed;
		return this;
	}

	public long getDuration() {
		return this.duration;
	}

	public Attempt setDuration(long duration) {
		this.duration = duration;
		return this;
	}

	public boolean isSetDuration() {
		return (duration > 0L);
	}

	public int getTotalAttemptsCount() {
		return this.totalAttemptsCount;
	}

	public Attempt setTotalAttemptsCount(int totalAttemptsCount) {
		this.totalAttemptsCount = totalAttemptsCount;
		return this;
	}

	public boolean isSetTotalAttemptsCount() {
		return (totalAttemptsCount > 0);
	}

	public boolean isScopedToWork() {
		return this.scopedToWork;
	}

	public Attempt setScopedToWork(boolean scopedToWork) {
		this.scopedToWork = scopedToWork;
		return this;
	}

	public AttemptWorkScope getWork() {
		return this.work;
	}

	public Attempt setWork(AttemptWorkScope work) {
		this.work = work;
		return this;
	}

	public boolean isSetWork() {
		return this.work != null;
	}

	public boolean isRespondedToAllItems() {
		return this.respondedToAllItems;
	}

	public Attempt setRespondedToAllItems(boolean respondedToAllItems) {
		this.respondedToAllItems = respondedToAllItems;
		return this;
	}

	@SuppressWarnings("unchecked")
	public List<Item> getItemsForResponses() {
		return (List<Item>) CollectionUtilities.newListPropertyProjection(responses, "item");
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Attempt)
			return this.equals((Attempt) that);
		return false;
	}

	private boolean equals(Attempt that) {
		if (that == null)
			return false;

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
				return false;
		}

		boolean this_present_user = true && this.isSetUser();
		boolean that_present_user = true && that.isSetUser();
		if (this_present_user || that_present_user) {
			if (!(this_present_user && that_present_user))
				return false;
			if (!this.user.equals(that.user))
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

		boolean this_present_status = true && this.isSetStatus();
		boolean that_present_status = true && that.isSetStatus();
		if (this_present_status || that_present_status) {
			if (!(this_present_status && that_present_status))
				return false;
			if (!this.status.equals(that.status))
				return false;
		}

		boolean this_present_createdOn = true;
		boolean that_present_createdOn = true;
		if (this_present_createdOn || that_present_createdOn) {
			if (!(this_present_createdOn && that_present_createdOn))
				return false;
			if (this.createdOn != that.createdOn)
				return false;
		}

		boolean this_present_completeOn = true;
		boolean that_present_completeOn = true;
		if (this_present_completeOn || that_present_completeOn) {
			if (!(this_present_completeOn && that_present_completeOn))
				return false;
			if (this.completeOn != that.completeOn)
				return false;
		}

		boolean this_present_gradedOn = true;
		boolean that_present_gradedOn = true;
		if (this_present_gradedOn || that_present_gradedOn) {
			if (!(this_present_gradedOn && that_present_gradedOn))
				return false;
			if (this.gradedOn != that.gradedOn)
				return false;
		}

		boolean this_present_grader = true && this.isSetGrader();
		boolean that_present_grader = true && that.isSetGrader();
		if (this_present_grader || that_present_grader) {
			if (!(this_present_grader && that_present_grader))
				return false;
			if (!this.grader.equals(that.grader))
				return false;
		}

		boolean this_present_score = true;
		boolean that_present_score = true;
		if (this_present_score || that_present_score) {
			if (!(this_present_score && that_present_score))
				return false;
			if (this.score != that.score)
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

		boolean this_present_duration = true;
		boolean that_present_duration = true;
		if (this_present_duration || that_present_duration) {
			if (!(this_present_duration && that_present_duration))
				return false;
			if (this.duration != that.duration)
				return false;
		}

		boolean this_present_totalAttemptsCount = true;
		boolean that_present_totalAttemptsCount = true;
		if (this_present_totalAttemptsCount || that_present_totalAttemptsCount) {
			if (!(this_present_totalAttemptsCount && that_present_totalAttemptsCount))
				return false;
			if (this.totalAttemptsCount != that.totalAttemptsCount)
				return false;
		}

		boolean this_present_scopedToWork = true;
		boolean that_present_scopedToWork = true;
		if (this_present_scopedToWork || that_present_scopedToWork) {
			if (!(this_present_scopedToWork && that_present_scopedToWork))
				return false;
			if (this.scopedToWork != that.scopedToWork)
				return false;
		}

		boolean this_present_work = true && this.isSetWork();
		boolean that_present_work = true && that.isSetWork();
		if (this_present_work || that_present_work) {
			if (!(this_present_work && that_present_work))
				return false;
			if (!this.work.equals(that.work))
				return false;
		}

		boolean this_present_respondedToAllItems = true;
		boolean that_present_respondedToAllItems = true;
		if (this_present_respondedToAllItems || that_present_respondedToAllItems) {
			if (!(this_present_respondedToAllItems && that_present_respondedToAllItems))
				return false;
			if (this.respondedToAllItems != that.respondedToAllItems)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_user = true && (isSetUser());
		builder.append(present_user);
		if (present_user)
			builder.append(user);

		boolean present_responses = true && (isSetResponses());
		builder.append(present_responses);
		if (present_responses)
			builder.append(responses);

		boolean present_status = true && (isSetStatus());
		builder.append(present_status);
		if (present_status)
			builder.append(status);

		boolean present_createdOn = true;
		builder.append(present_createdOn);
		if (present_createdOn)
			builder.append(createdOn);

		boolean present_completeOn = true;
		builder.append(present_completeOn);
		if (present_completeOn)
			builder.append(completeOn);

		boolean present_gradedOn = true;
		builder.append(present_gradedOn);
		if (present_gradedOn)
			builder.append(gradedOn);

		boolean present_grader = true && (isSetGrader());
		builder.append(present_grader);
		if (present_grader)
			builder.append(grader);

		boolean present_score = true;
		builder.append(present_score);
		if (present_score)
			builder.append(score);

		boolean present_passed = true;
		builder.append(present_passed);
		if (present_passed)
			builder.append(passed);

		boolean present_duration = true;
		builder.append(present_duration);
		if (present_duration)
			builder.append(duration);

		boolean present_totalAttemptsCount = true;
		builder.append(present_totalAttemptsCount);
		if (present_totalAttemptsCount)
			builder.append(totalAttemptsCount);

		boolean present_scopedToWork = true;
		builder.append(present_scopedToWork);
		if (present_scopedToWork)
			builder.append(scopedToWork);

		boolean present_work = true && (isSetWork());
		builder.append(present_work);
		if (present_work)
			builder.append(work);

		boolean present_respondedToAllItems = true;
		builder.append(present_respondedToAllItems);
		if (present_respondedToAllItems)
			builder.append(respondedToAllItems);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Attempt(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("user:");
		if (this.user == null) {
			sb.append("null");
		} else {
			sb.append(this.user);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("responses:");
		if (this.responses == null) {
			sb.append("null");
		} else {
			sb.append(this.responses);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("status:");
		if (this.status == null) {
			sb.append("null");
		} else {
			sb.append(this.status);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("createdOn:");
		sb.append(this.createdOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("completeOn:");
		sb.append(this.completeOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("gradedOn:");
		sb.append(this.gradedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("grader:");
		if (this.grader == null) {
			sb.append("null");
		} else {
			sb.append(this.grader);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("score:");
		sb.append(this.score);
		first = false;
		if (!first) sb.append(", ");
		sb.append("passed:");
		sb.append(this.passed);
		first = false;
		if (!first) sb.append(", ");
		sb.append("duration:");
		sb.append(this.duration);
		first = false;
		if (!first) sb.append(", ");
		sb.append("totalAttemptsCount:");
		sb.append(this.totalAttemptsCount);
		first = false;
		if (!first) sb.append(", ");
		sb.append("scopedToWork:");
		sb.append(this.scopedToWork);
		first = false;
		if (!first) sb.append(", ");
		sb.append("work:");
		if (this.work == null) {
			sb.append("null");
		} else {
			sb.append(this.work);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("respondedToAllItems:");
		sb.append(this.respondedToAllItems);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}