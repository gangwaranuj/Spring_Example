package com.workmarket.service.business.scheduler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.account.PastDueCompaniesDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.infra.payment.GCCPaymentAdapterImpl;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;


/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountingProcessorTest {

	@Mock AuthenticationService authenticationService;
	@Mock UserNotificationService userNotificationService;
	@Mock BillingService billingService;
	@Mock CompanyService companyService;
	@Mock BankingService bankingService;
	@Mock GCCPaymentAdapterImpl globalCashCardService;
	@Mock NotificationService notificationService;
	@Mock EmailTemplateFactory emailTemplateFactory;
	@InjectMocks AccountingProcessor accountingProcessor;


	private PastDueCompaniesDTO pastDueCompaniesDTO;

	@Before
	public void setUp() throws Exception {
		pastDueCompaniesDTO = new PastDueCompaniesDTO();
		Set<Long> companiesToLock = Sets.newHashSet(1L, 2L);

		Map<Long, Calendar> companiesTo24HsWarn = Maps.newHashMap();
		companiesTo24HsWarn.put(3L, DateUtilities.getMidnightTomorrow());

		Map<Long, Long> invoiceIdsToWarnOwners = Maps.newHashMap();
		invoiceIdsToWarnOwners.put(1L,  1001L);

		pastDueCompaniesDTO.setCompaniesTo24HsWarn(companiesTo24HsWarn);
		pastDueCompaniesDTO.setCompaniesToLock(companiesToLock);
		pastDueCompaniesDTO.setInvoiceIdsToWarnOwners(invoiceIdsToWarnOwners);
		when(billingService.findAllCompaniesWithOverdueInvoices(any(Calendar.class))).thenReturn(pastDueCompaniesDTO);

		when(userNotificationService.getCompaniesWithLowBalanceForAlert()).thenReturn(Sets.newHashSet(1L, 2L, 3L));

		Company company = mock(Company.class);
		when(company.getId()).thenReturn(1L);
		when(billingService.findAllCompaniesWithStatementsAsOfToday()).thenReturn(Lists.newArrayList(company));

		Statement statement = mock(Statement.class);
		when(statement.getId()).thenReturn(1000L);
		when(billingService.generateStatement(anyLong())).thenReturn(statement).thenReturn(null);
	}

	@Test
	public void processDueInvoices_success() throws Exception {
		accountingProcessor.processDueInvoices();
		verify(companyService, times(2)).lockCompanyAccount(anyLong());
		verify(userNotificationService, times(1)).on24HourInvoiceDueWarnings(anyMap(), anyMap());
	}

	@Test
	public void sendInvoiceDueReminders_success() {
		accountingProcessor.sendInvoiceDueReminders();
		verify(userNotificationService, times(1)).sendInvoiceDueReminders();
	}

	@Test
	public void sendLowBalanceAlerts() {
		accountingProcessor.sendLowBalanceAlerts();
		verify(userNotificationService, times(3)).onLowBalanceAlert(anyLong(), any(Calendar.class));
	}


	@Test
	public void resetAllLowBalanceAlertSentToday_success() {
		accountingProcessor.resetAllLowBalanceAlertSentToday();
		verify(authenticationService, times(1)).setCurrentUser(eq(Constants.WORKMARKET_SYSTEM_USER_ID));
		verify(userNotificationService, times(1)).getCompaniesWithLowBalanceForAlert();
		when(userNotificationService.getCompaniesWithLowBalanceForAlert()).thenReturn(Sets.newHashSet(1L));
		verify(companyService, times(1)).resetLowBalanceAlertSentToday(eq(1L));
	}

	@Test
	public void generateStatements_success() {
		accountingProcessor.generateStatements();
		verify(authenticationService, times(1)).setCurrentUser(eq(Constants.WORKMARKET_SYSTEM_USER_ID));
		verify(billingService, times(1)).findAllCompaniesWithStatementsAsOfToday();
		verify(billingService, times(2)).generateStatement(eq(1L));
		verify(userNotificationService, times(1)).onNewStatement(eq(1000L));
	}

	@Test
	public void generateStatements_sendsEmailOnException() {
		when(billingService.generateStatement(anyLong())).thenThrow(IllegalAccessException.class);
		accountingProcessor.generateStatements();
		verify(authenticationService, times(1)).setCurrentUser(eq(Constants.WORKMARKET_SYSTEM_USER_ID));
		verify(billingService, times(1)).findAllCompaniesWithStatementsAsOfToday();
		verify(billingService, times(1)).generateStatement(eq(1L));
		verify(userNotificationService, times(1)).onFailedStatement(anyMap());
	}
}
