package com.workmarket.domains.model.changelog.work;

import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;


@Entity
@DiscriminatorValue(WorkChangeLog.WORK_STATUS_CHANGE)
@AuditChanges
public class WorkStatusChangeChangeLog extends WorkChangeLog {

	private static final long serialVersionUID = -1544123573491155657L;
	private WorkStatusType oldStatus;
	private WorkStatusType newStatus;

	public WorkStatusChangeChangeLog() {
	}

	public WorkStatusChangeChangeLog(Long workId, Long actorId, Long masqueradeActorId, Long onBehalfOfActorId, WorkStatusType oldStatus, WorkStatusType newStatus) {
		super(workId, actorId, masqueradeActorId, onBehalfOfActorId);
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
	}

	@Transient
	public static String getDescription() {
		return "Work status changed";
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "old_work_status_type_code", referencedColumnName = "code", nullable = true)
	public WorkStatusType getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(WorkStatusType oldStatus) {
		this.oldStatus = oldStatus;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "new_work_status_type_code", referencedColumnName = "code", nullable = false)
	public WorkStatusType getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(WorkStatusType newStatus) {
		this.newStatus = newStatus;
	}
}
