package com.workmarket.domains.model.changelog.work;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;


@Entity
@DiscriminatorValue(WorkChangeLog.WORK_UPDATED)
@AuditChanges
public class WorkUpdatedChangeLog extends WorkChangeLog {

	private static final long serialVersionUID = 6165330661549588762L;


	@Transient
	public static String getDescription() {
		return "Work updated";
	}
}
