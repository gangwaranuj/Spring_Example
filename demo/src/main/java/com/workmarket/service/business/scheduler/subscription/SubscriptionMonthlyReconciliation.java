package com.workmarket.service.business.scheduler.subscription;

import com.google.api.client.util.Sets;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfigurationPagination;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.account.PaymentConfigurationService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.scheduler.ScheduledExecutor;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Author: rocio
 */
@Component
public class SubscriptionMonthlyReconciliation implements ScheduledExecutor {

	@Autowired private PaymentConfigurationService paymentConfigurationService;
	@Autowired private SubscriptionService subscriptionService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private CompanyService companyService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterService;
	private static final Log logger = LogFactory.getLog(SubscriptionMonthlyReconciliation.class);

	@Override
	public void execute() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		try {
			verifyIncrementalTransactions();
		} catch (Exception e) {
			logger.error("Error running verifyIncrementalTransactions", e);
		}

		try {
			verifyPaymentConfiguration();
		} catch (Exception e) {
			logger.error("Error running verifyPaymentConfiguration", e);
		}

		try {
			verifyNextThroughputResetDate();
		} catch (Exception e) {
			logger.error("Error running verifyNextThroughputResetDate", e);
		}
	}

	void verifyIncrementalTransactions() {
		logger.info(String.format("****** verifyIncrementalTransactions at %s", DateUtilities.formatDate_MMDDYY_HHMMAM(Calendar.getInstance())));

	}

	void verifyPaymentConfiguration() {
		logger.info(String.format("****** verifyPaymentConfiguration at %s", DateUtilities.formatDate_MMDDYY_HHMMAM(Calendar.getInstance())));
		final Set<String> differences = Sets.newHashSet();

		SubscriptionConfigurationPagination pagination = subscriptionService.findAllActiveSubscriptionConfigurations();
		logger.info("[paymentConfiguration] Found " + pagination.getRowCount() + " active subscriptions ");

		for (SubscriptionConfiguration subscription : pagination.getResults()) {
			if (subscription != null && subscription.getEffectiveDate() != null && !subscription.getEffectiveDate().after(Calendar.getInstance())) {
				Company company = companyService.findCompanyById(subscription.getCompany().getId());
				if (!company.getPaymentConfiguration().isSubscriptionPricing()) {
					String error = "Company " + company.getId() + " has the wrong payment configuration account pricing type: " + company.getPaymentConfiguration().getAccountPricingType().getCode();
					logger.error(error);
					differences.add(error);
				}
			}
		}

		List<Company> transactionalCompanies = paymentConfigurationService.findAllCompaniesWithTransactionalAccountPricingType();
		for (Company company : transactionalCompanies) {
			SubscriptionConfiguration subscription = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(company.getId());
			if (subscription != null && subscription.getEffectiveDate() != null && !subscription.getEffectiveDate().after(Calendar.getInstance())) {
				String error = "Company " + company.getId() + " has the wrong payment configuration account pricing type: " + company.getPaymentConfiguration().getAccountPricingType().getCode();
				logger.error(error);
				differences.add(error);
			}
		}

		if (isNotEmpty(differences)) {
			userNotificationService.onPaymentConfigurationReconciliationDifference(differences);
		} else {
			logger.info("[paymentConfiguration] No Errors Found ");
		}
	}

	void verifyNextThroughputResetDate() {
		logger.info(String.format("****** verifyNextThroughputResetDate at %s", DateUtilities.formatDate_MMDDYY_HHMMAM(Calendar.getInstance())));
		final Set<String> differences = Sets.newHashSet();

		SubscriptionConfigurationPagination pagination = subscriptionService.findAllActiveSubscriptionConfigurations();
		logger.info("Found " + pagination.getRowCount() + " active subscriptions ");

		for (SubscriptionConfiguration subscription : pagination.getResults()) {
			if (subscription != null && subscription.getEffectiveDate() != null && subscription.getEndDate() != null && !subscription.getEffectiveDate().after(Calendar.getInstance())) {
				int monthsBetweenEffectiveDateAndEndDate = DateUtilities.getMonthsBetween(subscription.getEffectiveDate(), subscription.getEndDate());
				if (monthsBetweenEffectiveDateAndEndDate > 12) {
					if (subscription.getNextThroughputResetDate() == null) {
						String error = "Subscription " + subscription.getId() + " has a null throughput reset date";
						differences.add(error);
						logger.error(error);
					} else {
						Calendar subscriptionNextResetDate = (Calendar) subscription.getNextThroughputResetDate().clone();
						Calendar calculatedDate = subscriptionService.calculateNextThroughputResetDate(subscription.getId());
						if (subscriptionNextResetDate.get(Calendar.YEAR) != calculatedDate.get(Calendar.YEAR) ||
								subscriptionNextResetDate.get(Calendar.MONTH) != calculatedDate.get(Calendar.MONTH) ||
								subscriptionNextResetDate.get(Calendar.DAY_OF_MONTH) != calculatedDate.get(Calendar.DAY_OF_MONTH)) {
							String error = "Subscription " + subscription.getId() + " has a incorrect throughput reset date";
							differences.add(error);
							logger.error(error);
						}
					}
				}
			}
		}

		if (isNotEmpty(differences)) {
			userNotificationService.onNextThroughputResetDateDifference(differences);
		} else {
			logger.info("[paymentConfiguration] No Errors Found ");
		}
	}
}
