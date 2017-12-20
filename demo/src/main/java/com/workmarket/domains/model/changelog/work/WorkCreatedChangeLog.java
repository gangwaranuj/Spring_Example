package com.workmarket.domains.model.changelog.work;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.workmarket.domains.model.audit.AuditChanges;


@Entity
@DiscriminatorValue(WorkChangeLog.WORK_CREATED)
@AuditChanges
public class WorkCreatedChangeLog extends WorkChangeLog {

	private static final long serialVersionUID = -8494506579606254113L;

	public WorkCreatedChangeLog() {
	}

	public WorkCreatedChangeLog(Long workId, Long actorId, Long masqueradeActorId, Long onBehalfOfUserId) {
		super(workId, actorId, masqueradeActorId, onBehalfOfUserId);
	}

	@Transient
	public static String getDescription() {
		return "Company created";
	}
}
