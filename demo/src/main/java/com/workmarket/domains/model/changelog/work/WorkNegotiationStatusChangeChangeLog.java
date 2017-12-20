package com.workmarket.domains.model.changelog.work;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@DiscriminatorValue(WorkChangeLog.WORK_NEGOTIATION_STATUS_CHANGE)
@AuditChanges
public class WorkNegotiationStatusChangeChangeLog extends WorkNegotiationRequestedChangeLog {

	private static final long serialVersionUID = -8931865810728000360L;
	private ApprovalStatus oldApprovalStatus;
	private ApprovalStatus newApprovalStatus;

	public WorkNegotiationStatusChangeChangeLog() {}
	public WorkNegotiationStatusChangeChangeLog(Long work, Long actor, Long masqueradeActor, Long onBehalfOfActor, AbstractWorkNegotiation negotiation, ApprovalStatus oldApprovalStatus, ApprovalStatus newApprovalStatus) {
		super(work, actor, masqueradeActor, onBehalfOfActor, negotiation);
		this.oldApprovalStatus = oldApprovalStatus;
		this.newApprovalStatus = newApprovalStatus;
	}

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
		return "Counteroffer status changed";
	}
}
