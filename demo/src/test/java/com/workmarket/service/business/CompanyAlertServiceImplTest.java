package com.workmarket.service.business;

import com.workmarket.dao.company.CompanyAlertDAO;
import com.workmarket.domains.model.notification.CompanyAlert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: iloveopt
 * Date: 10/4/13
 */

@RunWith(MockitoJUnitRunner.class)
public class CompanyAlertServiceImplTest {

	@InjectMocks CompanyAlertServiceImpl companyAlertService;
	@Mock CompanyService companyService;
	@Mock	CompanyAlertDAO companyAlertDao;

	private CompanyAlert companyAlert;

	@Before
	public void setup() {
		companyAlert = mock(CompanyAlert.class);
		when(companyAlert.getCompanyId()).thenReturn(1L);
		when(companyAlert.isSentToday()).thenReturn(false);
	}


	@Test
	public void setLowBalanceAlertSentToday_set() {
		companyAlertService.setLowBalanceAlertSentToday(companyAlert.getCompanyId());
		verify(companyAlertDao).setLowBalanceAlertSentToday(companyAlert.getCompanyId());
	}

	@Test
	public void resetLowBalanceAlertSentToday_reset() {
		companyAlertService.resetLowBalanceAlertSentToday(companyAlert.getCompanyId());
		verify(companyAlertDao).resetLowBalanceAlertSentToday(companyAlert.getCompanyId());
	}




}
