package com.workmarket.service.business.scheduler.subscription;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.scheduler.ScheduledExecutor;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Author: rocio
 */
@Component
@ManagedResource(objectName = "bean:name=subscriptionMonthlyInvoice", description = "Subscription monthly invoice")
public class SubscriptionMonthlyInvoiceExecutor implements ScheduledExecutor {

	@Autowired private SubscriptionService subscriptionService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterService;

	private static final Log logger = LogFactory.getLog(SubscriptionMonthlyInvoiceExecutor.class);

	@Override
	@ManagedOperation(description = "subscription Monthly invoice")
	public void execute() {
		generateRegularSubscriptionInvoices(DateUtilities.getCalendarNow());
	}

	public void generateRegularSubscriptionInvoices(Calendar currentDate) {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		logger.info(String.format("****** generateSubscriptionInvoices at %s", DateUtilities.formatDate_MMDDYY_HHMMAM(currentDate)));

		Calendar firstDayOfNextMonth = getFirstDateOfNextMonthFromDate(currentDate);
		logger.debug("[subscriptions] Looking for invoices by date " + DateUtilities.formatDateForEmail(firstDayOfNextMonth));

		Set<SubscriptionConfiguration> subscriptionConfigurations = subscriptionService.findSubscriptionsPendingInvoiceByPaymentPeriodStartDate(firstDayOfNextMonth);
		logger.debug("[subscriptions] Found " + subscriptionConfigurations.size() + "records to generate regular subscription invoices");

		for (SubscriptionConfiguration configuration : subscriptionConfigurations) {
			generateSubscriptionRegularInvoice(configuration, firstDayOfNextMonth);
		}
	}

	private Calendar getFirstDateOfNextMonthFromDate(Calendar currentDate) {
		Calendar firstDayOfNextMonth = (Calendar) currentDate.clone();
		firstDayOfNextMonth.add(Calendar.MONTH, 1);
		return DateUtilities.getCalendarWithFirstDayOfTheMonth(firstDayOfNextMonth, TimeZone.getTimeZone("UTC"));
	}

	private void generateSubscriptionRegularInvoice(SubscriptionConfiguration configuration, Calendar firstDayOfNextMonth) {
		logger.debug("[subscriptions] Generating invoices for subscription id " + configuration.getId());

		SubscriptionInvoice invoice = subscriptionService.issueRegularInvoiceableSubscriptionInvoice(configuration.getId(), firstDayOfNextMonth);
		while (invoice != null) {
			logger.debug("[subscriptions] Created invoice id " + invoice.getId() + " for subscription id " + configuration.getId());
			invoice = subscriptionService.issueRegularInvoiceableSubscriptionInvoice(configuration.getId(), firstDayOfNextMonth);
		}
		logger.debug("[subscriptions] No invoice generated for subscription id " + configuration.getId());
	}

	public void generateIncrementalSubscriptionInvoices() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		logger.debug(String.format("****** generateIncrementalSubscriptionInvoices at %s", DateUtilities.formatDate_MMDDYY_HHMMAM(Calendar.getInstance())));
		Map<Long, List<Long>> transactions = accountRegisterService.findAllSubscriptionTransactionPendingInvoice();
		for (Map.Entry<Long, List<Long>> entry : transactions.entrySet()) {
			try {
				SubscriptionInvoice invoice = subscriptionService.issueIncrementalSubscriptionInvoice(entry.getValue());
				if (invoice == null) {
					logger.debug("No invoice generated for account register id " + entry.getKey());
				} else {
					logger.debug("Created invoice id " + invoice.getId() + " account register id " + entry.getKey());
				}
			} catch (Exception e) {
				logger.error("No invoice generated for account register id " + entry.getKey(), e);
			}
		}
	}

}