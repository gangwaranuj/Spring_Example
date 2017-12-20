package com.workmarket.service.business.scheduler.subscription;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfigurationPagination;
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
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionThroughputReconciliationTest {

	@Mock SubscriptionService subscriptionService;
	@Mock AuthenticationService authenticationService;
	@Mock AccountRegisterServicePrefundImpl accountRegisterService;
	@InjectMocks SubscriptionThroughputReconciliation subscriptionThroughputReconciliation;

	SubscriptionConfiguration subscriptionConfiguration;
	SubscriptionConfigurationPagination pagination;

	@Before
	public void setUp() throws Exception {
		subscriptionConfiguration = mock(SubscriptionConfiguration.class);
		pagination = mock(SubscriptionConfigurationPagination.class);
		when(pagination.getResults()).thenReturn(Lists.newArrayList(subscriptionConfiguration));
		when(subscriptionService.findAllActiveSubscriptionConfigurations()).thenReturn(pagination);
		when(subscriptionConfiguration.getCompany()).thenReturn(mock(Company.class));
	}

	@Test
	public void execute() {
		subscriptionThroughputReconciliation.execute();
		verify(subscriptionService, times(1)).findAllActiveSubscriptionConfigurations();
	}

	@Test
	public void reconcileSubscriptionThroughput_withSubscriptionCreatedInTheCurrentYear() {
		Calendar effectiveDate = DateUtilities.getCalendarWithFirstDayOfYear(Calendar.getInstance());
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		Calendar date = subscriptionThroughputReconciliation.reconcileSubscriptionThroughput(subscriptionConfiguration);
		assertNotNull(date);
		assertEquals(date.get(Calendar.MONTH), effectiveDate.get(Calendar.MONTH));
		assertEquals(date.get(Calendar.YEAR), effectiveDate.get(Calendar.YEAR));
		assertEquals(date.get(Calendar.DAY_OF_MONTH), effectiveDate.get(Calendar.DAY_OF_MONTH));
	}

	@Test
	public void reconcileSubscriptionThroughput_withSubscriptionEffectiveSinceMoreThanOneYearAgo() {
		Calendar effectiveDate = DateUtilities.getCalendarWithFirstDayOfYear(Calendar.getInstance());
		effectiveDate.add(Calendar.MONTH, -12);
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		Calendar date = subscriptionThroughputReconciliation.reconcileSubscriptionThroughput(subscriptionConfiguration);
		assertNotNull(date);
		assertEquals(date.get(Calendar.MONTH), effectiveDate.get(Calendar.MONTH));
		assertEquals(date.get(Calendar.YEAR), effectiveDate.get(Calendar.YEAR)+1);
		assertEquals(date.get(Calendar.DAY_OF_MONTH), effectiveDate.get(Calendar.DAY_OF_MONTH));
	}

	@Test
	public void reconcileSubscriptionThroughput_withSubscriptionEffectiveSinceMoreThanTwoYearsAgo() {
		Calendar effectiveDate = DateUtilities.getCalendarWithFirstDayOfYear(Calendar.getInstance());
		effectiveDate.add(Calendar.MONTH, -24);
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		Calendar date = subscriptionThroughputReconciliation.reconcileSubscriptionThroughput(subscriptionConfiguration);
		assertNotNull(date);
		assertEquals(date.get(Calendar.MONTH), effectiveDate.get(Calendar.MONTH));
		assertEquals(date.get(Calendar.YEAR), effectiveDate.get(Calendar.YEAR) + 2);
		assertEquals(date.get(Calendar.DAY_OF_MONTH), effectiveDate.get(Calendar.DAY_OF_MONTH));
	}


	@Test
	public void reconcileSubscriptionThroughput_withSubscriptionEffectiveSinceMoreThanOneYearAgoInTheSameMonth() {
		Calendar effectiveDate = DateUtilities.getCalendarWithFirstDayOfTheMonth(Calendar.getInstance(), TimeZone.getTimeZone("UTC"));
		effectiveDate.add(Calendar.MONTH, -12);
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		Calendar date = subscriptionThroughputReconciliation.reconcileSubscriptionThroughput(subscriptionConfiguration);
		assertNotNull(date);
		assertEquals(date.get(Calendar.MONTH), effectiveDate.get(Calendar.MONTH));
		assertEquals(date.get(Calendar.YEAR), effectiveDate.get(Calendar.YEAR) + 1);
		assertEquals(date.get(Calendar.DAY_OF_MONTH), effectiveDate.get(Calendar.DAY_OF_MONTH));
	}

	@Test
	public void reconcileSubscriptionThroughput_withSEffectiveDateAfterToday() {
		Calendar effectiveDate = Calendar.getInstance();
		effectiveDate.add(Calendar.MONTH, 1);
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		Calendar date = subscriptionThroughputReconciliation.reconcileSubscriptionThroughput(subscriptionConfiguration);
		assertNull(date);
		verify(accountRegisterService, never()).reconcileSubscriptionThroughput(anyLong(), any(Calendar.class));
	}

}