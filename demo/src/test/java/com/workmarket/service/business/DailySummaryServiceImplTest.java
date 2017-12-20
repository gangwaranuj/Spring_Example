package com.workmarket.service.business;

import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.account.AccountingSummaryDAO;
import com.workmarket.dao.report.internal.DailySummaryDAO;
import com.workmarket.dao.summary.work.WorkStatusTransitionDAO;
import com.workmarket.service.infra.business.AuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class DailySummaryServiceImplTest {

	@Mock DailySummaryDAO dailySummaryDAO;
	@Mock AccountingSummaryDAO accountingSummaryDAO;
	@Mock CompanyDAO companyDAO;
	@Mock AuthenticationService authenticationService;
	@Mock UserService userService;
	@Mock WorkStatusTransitionDAO workStatusTransitionDAO;
	@InjectMocks DailySummaryServiceImpl dailySummaryService;

	@Test
	public void testCreateNewSummary() throws Exception {
		dailySummaryService.createNewSummary();
		verify(workStatusTransitionDAO, times(8)).countWorkStatusTransitions(anyString(), any(Calendar.class), any(Calendar.class));
		verify(workStatusTransitionDAO, times(2)).countUniqueCompaniesWithWorkStatusTransitions(anyString(), any(Calendar.class), any(Calendar.class));
		verify(workStatusTransitionDAO, times(2)).calculatePotentialRevenueByWorkStatusType(anyString(), any(Calendar.class), any(Calendar.class));
		verify(workStatusTransitionDAO, times(1)).calculateAveragePriceByWorkStatusType(anyString(), any(Calendar.class), any(Calendar.class));
		verify(dailySummaryDAO, times(1)).calculateTermsExpired();
		verify(dailySummaryDAO, times(1)).calculateTermsOverdue();

	}
}
