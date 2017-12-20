package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class ApprovableVerifiableEntity extends VerifiableEntity {

	private static final long serialVersionUID = 1L;

	private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

	@Column(name = "approval_status")
	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	@Transient
	public boolean isApproved() {
		return ApprovalStatus.APPROVED.equals(approvalStatus);
	}

	@Transient
	public boolean isPendingApproval() {
		return ApprovalStatus.PENDING.equals(approvalStatus);
	}

	@Transient
	public boolean isPendingRemoval() {
		return ApprovalStatus.PENDING_REMOVAL.equals(approvalStatus);
	}

	@Transient
	public boolean isRemoved() {
		return ApprovalStatus.REMOVED.equals(approvalStatus);
	}

	@Transient
	public boolean isNotReady() {
		return ApprovalStatus.NOT_READY.equals(approvalStatus);
	}

	@Transient
	public boolean isDeclined() {
		return ApprovalStatus.DECLINED.equals(approvalStatus);
	}

	@Transient
	public boolean isOptOut() {
		return ApprovalStatus.OPT_OUT.equals(approvalStatus);
	}

}
