package com.workmarket.domains.work.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.service.infra.business.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class WorkActionRequestFactoryImpl implements WorkActionRequestFactory {

	@Autowired AuthenticationService authenticationService;

	@Override
	public WorkActionRequest create(AbstractWork work, Long modifierId, Long behalfOfUserId, Long masqUserId, WorkAuditType workAudit) {
		WorkActionRequest workRequest = new WorkActionRequest();
		workRequest.setWorkId(work.getId());
		workRequest.setAuditType(workAudit);
		workRequest.setModifierId(modifierId);
		if (behalfOfUserId != null) {
			workRequest.setOnBehalfOfId(behalfOfUserId);
		}
		if (masqUserId != null) {
			workRequest.setMasqueradeId(masqUserId);
		}
		return workRequest;
	}

	@Override
	public WorkActionRequest create(AbstractWork work, WorkAuditType auditType) {
		return create(work.getId(), auditType);
	}

	@Override
	public WorkActionRequest create(Long workId, WorkAuditType auditType) {
		WorkActionRequest workAction = new WorkActionRequest();
		workAction.setAuditType(auditType);
		User masqUser = authenticationService.getMasqueradeUser();
		if (masqUser != null) {
			workAction.setMasqueradeId(masqUser.getId());
		}
		User currentUser = authenticationService.getCurrentUser();
		workAction.setModifierId(currentUser.getId());
		workAction.setWorkId(workId);
		workAction.setLastActionOn(Calendar.getInstance());
		return workAction;
	}
}
