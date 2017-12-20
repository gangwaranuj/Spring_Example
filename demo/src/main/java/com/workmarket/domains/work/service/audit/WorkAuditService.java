package com.workmarket.domains.work.service.audit;

import com.workmarket.domains.work.model.audit.WorkSubStatusAuditType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;

public interface WorkAuditService {

	void auditAndReindexWork(WorkActionRequest request);
	void auditWork(WorkActionRequest request);
	void auditWorkSubStatus(WorkSubStatusAuditType actionType, WorkSubStatusTypeAssociation association);

}
