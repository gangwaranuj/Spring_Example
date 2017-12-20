package com.workmarket.domains.work.service.audit;

import com.workmarket.dao.changelog.work.WorkUpdateChangeLogDAO;
import com.workmarket.domains.work.dao.audit.WorkActionAuditDAO;
import com.workmarket.domains.work.dao.audit.WorkAuditDAO;
import com.workmarket.domains.work.dao.audit.WorkSubStatusAuditDAO;
import com.workmarket.domains.work.model.audit.WorkActionAudit;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WorkAuditServiceTest {

	@Mock WorkAuditDAO workAuditDAO;
	@Mock WorkActionAuditDAO workActionAuditDAO;
	@Mock WorkSubStatusAuditDAO workSubStatusAuditDAO;
	@Mock WorkUpdateChangeLogDAO workUpdateChangeLogDAO;
	@Mock EventRouter eventRouter;
	@InjectMocks WorkAuditServiceImpl workAuditService;

	private WorkActionRequest workActionRequest;

	@Before
	public void setUp() throws Exception {
		workActionRequest = new WorkActionRequest();
		workActionRequest.setWorkId(1L);
		workActionRequest.setModifierId(1000L);
		workActionRequest.setAuditType(WorkAuditType.ABANDON);
	}

	@Test
	public void auditAndReindexWork_success() throws Exception {
		workAuditService.auditAndReindexWork(workActionRequest);
		verify(workActionAuditDAO, times(1)).saveOrUpdate(any(WorkActionAudit.class));
		verify(eventRouter, times(1)).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test
	public void auditWork_success() throws Exception {
		workAuditService.auditWork(workActionRequest);
		verify(workActionAuditDAO, times(1)).saveOrUpdate(any(WorkActionAudit.class));
		verify(eventRouter, never()).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}
}
