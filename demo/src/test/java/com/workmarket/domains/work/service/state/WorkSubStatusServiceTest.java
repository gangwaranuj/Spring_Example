package com.workmarket.domains.work.service.state;

import com.workmarket.dao.state.WorkSubStatusTypeCompanySettingDAO;
import com.workmarket.domains.work.dao.state.WorkSubStatusTypeRecipientAssociationDAO;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WorkSubStatusServiceTest {
	private static final Long COMPANY_ID = 1L;
	private static final Long WORK_SUB_STATUS_ID = 2L;

	@Mock WorkSubStatusTypeCompanySettingDAO workSubStatusTypeCompanySettingDAO;
	@Mock WorkSubStatusTypeRecipientAssociationDAO workSubStatusTypeRecipientAssociationDAO;
	@InjectMocks WorkSubStatusServiceImpl workSubStatusService;

	@Test
	public void findColorByIdAndCompany() {
		workSubStatusService.findColorByIdAndCompany(1L, 1L);

		verify(workSubStatusTypeCompanySettingDAO).findWorkSubStatusTypeCompanySettingByWorkSubStatusAndCompany(1L, 1L);
	}

	@Test
	public void findAllRecipientsByWorkSubStatusId() {
		workSubStatusService.findAllRecipientsByWorkSubStatusId(WORK_SUB_STATUS_ID);

		verify(workSubStatusTypeRecipientAssociationDAO).findRecipientsByWorkSubStatusId(WORK_SUB_STATUS_ID);
	}

	@Test
	public void findAllRecipientsByWorkSubStatusCodeAndCompany() {
		workSubStatusService.findAllRecipientsByWorkSubStatusCodeAndCompany(WorkSubStatusType.GENERAL_PROBLEM, COMPANY_ID);

		verify(workSubStatusTypeRecipientAssociationDAO)
			.findRecipientsByWorkSubStatusCodeAndCompanyId(WorkSubStatusType.GENERAL_PROBLEM, COMPANY_ID);
	}
}
