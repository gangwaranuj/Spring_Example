package com.workmarket.domains.model.summary.work;

import com.workmarket.domains.model.WorkStatusType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity(name = "workMilestones")
@Table(name = "work_milestones")
public class WorkMilestones implements Serializable{

	private static final long serialVersionUID = -7101149244977588835L;
	private Long workId;

	private Calendar createdOn;
	private Calendar draftOn;
	private Calendar sentOn;
	private Calendar holdOn;
	private Calendar acceptedOn;
	private Calendar declinedOn;
	private Calendar activeOn;
	private Calendar completeOn;
	private Calendar closedOn;
	private Calendar cancelledOn;
	private Calendar exceptionOn;
	private Calendar paidOn;
	private Calendar voidOn;
	private Calendar refundedOn;
	private Calendar inProgressOn;
	private Calendar dueOn;
	private boolean paidWithPaymentTerms = false;
	private boolean latePayment = false;
	private Long companyId;

	@Id
	@Column(name = "work_id", nullable = false)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@Column(name = "created_on", nullable = true)
	public Calendar getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
	}

	@Column(name = "draft_on", nullable = true)
	public Calendar getDraftOn() {
		return draftOn;
	}

	public void setDraftOn(Calendar draftOn) {
		this.draftOn = draftOn;
	}

	@Column(name = "sent_on", nullable = true)
	public Calendar getSentOn() {
		return sentOn;
	}

	public void setSentOn(Calendar sentOn) {
		this.sentOn = sentOn;
	}

	@Column(name = "hold_on", nullable = true)
	public Calendar getHoldOn() {
		return holdOn;
	}

	public void setHoldOn(Calendar holdOn) {
		this.holdOn = holdOn;
	}

	@Column(name = "accepted_on", nullable = true)
	public Calendar getAcceptedOn() {
		return acceptedOn;
	}

	public void setAcceptedOn(Calendar acceptedOn) {
		this.acceptedOn = acceptedOn;
	}

	@Column(name = "declined_on", nullable = true)
	public Calendar getDeclinedOn() {
		return declinedOn;
	}

	public void setDeclinedOn(Calendar declinedOn) {
		this.declinedOn = declinedOn;
	}

	@Column(name = "active_on", nullable = true)
	public Calendar getActiveOn() {
		return activeOn;
	}

	public void setActiveOn(Calendar activeOn) {
		this.activeOn = activeOn;
	}

	@Column(name = "complete_on", nullable = true)
	public Calendar getCompleteOn() {
		return completeOn;
	}

	public void setCompleteOn(Calendar completeOn) {
		this.completeOn = completeOn;
	}

	@Column(name = "closed_on", nullable = true)
	public Calendar getClosedOn() {
		return closedOn;
	}

	public void setClosedOn(Calendar closedOn) {
		this.closedOn = closedOn;
	}

	@Column(name = "cancelled_on", nullable = true)
	public Calendar getCancelledOn() {
		return cancelledOn;
	}

	public void setCancelledOn(Calendar cancelledOn) {
		this.cancelledOn = cancelledOn;
	}

	@Column(name = "exception_on", nullable = true)
	public Calendar getExceptionOn() {
		return exceptionOn;
	}

	public void setExceptionOn(Calendar exceptionOn) {
		this.exceptionOn = exceptionOn;
	}

	@Column(name = "refunded_on", nullable = true)
	public Calendar getRefundedOn() {
		return refundedOn;
	}

	public void setRefundedOn(Calendar refundedOn) {
		this.refundedOn = refundedOn;
	}

	@Column(name = "paid_on", nullable = true)
	public Calendar getPaidOn() {
		return paidOn;
	}

	public void setPaidOn(Calendar paidOn) {
		this.paidOn = paidOn;
	}

	@Column(name = "void_on", nullable = true)
	public Calendar getVoidOn() {
		return voidOn;
	}

	public void setVoidOn(Calendar voidOn) {
		this.voidOn = voidOn;
	}
	
	@Column(name = "inprogress_on", nullable = true)
	public Calendar getInProgressOn() {
		return inProgressOn;
	}

	public void setInProgressOn(Calendar inprogressOn) {
		this.inProgressOn = inprogressOn;
	}

	@Column(name = "due_on", nullable = true)
	public Calendar getDueOn() {
		return dueOn;
	}

	public void setDueOn(Calendar dueOn) {
		this.dueOn = dueOn;
	}

	@Column(name = "payment_terms_enabled", nullable = false)
	public boolean isPaidWithPaymentTerms() {
		return paidWithPaymentTerms;
	}

	public void setPaidWithPaymentTerms(boolean paidWithPaymentTerms) {
		this.paidWithPaymentTerms = paidWithPaymentTerms;
	}

	@Column(name = "company_id", nullable = false)
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Column(name = "late_payment", nullable = false)
	public boolean isLatePayment() {
		return latePayment;
	}

	public void setLatePayment(boolean latePayment) {
		this.latePayment = latePayment;
	}

	@Transient
	public Calendar getMilestonesFieldFromWorkStatus(WorkStatusType workStatusType) {
		if (workStatusType == null) return null;
		if (WorkStatusType.DRAFT.equals(workStatusType.getCode())) {
			return getCreatedOn();
		}
		if (WorkStatusType.SENT.equals(workStatusType.getCode())) {
			return getSentOn();
		}
		if (WorkStatusType.ACTIVE.equals(workStatusType.getCode())) {
			return getAcceptedOn();
		}
		if (WorkStatusType.COMPLETE.equals(workStatusType.getCode())) {
			return getCompleteOn();
		}
		if (WorkStatusType.CLOSED.equals(workStatusType.getCode())) {
			return getClosedOn();
		}
		if (WorkStatusType.PAYMENT_PENDING.equals(workStatusType.getCode())) {
			return getClosedOn();
		}
		if (WorkStatusType.CANCELLED_PAYMENT_PENDING.equals(workStatusType.getCode())) {
			return getClosedOn();
		}
		if (WorkStatusType.VOID.equals(workStatusType.getCode())) {
			return getVoidOn();
		}
		if (WorkStatusType.CANCELLED.equals(workStatusType.getCode())) {
			return getCancelledOn();
		}
		if (WorkStatusType.CANCELLED_WITH_PAY.equals(workStatusType.getCode())) {
			return getCancelledOn();
		}
		if (WorkStatusType.PAID.equals(workStatusType.getCode())) {
			return getPaidOn();
		}
		if (WorkStatusType.ABANDONED.equals(workStatusType.getCode())) {
			return getExceptionOn();
		}
		if (WorkStatusType.EXCEPTION.equals(workStatusType.getCode())) {
			return getExceptionOn();
		}
		if (WorkStatusType.DECLINED.equals(workStatusType.getCode())) {
			return getDeclinedOn();
		}
		return null;
	}
}
