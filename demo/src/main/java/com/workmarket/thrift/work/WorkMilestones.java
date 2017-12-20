package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class WorkMilestones implements Serializable {
	private static final long serialVersionUID = 1L;

	private long sentOn;
	private long acceptedOn;
	private long declinedOn;
	private long activeOn;
	private long completeOn;
	private long cancelledOn;
	private long finishedOn;
	private long paidOn;
	private long voidOn;
	private long refundedOn;
	private long closedOn;
	private long createdOn;
	private long draftOn;
	private long dueOn;

	public WorkMilestones() {
	}

	public WorkMilestones(
			long sentOn,
			long acceptedOn,
			long declinedOn,
			long activeOn,
			long completeOn,
			long cancelledOn,
			long finishedOn,
			long paidOn,
			long voidOn,
			long refundedOn,
			long closedOn,
			long createdOn,
			long draftOn,
			long dueOn) {
		this();
		this.sentOn = sentOn;
		this.acceptedOn = acceptedOn;
		this.declinedOn = declinedOn;
		this.activeOn = activeOn;
		this.completeOn = completeOn;
		this.cancelledOn = cancelledOn;
		this.finishedOn = finishedOn;
		this.paidOn = paidOn;
		this.voidOn = voidOn;
		this.refundedOn = refundedOn;
		this.closedOn = closedOn;
		this.createdOn = createdOn;
		this.draftOn = draftOn;
		this.dueOn = dueOn;
	}

	public long getSentOn() {
		return this.sentOn;
	}

	public WorkMilestones setSentOn(long sentOn) {
		this.sentOn = sentOn;
		return this;
	}

	public long getAcceptedOn() {
		return this.acceptedOn;
	}

	public WorkMilestones setAcceptedOn(long acceptedOn) {
		this.acceptedOn = acceptedOn;
		return this;
	}

	public long getDeclinedOn() {
		return this.declinedOn;
	}

	public WorkMilestones setDeclinedOn(long declinedOn) {
		this.declinedOn = declinedOn;
		return this;
	}

	public long getActiveOn() {
		return this.activeOn;
	}

	public WorkMilestones setActiveOn(long activeOn) {
		this.activeOn = activeOn;
		return this;
	}

	public long getCompleteOn() {
		return this.completeOn;
	}

	public WorkMilestones setCompleteOn(long completeOn) {
		this.completeOn = completeOn;
		return this;
	}

	public long getCancelledOn() {
		return this.cancelledOn;
	}

	public WorkMilestones setCancelledOn(long cancelledOn) {
		this.cancelledOn = cancelledOn;
		return this;
	}

	public long getFinishedOn() {
		return this.finishedOn;
	}

	public WorkMilestones setFinishedOn(long finishedOn) {
		this.finishedOn = finishedOn;
		return this;
	}

	public long getPaidOn() {
		return this.paidOn;
	}

	public WorkMilestones setPaidOn(long paidOn) {
		this.paidOn = paidOn;
		return this;
	}

	public long getVoidOn() {
		return this.voidOn;
	}

	public WorkMilestones setVoidOn(long voidOn) {
		this.voidOn = voidOn;
		return this;
	}

	public long getRefundedOn() {
		return this.refundedOn;
	}

	public WorkMilestones setRefundedOn(long refundedOn) {
		this.refundedOn = refundedOn;
		return this;
	}

	public long getClosedOn() {
		return this.closedOn;
	}

	public WorkMilestones setClosedOn(long closedOn) {
		this.closedOn = closedOn;
		return this;
	}

	public long getCreatedOn() {
		return this.createdOn;
	}

	public WorkMilestones setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public long getDraftOn() {
		return this.draftOn;
	}

	public WorkMilestones setDraftOn(long draftOn) {
		this.draftOn = draftOn;
		return this;
	}

	public long getDueOn() {
		return this.dueOn;
	}

	public WorkMilestones setDueOn(long dueOn) {
		this.dueOn = dueOn;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkMilestones)
			return this.equals((WorkMilestones) that);
		return false;
	}

	private boolean equals(WorkMilestones that) {
		if (that == null)
			return false;

		boolean this_present_sentOn = true;
		boolean that_present_sentOn = true;
		if (this_present_sentOn || that_present_sentOn) {
			if (!(this_present_sentOn && that_present_sentOn))
				return false;
			if (this.sentOn != that.sentOn)
				return false;
		}

		boolean this_present_acceptedOn = true;
		boolean that_present_acceptedOn = true;
		if (this_present_acceptedOn || that_present_acceptedOn) {
			if (!(this_present_acceptedOn && that_present_acceptedOn))
				return false;
			if (this.acceptedOn != that.acceptedOn)
				return false;
		}

		boolean this_present_declinedOn = true;
		boolean that_present_declinedOn = true;
		if (this_present_declinedOn || that_present_declinedOn) {
			if (!(this_present_declinedOn && that_present_declinedOn))
				return false;
			if (this.declinedOn != that.declinedOn)
				return false;
		}

		boolean this_present_activeOn = true;
		boolean that_present_activeOn = true;
		if (this_present_activeOn || that_present_activeOn) {
			if (!(this_present_activeOn && that_present_activeOn))
				return false;
			if (this.activeOn != that.activeOn)
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

		boolean this_present_cancelledOn = true;
		boolean that_present_cancelledOn = true;
		if (this_present_cancelledOn || that_present_cancelledOn) {
			if (!(this_present_cancelledOn && that_present_cancelledOn))
				return false;
			if (this.cancelledOn != that.cancelledOn)
				return false;
		}

		boolean this_present_finishedOn = true;
		boolean that_present_finishedOn = true;
		if (this_present_finishedOn || that_present_finishedOn) {
			if (!(this_present_finishedOn && that_present_finishedOn))
				return false;
			if (this.finishedOn != that.finishedOn)
				return false;
		}

		boolean this_present_paidOn = true;
		boolean that_present_paidOn = true;
		if (this_present_paidOn || that_present_paidOn) {
			if (!(this_present_paidOn && that_present_paidOn))
				return false;
			if (this.paidOn != that.paidOn)
				return false;
		}

		boolean this_present_voidOn = true;
		boolean that_present_voidOn = true;
		if (this_present_voidOn || that_present_voidOn) {
			if (!(this_present_voidOn && that_present_voidOn))
				return false;
			if (this.voidOn != that.voidOn)
				return false;
		}

		boolean this_present_refundedOn = true;
		boolean that_present_refundedOn = true;
		if (this_present_refundedOn || that_present_refundedOn) {
			if (!(this_present_refundedOn && that_present_refundedOn))
				return false;
			if (this.refundedOn != that.refundedOn)
				return false;
		}

		boolean this_present_closedOn = true;
		boolean that_present_closedOn = true;
		if (this_present_closedOn || that_present_closedOn) {
			if (!(this_present_closedOn && that_present_closedOn))
				return false;
			if (this.closedOn != that.closedOn)
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

		boolean this_present_draftOn = true;
		boolean that_present_draftOn = true;
		if (this_present_draftOn || that_present_draftOn) {
			if (!(this_present_draftOn && that_present_draftOn))
				return false;
			if (this.draftOn != that.draftOn)
				return false;
		}

		boolean this_present_dueOn = true;
		boolean that_present_dueOn = true;
		if (this_present_dueOn || that_present_dueOn) {
			if (!(this_present_dueOn && that_present_dueOn))
				return false;
			if (this.dueOn != that.dueOn)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_sentOn = true;
		builder.append(present_sentOn);
		if (present_sentOn)
			builder.append(sentOn);

		boolean present_acceptedOn = true;
		builder.append(present_acceptedOn);
		if (present_acceptedOn)
			builder.append(acceptedOn);

		boolean present_declinedOn = true;
		builder.append(present_declinedOn);
		if (present_declinedOn)
			builder.append(declinedOn);

		boolean present_activeOn = true;
		builder.append(present_activeOn);
		if (present_activeOn)
			builder.append(activeOn);

		boolean present_completeOn = true;
		builder.append(present_completeOn);
		if (present_completeOn)
			builder.append(completeOn);

		boolean present_cancelledOn = true;
		builder.append(present_cancelledOn);
		if (present_cancelledOn)
			builder.append(cancelledOn);

		boolean present_finishedOn = true;
		builder.append(present_finishedOn);
		if (present_finishedOn)
			builder.append(finishedOn);

		boolean present_paidOn = true;
		builder.append(present_paidOn);
		if (present_paidOn)
			builder.append(paidOn);

		boolean present_voidOn = true;
		builder.append(present_voidOn);
		if (present_voidOn)
			builder.append(voidOn);

		boolean present_refundedOn = true;
		builder.append(present_refundedOn);
		if (present_refundedOn)
			builder.append(refundedOn);

		boolean present_closedOn = true;
		builder.append(present_closedOn);
		if (present_closedOn)
			builder.append(closedOn);

		boolean present_createdOn = true;
		builder.append(present_createdOn);
		if (present_createdOn)
			builder.append(createdOn);

		boolean present_draftOn = true;
		builder.append(present_draftOn);
		if (present_draftOn)
			builder.append(draftOn);

		boolean present_dueOn = true;
		builder.append(present_dueOn);
		if (present_dueOn)
			builder.append(dueOn);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkMilestones(");
		boolean first = true;

		sb.append("sentOn:");
		sb.append(this.sentOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("acceptedOn:");
		sb.append(this.acceptedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("declinedOn:");
		sb.append(this.declinedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("activeOn:");
		sb.append(this.activeOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("completeOn:");
		sb.append(this.completeOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("cancelledOn:");
		sb.append(this.cancelledOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("finishedOn:");
		sb.append(this.finishedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("paidOn:");
		sb.append(this.paidOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("voidOn:");
		sb.append(this.voidOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("refundedOn:");
		sb.append(this.refundedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("closedOn:");
		sb.append(this.closedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("createdOn:");
		sb.append(this.createdOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("draftOn:");
		sb.append(this.draftOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("dueOn:");
		sb.append(this.dueOn);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}