package com.workmarket.domains.work.dao.audit;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.audit.WorkActionAudit;

@Repository
public class WorkActionAuditDAOImpl extends AbstractDAO<WorkActionAudit> implements WorkActionAuditDAO {

	@Override
	protected Class<?> getEntityClass() {
		return WorkActionAudit.class;
	}
}
