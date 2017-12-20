package com.workmarket.domains.model.changelog.work;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.Transient;

import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@NamedQueries({
})
@DiscriminatorValue(WorkChangeLog.WORK_RESCHEDULE_AUTO_APPROVED)
@AuditChanges
public class WorkRescheduleAutoApprovedChangeLog extends WorkChangeLog{

	private static final long serialVersionUID = 1L;

	public WorkRescheduleAutoApprovedChangeLog() {}

	public WorkRescheduleAutoApprovedChangeLog(Long work, Long actor) {
        super(work, actor, null, null);
	}

	 @Transient
    public static String getDescription()
    {
        return "Work reschedule auto-approved";
    }
}
