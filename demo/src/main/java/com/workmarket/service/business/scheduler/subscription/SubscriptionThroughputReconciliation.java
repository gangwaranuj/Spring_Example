package com.workmarket.service.business.scheduler.subscription;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfigurationPagination;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.scheduler.ScheduledExecutor;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.Calendar;


@Component
@ManagedResource(objectName = "bean:name=subscriptionThroughputExecutor", description = "SubscriptionThroughputExecutor")
public class SubscriptionThroughputReconciliation implements ScheduledExecutor {

	private static final Log logger = LogFactory.getLog(SubscriptionThroughputReconciliation.class);
	@Autowired private SubscriptionService subscriptionService;
	@Autowired private AuthenticationService authenticationService;
	@Qualifier("accountRegisterServicePrefundImpl") @Autowired private AccountRegisterService accountRegisterService;

	@Override
	@ManagedOperation(description = "SubscriptionThroughputExecutor")
	public void execute() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		SubscriptionConfigurationPagination pagination = subscriptionService.findAllActiveSubscriptionConfigurations();
		logger.debug("[throughput] Found " + pagination.getRowCount() + " active subscriptions ");

		for (SubscriptionConfiguration subscription : pagination.getResults()) {
			reconcileSubscriptionThroughput(subscription);
		}
	}

	public Calendar reconcileSubscriptionThroughput(SubscriptionConfiguration subscription) {
		if (subscription != null && subscription.getEffectiveDate() != null) {
			if (!subscription.getEffectiveDate().after(Calendar.getInstance())) {
				Calendar subscriptionEffectiveDate = (Calendar) subscription.getEffectiveDate().clone();
				Calendar lastThroughputResetDate = (Calendar) subscriptionEffectiveDate.clone();
				int monthsBetweenEffectiveDateAndToday = DateUtilities.getMonthsBetween(subscriptionEffectiveDate, Calendar.getInstance());

				if (monthsBetweenEffectiveDateAndToday >= 12) {
					lastThroughputResetDate.add(Calendar.YEAR, monthsBetweenEffectiveDateAndToday / 12);
				}

				logger.debug("[throughput] Calculating throughput since " + DateUtilities.formatDateForEmail(lastThroughputResetDate) + " for subscription " + subscription.getId());
				accountRegisterService.reconcileSubscriptionThroughput(subscription.getCompany().getId(), lastThroughputResetDate);
				return lastThroughputResetDate;
			}
		}
		return null;
	}
}
