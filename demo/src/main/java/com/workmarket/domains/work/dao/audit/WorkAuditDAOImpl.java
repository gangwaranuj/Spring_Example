package com.workmarket.domains.work.dao.audit;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.audit.WorkAudit;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class WorkAuditDAOImpl extends AbstractDAO<WorkAudit> implements WorkAuditDAO {

	@Override
	public void markAudit(Long workId, WorkAuditType type) {
		if (findByWorkIdAndWorkAuditType(workId, type) == null) {
			WorkAudit audit = new WorkAudit();
			audit.setWorkId(workId);
			audit.setCreatedOn(Calendar.getInstance());
			audit.setWorkAuditAction(type);
			saveOrUpdate(audit);
		}
	}

	private WorkAudit findByWorkIdAndWorkAuditType(Long workId, WorkAuditType type) {
		return (WorkAudit)getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("workId", workId))
				.add(Restrictions.eq("workAuditAction", type))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	protected Class<WorkAudit> getEntityClass() {
		return WorkAudit.class;
	}

}
