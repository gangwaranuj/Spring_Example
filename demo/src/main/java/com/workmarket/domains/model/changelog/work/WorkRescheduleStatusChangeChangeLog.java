package com.workmarket.domains.model.changelog.work;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@DiscriminatorValue(WorkChangeLog.WORK_RESCHEDULE_REQUESTED)
@AuditChanges
public class WorkRescheduleStatusChangeChangeLog extends WorkRescheduleRequestedChangeLog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1874005079058877878L;
	private ApprovalStatus oldApprovalStatus;
    private ApprovalStatus newApprovalStatus;

	public WorkRescheduleStatusChangeChangeLog() {}


	@Column(name = "old_work_negotiation_approval_status", nullable = true)
	public ApprovalStatus getOldApprovalStatus() {
		return this.oldApprovalStatus;
	}
	public void setOldApprovalStatus(ApprovalStatus oldApprovalStatus) {
		this.oldApprovalStatus = oldApprovalStatus;
	}

	@Column(name = "new_work_negotiation_approval_status", nullable = true)
	public ApprovalStatus getNewApprovalStatus() {
		return this.newApprovalStatus;
	}
	public void setNewApprovalStatus(ApprovalStatus newApprovalStatus) {
		this.newApprovalStatus = newApprovalStatus;
	}

	@Transient
	public static String getDescription() {
		return "Counteroffer requested";
	}
}
