package com.workmarket.domains.model.changelog.work;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(WorkChangeLog.WORK_SUPPORT_CONTACT_CHANGED)
@AuditChanges
public class WorkSupportContactChangedChangeLog extends WorkChangeLog{
	private static final long serialVersionUID = 1L;

	public WorkSupportContactChangedChangeLog() {}

	public WorkSupportContactChangedChangeLog(Long work, Long actor) {
		super(work, actor, null, null);
	}

	@Transient
	public static String getDescription() {
		return "Support Contact changed";
	}
}
