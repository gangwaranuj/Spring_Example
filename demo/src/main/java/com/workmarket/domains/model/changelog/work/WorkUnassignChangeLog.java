package com.workmarket.domains.model.changelog.work;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Created by alejandrosilva on 1/6/15.
 */
@Entity
@AuditChanges
@DiscriminatorValue(WorkChangeLog.WORK_UNASSIGN)
public class WorkUnassignChangeLog extends WorkPropertyChangeLog {

    private static final long serialVersionUID = -3915575853456612363L;
    private static final String PROPERTY_NAME = "assignedWorker";

    public WorkUnassignChangeLog() {}

    public WorkUnassignChangeLog(Long workId, Long actorId, Long masqueradeActorId, Long onBehalfOfActorId, String unassignedWorkerName) {
        super(workId, actorId, masqueradeActorId, onBehalfOfActorId);
        setPropertyName(PROPERTY_NAME);
        setOldValue(unassignedWorkerName);
    }

    @Transient
    public static String getDescription() {
        return "Worker unassigned";
    }
}
