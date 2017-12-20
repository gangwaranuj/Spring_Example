package com.workmarket.domains.model.changelog.work;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Created by ianha on 12/22/14
 */
@Entity
@AuditChanges
@DiscriminatorValue(WorkChangeLog.WORK_NOTIFY)
public class WorkNotifyChangeLog extends WorkChangeLog {
	private static final long serialVersionUID = -2820561052757910594L;

	public WorkNotifyChangeLog() {
	}

	public WorkNotifyChangeLog(Long workId, Long actorId, Long masqueradeActorId, Long onBehalfOfUserId) {
		super(workId, actorId, masqueradeActorId, onBehalfOfUserId);
	}

	@Transient
	public static String getDescription() {
		return "Work Notify request sent";
	}
}
