package com.workmarket.thrift.work;

import com.workmarket.domains.work.service.audit.WorkActionRequest;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class VoidWorkRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String note;
	private WorkActionRequest workAction;

	public VoidWorkRequest() {
	}

	public VoidWorkRequest(String note, WorkActionRequest workAction) {
		this();
		this.note = note;
		this.workAction = workAction;
	}

	public String getNote() {
		return this.note;
	}

	public VoidWorkRequest setNote(String note) {
		this.note = note;
		return this;
	}

	public boolean isSetNote() {
		return this.note != null;
	}

	public WorkActionRequest getWorkAction() {
		return this.workAction;
	}

	public VoidWorkRequest setWorkAction(WorkActionRequest workAction) {
		this.workAction = workAction;
		return this;
	}

	public boolean isSetWorkAction() {
		return this.workAction != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof VoidWorkRequest)
			return this.equals((VoidWorkRequest) that);
		return false;
	}

	private boolean equals(VoidWorkRequest that) {
		if (that == null)
			return false;

		boolean this_present_note = true && this.isSetNote();
		boolean that_present_note = true && that.isSetNote();
		if (this_present_note || that_present_note) {
			if (!(this_present_note && that_present_note))
				return false;
			if (!this.note.equals(that.note))
				return false;
		}

		boolean this_present_workAction = true && this.isSetWorkAction();
		boolean that_present_workAction = true && that.isSetWorkAction();
		if (this_present_workAction || that_present_workAction) {
			if (!(this_present_workAction && that_present_workAction))
				return false;
			if (!this.workAction.equals(that.workAction))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_note = true && (isSetNote());
		builder.append(present_note);
		if (present_note)
			builder.append(note);

		boolean present_workAction = true && (isSetWorkAction());
		builder.append(present_workAction);
		if (present_workAction)
			builder.append(workAction);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("VoidWorkRequest(");
		boolean first = true;

		sb.append("note:");
		if (this.note == null) {
			sb.append("null");
		} else {
			sb.append(this.note);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("workAction:");
		if (this.workAction == null) {
			sb.append("null");
		} else {
			sb.append(this.workAction);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}