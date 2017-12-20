package com.workmarket.domains.work.service.audit;

import com.workmarket.dao.changelog.work.WorkUpdateChangeLogDAO;
import com.workmarket.domains.work.dao.audit.WorkActionAuditDAO;
import com.workmarket.domains.work.dao.audit.WorkAuditDAO;
import com.workmarket.domains.work.dao.audit.WorkSubStatusAuditDAO;
import com.workmarket.domains.model.changelog.work.FlatWorkUpdatedChangeLog;
import com.workmarket.domains.work.model.audit.WorkActionAudit;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.model.audit.WorkSubStatusAudit;
import com.workmarket.domains.work.model.audit.WorkSubStatusAuditType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;

@Service
public class WorkAuditServiceImpl implements WorkAuditService {
	
	private static final Log logger = LogFactory.getLog(WorkAuditServiceImpl.class);
	
	@Autowired private WorkAuditDAO workAuditDAO;
	@Autowired private WorkActionAuditDAO workActionAuditDAO;
	@Autowired private WorkSubStatusAuditDAO workSubStatusAuditDAO;
	@Autowired private WorkUpdateChangeLogDAO workUpdateChangeLogDAO;
	@Autowired private EventRouter eventRouter;

	@Override
	public void auditAndReindexWork(WorkActionRequest request) {
		auditWork(request, true);
	}

	@Override
	public void auditWork(WorkActionRequest request) {
		auditWork(request, false);
	}

	private void auditWork(WorkActionRequest request, boolean reindexWork) {
		logger.info(request);
		Assert.notNull(request.getWorkId(), "Cannot audit null work");
		Assert.notNull(request.getModifierId(), "Cannot audit without a modifier");
		Assert.notNull(request.getAuditType(), "Cannot audit without a type");

		WorkActionAudit actionAudit = workActionAuditDAO.get(request.getWorkId());
		if (actionAudit == null) {
			actionAudit = new WorkActionAudit();
			actionAudit.setWorkId(request.getWorkId());
		}
		actionAudit.setLastActionOn(request.getLastActionOn());
		actionAudit.setMasqueradeId(request.getMasqueradeId());
		actionAudit.setModifierId(request.getModifierId());
		actionAudit.setOnBehalfOfId(request.getOnBehalfOfId());
		actionAudit.setLastActionOn(Calendar.getInstance());
		workActionAuditDAO.saveOrUpdate(actionAudit);

		// We only care about ENABLE_WORK_DIALER in realtime
		if (WorkAuditType.ENABLE_WORK_DIALER.equals(request.getAuditType())) {
			workAuditDAO.markAudit(request.getWorkId(), request.getAuditType());
		}

		if (!WorkAuditType.WORK_STATUS_CHANGE_AUDIT_TYPE.contains(request.getAuditType())) {
			workUpdateChangeLogDAO.saveOrUpdate(new FlatWorkUpdatedChangeLog(request.getWorkId(), request.getModifierId(), request.getMasqueradeId(), request.getOnBehalfOfId()));
		}

		if (reindexWork) {
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(request.getWorkId()));
		}
	}

	@Override
	public void auditWorkSubStatus(WorkSubStatusAuditType actionType, WorkSubStatusTypeAssociation association) {
		WorkSubStatusAudit audit = new WorkSubStatusAudit();
		audit.setActionType(actionType);
		audit.setAssociationId(association.getId());
		if (association.getTransitionNote() != null) {
			audit.setNoteId(association.getTransitionNote().getId());
		}
		workSubStatusAuditDAO.saveOrUpdate(audit);
	}

}
