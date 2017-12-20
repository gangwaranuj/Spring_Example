package com.workmarket.domains.work.service;

import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.service.audit.WorkActionRequest;

public interface WorkActionRequestFactory {

	public WorkActionRequest create(AbstractWork work, Long modifierId, Long behalfOfUserId, Long masqUserId, WorkAuditType workAudit);

	public WorkActionRequest create(AbstractWork work, WorkAuditType auditType);

	public WorkActionRequest create(Long workId, WorkAuditType auditType);
}
