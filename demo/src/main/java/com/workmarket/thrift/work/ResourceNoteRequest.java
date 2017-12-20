package com.workmarket.thrift.work;

import com.workmarket.domains.work.service.audit.WorkActionRequest;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class ResourceNoteRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private WorkActionRequest workAction;
	private String note;
	private ResourceNoteActionType actionType;

	public ResourceNoteRequest() {
	}

	public WorkActionRequest getWorkAction() {
		return this.workAction;
	}

	public ResourceNoteRequest setWorkAction(WorkActionRequest workAction) {
		this.workAction = workAction;
		return this;
	}

	public boolean isSetWorkAction() {
		return this.workAction != null;
	}

	public String getNote() {
		return this.note;
	}

	public ResourceNoteRequest setNote(String note) {
		this.note = note;
		return this;
	}

	public boolean isSetNote() {
		return this.note != null;
	}

	public ResourceNoteActionType getActionType() {
		return this.actionType;
	}

	public ResourceNoteRequest setActionType(ResourceNoteActionType actionType) {
		this.actionType = actionType;
		return this;
	}

	public boolean isSetActionType() {
		return this.actionType != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ResourceNoteRequest)
			return this.equals((ResourceNoteRequest) that);
		return false;
	}

	private boolean equals(ResourceNoteRequest that) {
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

		boolean this_present_note = true && this.isSetNote();
		boolean that_present_note = true && that.isSetNote();
		if (this_present_note || that_present_note) {
			if (!(this_present_note && that_present_note))
				return false;
			if (!this.note.equals(that.note))
				return false;
		}

		boolean this_present_actionType = true && this.isSetActionType();
		boolean that_present_actionType = true && that.isSetActionType();
		if (this_present_actionType || that_present_actionType) {
			if (!(this_present_actionType && that_present_actionType))
				return false;
			if (!this.actionType.equals(that.actionType))
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

		boolean present_note = true && (isSetNote());
		builder.append(present_note);
		if (present_note)
			builder.append(note);

		boolean present_actionType = true && (isSetActionType());
		builder.append(present_actionType);
		if (present_actionType)
			builder.append(actionType.getValue());

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ResourceNoteRequest(");
		boolean first = true;

		sb.append("workAction:");
		if (this.workAction == null) {
			sb.append("null");
		} else {
			sb.append(this.workAction);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("note:");
		if (this.note == null) {
			sb.append("null");
		} else {
			sb.append(this.note);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("actionType:");
		if (this.actionType == null) {
			sb.append("null");
		} else {
			sb.append(this.actionType);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

