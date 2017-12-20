package com.workmarket.thrift.work;

import com.workmarket.domains.work.service.audit.WorkActionRequest;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class DeclineWorkOfferRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private WorkActionRequest workAction;
	private String note;
	private DeclineWorkActionType actionCode;

	public DeclineWorkOfferRequest() {
	}

	public DeclineWorkOfferRequest(WorkActionRequest workAction, String note, DeclineWorkActionType actionCode) {
		this();
		this.workAction = workAction;
		this.note = note;
		this.actionCode = actionCode;
	}

	public WorkActionRequest getWorkAction() {
		return this.workAction;
	}

	public DeclineWorkOfferRequest setWorkAction(WorkActionRequest workAction) {
		this.workAction = workAction;
		return this;
	}

	public boolean isSetWorkAction() {
		return this.workAction != null;
	}

	public String getNote() {
		return this.note;
	}

	public DeclineWorkOfferRequest setNote(String note) {
		this.note = note;
		return this;
	}

	public boolean isSetNote() {
		return this.note != null;
	}

	public DeclineWorkActionType getActionCode() {
		return this.actionCode;
	}

	public DeclineWorkOfferRequest setActionCode(DeclineWorkActionType actionCode) {
		this.actionCode = actionCode;
		return this;
	}

	public boolean isSetActionCode() {
		return this.actionCode != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof DeclineWorkOfferRequest)
			return this.equals((DeclineWorkOfferRequest) that);
		return false;
	}

	private boolean equals(DeclineWorkOfferRequest that) {
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

		boolean this_present_actionCode = true && this.isSetActionCode();
		boolean that_present_actionCode = true && that.isSetActionCode();
		if (this_present_actionCode || that_present_actionCode) {
			if (!(this_present_actionCode && that_present_actionCode))
				return false;
			if (!this.actionCode.equals(that.actionCode))
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

		boolean present_actionCode = true && (isSetActionCode());
		builder.append(present_actionCode);
		if (present_actionCode)
			builder.append(actionCode.getValue());

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DeclineWorkOfferRequest(");
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
		sb.append("actionCode:");
		if (this.actionCode == null) {
			sb.append("null");
		} else {
			sb.append(this.actionCode);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}