package com.workmarket.service.business.scheduler.subscription;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
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
import java.util.Set;
import java.util.TimeZone;

/** Author: sgomez */
@Component
@ManagedResource(objectName="bean:name=subscriptionUpdate", description="subscription Update")
public class SubscriptionUpdateExecutor implements ScheduledExecutor {

	private static final Log logger = LogFactory.getLog(SubscriptionUpdateExecutor.class);
	@Autowired private SubscriptionService subscriptionService;
	@Autowired private AuthenticationService authenticationService;

	@Override
	@ManagedOperation(description = "subscription Update")
	public void execute() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		Calendar currentDate = DateUtilities.getCalendarNow();
		logger.info("****** Updating subscription configurations at " + DateUtilities.formatCalendar_MMDDYY(currentDate));
		Set<SubscriptionConfiguration> subscriptionConfigurations = subscriptionService.findAllUpdatableSubscriptionConfigurationsByUpdateDate(currentDate);
		for (SubscriptionConfiguration subscriptionConfiguration : subscriptionConfigurations) {
			logger.info("****** Updating subscription id " + subscriptionConfiguration.getId());
			try {
				subscriptionService.updateSubscriptionConfigurationChanges(subscriptionConfiguration.getId(), currentDate);
			} catch (Exception e) {
				logger.error("Error updating subscription id " + subscriptionConfiguration.getId(), e);
			}
		}

		//Find all the subscriptions that need to reset its throughput
		subscriptionConfigurations = subscriptionService.findAllSubscriptionConfigurationsWithNextThroughputReset(currentDate);
		for (SubscriptionConfiguration subscriptionConfiguration : subscriptionConfigurations) {
			logger.info("****** Re-seting throughput for company id " + subscriptionConfiguration.getCompany().getId());
			try {
				subscriptionService.updateYearlySubscriptionThroughput(subscriptionConfiguration.getId());
			} catch (Exception e) {
				logger.error("Error updating subscription id " + subscriptionConfiguration.getId(), e);
			}
		}

		/**
		 * Approved subscriptions need to switch to subscription pricing
		 */
		subscriptionService.updateApprovedSubscriptionsPricingType(currentDate);
	}

	public void generateMissingRenewalInvoices() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		Calendar firstDayOfNextMonth = Calendar.getInstance();
		firstDayOfNextMonth.add(Calendar.MONTH, 1);
		firstDayOfNextMonth = DateUtilities.getCalendarWithFirstDayOfTheMonth(firstDayOfNextMonth, TimeZone.getTimeZone("UTC"));

		logger.info("[subscriptions] Looking for invoices by date " + DateUtilities.formatDateForEmail(firstDayOfNextMonth));
		Set<SubscriptionConfiguration> subscriptions = subscriptionService.findSubscriptionRenewalsPendingInvoiceByPaymentPeriodStartDate(firstDayOfNextMonth);
		for (SubscriptionConfiguration subscription : subscriptions) {
			logger.info("[subscriptions] Generating invoice for subscription id " + subscription.getId());
			SubscriptionInvoice invoice = subscriptionService.issueRegularSubscriptionInvoice(subscription.getId());
			if (invoice == null) {
				logger.info("[subscriptions] No invoice generated for subscription id " + subscription.getId());
			} else {
				logger.info("[subscriptions] Created invoice id " + invoice.getId() + " for subscription id " + subscription.getId());
			}
		}

	}
}
