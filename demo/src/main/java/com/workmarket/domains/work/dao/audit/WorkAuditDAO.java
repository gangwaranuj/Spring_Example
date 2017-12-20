package com.workmarket.domains.work.dao.audit;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.audit.WorkAudit;
import com.workmarket.domains.work.model.audit.WorkAuditType;

public interface WorkAuditDAO extends DAOInterface<WorkAudit> {
	
	void markAudit(Long workId, WorkAuditType type);

}
