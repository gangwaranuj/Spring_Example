package com.workmarket.domains.model.changelog.work;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Transient;

import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@NamedQueries({
})
@DiscriminatorValue(WorkChangeLog.WORK_RESOURCE_STATUS_CHANGE)
@AuditChanges
public class WorkResourceStatusChangeChangeLog extends WorkChangeLog {
	/**
	 *
	 */
	private static final long serialVersionUID = 1577230731310474825L;
	private WorkResourceStatusType oldStatus;
	private WorkResourceStatusType newStatus;

	public WorkResourceStatusChangeChangeLog() {
	}

	public WorkResourceStatusChangeChangeLog(Long workId, Long actorId, Long masqueradeActorId, Long onBehalfOfActorId, WorkResourceStatusType oldStatus, WorkResourceStatusType newStatus) {
		super(workId, actorId, masqueradeActorId, onBehalfOfActorId);
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "old_work_resource_status_type_code", referencedColumnName = "code", nullable = true)
	public WorkResourceStatusType getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(WorkResourceStatusType oldStatus) {
		this.oldStatus = oldStatus;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "new_work_resource_status_type_code", referencedColumnName = "code", nullable = false)
	public WorkResourceStatusType getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(WorkResourceStatusType newStatus) {
		this.newStatus = newStatus;
	}

	@Transient
	public static String getDescription() {
		return "Resource status changed";
	}
}
