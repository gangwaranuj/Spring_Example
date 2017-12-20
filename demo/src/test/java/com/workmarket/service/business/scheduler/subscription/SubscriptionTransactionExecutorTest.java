package com.workmarket.service.business.scheduler.subscription;

import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionTransactionExecutorTest {

	@Mock SubscriptionService subscriptionService;
	@Mock AuthenticationService authenticationService;
	@InjectMocks SubscriptionTransactionExecutor subscriptionTransactionExecutor;

	@Test
	public void execute() {
		subscriptionTransactionExecutor.execute();
		verify(authenticationService, times(1)).setCurrentUser(anyLong());
	}

	@Test
	public void processThroughputIncrementTransaction() {
		subscriptionTransactionExecutor.processThroughputIncrementTransaction();
		verify(subscriptionService, times(1)).findAllSubmittedSubscriptionThroughputIncrementTxs();
	}
}