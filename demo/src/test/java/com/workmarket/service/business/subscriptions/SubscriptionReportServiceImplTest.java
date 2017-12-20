package com.workmarket.service.business.subscriptions;

import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.report.subscription.SubscriptionReportDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.reporting.subscriptions.SubscriptionAggregate;
import com.workmarket.domains.model.reporting.subscriptions.SubscriptionReportPagination;
import com.workmarket.service.business.account.SubscriptionReportService;
import com.workmarket.service.business.account.SubscriptionReportServiceImpl;
import com.workmarket.service.infra.business.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionReportServiceImplTest {

	@Mock SubscriptionReportDAO subscriptionReportDAO;
	@Mock AuthenticationService authenticationService;
	@Mock private UserRoleService userRoleService;
	@InjectMocks SubscriptionReportService subscriptionReportService = new SubscriptionReportServiceImpl();

	private User user;

	@Before
	public void setUp() throws Exception {
   		user = mock(User.class);
		when(authenticationService.getCurrentUser()).thenReturn(user);
		when(userRoleService.isInternalUser(user)).thenReturn(true);
		when(subscriptionReportDAO.getSubscriptionAggregateReport(any(SubscriptionReportPagination.class))).thenReturn(new SubscriptionAggregate());
		when(subscriptionReportDAO.getStandardReport(any(SubscriptionReportPagination.class))).thenReturn(new SubscriptionReportPagination());
		when(subscriptionReportDAO.getUsageReport(any(SubscriptionReportPagination.class))).thenReturn(new SubscriptionReportPagination());
	}

	@Test
	public void getStandardReport_success() {
		SubscriptionReportPagination subscriptionReportPagination = new SubscriptionReportPagination();
		subscriptionReportService.getStandardReport(subscriptionReportPagination);
		verify(subscriptionReportDAO, times(1)).getStandardReport(any(SubscriptionReportPagination.class));
		verify(subscriptionReportDAO, times(1)).getSubscriptionAggregateReport(any(SubscriptionReportPagination.class));
	}

	@Test
	public void getUsageReport_success() {
		SubscriptionReportPagination subscriptionReportPagination = new SubscriptionReportPagination();
		subscriptionReportService.getUsageReport(subscriptionReportPagination);
		verify(subscriptionReportDAO, times(1)).getUsageReport(any(SubscriptionReportPagination.class));
	}
}
