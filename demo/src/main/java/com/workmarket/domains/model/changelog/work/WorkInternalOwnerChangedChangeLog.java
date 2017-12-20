package com.workmarket.domains.model.changelog.work;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(WorkChangeLog.WORK_INTERNAL_OWNER_CHANGED)
@AuditChanges
public class WorkInternalOwnerChangedChangeLog extends WorkChangeLog{
	private static final long serialVersionUID = 1L;

	public WorkInternalOwnerChangedChangeLog() {}

	public WorkInternalOwnerChangedChangeLog(Long work, Long actor) {
		super(work, actor, null, null);
	}

	@Transient
	public static String getDescription() {
		return "Internal Owner changed";
	}
}
