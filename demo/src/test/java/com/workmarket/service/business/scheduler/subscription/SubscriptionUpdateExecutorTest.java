package com.workmarket.service.business.scheduler.subscription;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionUpdateExecutorTest {

	@Mock private SubscriptionService subscriptionService;
	@Mock private AuthenticationService authenticationService;
	@InjectMocks SubscriptionUpdateExecutor subscriptionUpdateExecutor;

	private SubscriptionConfiguration subscriptionConfiguration;
	private Set<SubscriptionConfiguration> subscriptionConfigurations;

	@Before
	public void setup() {
		subscriptionConfiguration = mock(SubscriptionConfiguration.class);
		subscriptionConfigurations = Sets.newHashSet(Arrays.asList(subscriptionConfiguration));
		when(subscriptionConfiguration.getId()).thenReturn(1L);
		when(subscriptionService.findAllUpdatableSubscriptionConfigurationsByUpdateDate(any(Calendar.class))).thenReturn(subscriptionConfigurations);
	}

	@Test
	public void execute () {
		subscriptionUpdateExecutor.execute();
   		verify(subscriptionService, times(1)).updateSubscriptionConfigurationChanges(eq(1L), any(Calendar.class));
		verify(subscriptionService, times(1)).updateApprovedSubscriptionsPricingType(any(Calendar.class));
		verify(subscriptionService, times(1)).findAllSubscriptionConfigurationsWithNextThroughputReset(any(Calendar.class));
	}

}

