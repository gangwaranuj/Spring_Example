package com.workmarket.service.business.scheduler.subscription;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.payments.service.AccountRegisterServicePrefundImpl;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionMonthlyInvoiceExecutorTest {

	@Mock SubscriptionService subscriptionService;
	@Mock AuthenticationService authenticationService;
	@Mock AccountRegisterServicePrefundImpl accountRegisterService;
	@InjectMocks SubscriptionMonthlyInvoiceExecutor subscriptionMonthlyInvoiceExecutor;

	Map<Long, List<Long>> transactions = Maps.newHashMap();

	@Before
	public void setUp() throws Exception {
		transactions.put(1L, Lists.newArrayList(2L, 4L));
		when(accountRegisterService.findAllSubscriptionTransactionPendingInvoice()).thenReturn(transactions);
	}

	@Test
	public void generateRegularSubscriptionInvoices_sendInvoice() {
		SubscriptionConfiguration subscription = mock(SubscriptionConfiguration.class);
		Set<SubscriptionConfiguration> subscriptions = ImmutableSet.of(subscription);
		when(subscriptionService.findSubscriptionsPendingInvoiceByPaymentPeriodStartDate(any(Calendar.class))).thenReturn(subscriptions);
		subscriptionMonthlyInvoiceExecutor.generateRegularSubscriptionInvoices(DateUtilities.getMidnightYesterday());
		verify(authenticationService, times(1)).setCurrentUser(anyLong());
		verify(subscriptionService, times(1)).findSubscriptionsPendingInvoiceByPaymentPeriodStartDate(any(Calendar.class));
	}

	@Test
	public void generateIncrementalSubscriptionInvoices() {
		subscriptionMonthlyInvoiceExecutor.generateIncrementalSubscriptionInvoices();
		verify(authenticationService, times(1)).setCurrentUser(anyLong());
		verify(accountRegisterService, times(1)).findAllSubscriptionTransactionPendingInvoice();
		verify(subscriptionService, times(1)).issueIncrementalSubscriptionInvoice(anyList());
	}
}