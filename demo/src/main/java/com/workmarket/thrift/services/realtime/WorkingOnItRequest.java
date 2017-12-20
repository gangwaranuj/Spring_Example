package com.workmarket.thrift.services.realtime;

import com.workmarket.domains.work.service.audit.WorkActionRequest;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class WorkingOnItRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private WorkActionRequest workAction;
	private WorkingOnItStatusType status;

	public WorkingOnItRequest() {
		this.status = com.workmarket.thrift.services.realtime.WorkingOnItStatusType.OFF;
	}

	public WorkingOnItRequest(WorkActionRequest workAction, WorkingOnItStatusType status) {
		this();
		this.workAction = workAction;
		this.status = status;
	}

	public WorkActionRequest getWorkAction() {
		return this.workAction;
	}

	public WorkingOnItRequest setWorkAction(WorkActionRequest workAction) {
		this.workAction = workAction;
		return this;
	}

	public boolean isSetWorkAction() {
		return this.workAction != null;
	}

	public WorkingOnItStatusType getStatus() {
		return this.status;
	}

	public WorkingOnItRequest setStatus(WorkingOnItStatusType status) {
		this.status = status;
		return this;
	}

	public boolean isSetStatus() {
		return this.status != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkingOnItRequest)
			return this.equals((WorkingOnItRequest) that);
		return false;
	}

	private boolean equals(WorkingOnItRequest that) {
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

		boolean this_present_status = true && this.isSetStatus();
		boolean that_present_status = true && that.isSetStatus();
		if (this_present_status || that_present_status) {
			if (!(this_present_status && that_present_status))
				return false;
			if (!this.status.equals(that.status))
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

		boolean present_status = true && (isSetStatus());
		builder.append(present_status);
		if (present_status)
			builder.append(status.getValue());

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkingOnItRequest(");
		boolean first = true;

		sb.append("workAction:");
		if (this.workAction == null) {
			sb.append("null");
		} else {
			sb.append(this.workAction);
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
		sb.append(")");
		return sb.toString();
	}
}