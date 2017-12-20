package com.workmarket.service.business.scheduler.subscription;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionThroughputIncrementTransaction;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.scheduler.ScheduledExecutor;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * Author: rocio
 */
@Component
@ManagedResource(objectName="bean:name=subscriptionTransaction", description="subscription transaction")
public class SubscriptionTransactionExecutor implements ScheduledExecutor {

	@Autowired private SubscriptionService subscriptionService;
	@Autowired private AuthenticationService authenticationService;
	private static final Log logger = LogFactory.getLog(SubscriptionTransactionExecutor.class);

	@Override
	@ManagedOperation(description = "subscription transaction")
	public void execute() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		processThroughputIncrementTransaction();

	}

	public List<SubscriptionThroughputIncrementTransaction> processThroughputIncrementTransaction() {
		logger.debug(String.format("****** generateIncrementalPaymentPeriodInvoices at %s", DateUtilities.formatDate_MMDDYY_HHMMAM(Calendar.getInstance())));

		List<SubscriptionThroughputIncrementTransaction> failedSubscriptionThroughputIncrementTransactions = Lists.newArrayList();
		List<SubscriptionThroughputIncrementTransaction> subscriptionThroughputIncrementTransactions = subscriptionService.findAllSubmittedSubscriptionThroughputIncrementTxs();

		for (SubscriptionThroughputIncrementTransaction transaction : subscriptionThroughputIncrementTransactions) {
			try {
				subscriptionService.processThroughputIncrementTransaction(transaction);
			} catch (Exception exception) {
				logger.debug(String.format("[increment transaction] - Failed to process %s", transaction.getId()));
				failedSubscriptionThroughputIncrementTransactions.add(transaction);
				logger.debug("", exception);
			}
		}
		return failedSubscriptionThroughputIncrementTransactions;
	}
}
