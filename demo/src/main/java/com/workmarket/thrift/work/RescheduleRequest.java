package com.workmarket.thrift.work;

import com.workmarket.domains.work.service.audit.WorkActionRequest;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class RescheduleRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private WorkActionRequest workAction;
	private com.workmarket.thrift.core.TimeRange assignmentTimeRange;

	public RescheduleRequest() {
	}

	public RescheduleRequest(
			WorkActionRequest workAction,
			com.workmarket.thrift.core.TimeRange assignmentTimeRange) {
		this();
		this.workAction = workAction;
		this.assignmentTimeRange = assignmentTimeRange;
	}

	public WorkActionRequest getWorkAction() {
		return this.workAction;
	}

	public RescheduleRequest setWorkAction(WorkActionRequest workAction) {
		this.workAction = workAction;
		return this;
	}

	public boolean isSetWorkAction() {
		return this.workAction != null;
	}

	public com.workmarket.thrift.core.TimeRange getAssignmentTimeRange() {
		return this.assignmentTimeRange;
	}

	public RescheduleRequest setAssignmentTimeRange(com.workmarket.thrift.core.TimeRange assignmentTimeRange) {
		this.assignmentTimeRange = assignmentTimeRange;
		return this;
	}

	public boolean isSetAssignmentTimeRange() {
		return this.assignmentTimeRange != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RescheduleRequest)
			return this.equals((RescheduleRequest) that);
		return false;
	}

	private boolean equals(RescheduleRequest that) {
		if (that == null)
			return false;

		boolean this_present_workAction = true && this.isSetWorkAction();
		boolean that_present_workAction = true && that.isSetWorkAction();
		if (this_present_workAction || that_present_workAction) {
			if (!(this_present_workAction && that_present_workAction))
				return false;
			if (!this.workAction.equals(that.workAction))
				return false;
		}

		boolean this_present_assignmentTimeRange = true && this.isSetAssignmentTimeRange();
		boolean that_present_assignmentTimeRange = true && that.isSetAssignmentTimeRange();
		if (this_present_assignmentTimeRange || that_present_assignmentTimeRange) {
			if (!(this_present_assignmentTimeRange && that_present_assignmentTimeRange))
				return false;
			if (!this.assignmentTimeRange.equals(that.assignmentTimeRange))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_workAction = true && (isSetWorkAction());
		builder.append(present_workAction);
		if (present_workAction)
			builder.append(workAction);

		boolean present_assignmentTimeRange = true && (isSetAssignmentTimeRange());
		builder.append(present_assignmentTimeRange);
		if (present_assignmentTimeRange)
			builder.append(assignmentTimeRange);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RescheduleRequest(");
		boolean first = true;

		sb.append("workAction:");
		if (this.workAction == null) {
			sb.append("null");
		} else {
			sb.append(this.workAction);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("assignmentTimeRange:");
		if (this.assignmentTimeRange == null) {
			sb.append("null");
		} else {
			sb.append(this.assignmentTimeRange);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

