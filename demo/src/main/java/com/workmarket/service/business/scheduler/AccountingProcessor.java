package com.workmarket.service.business.scheduler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.account.PastDueCompaniesDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
@ManagedResource(objectName = "bean:name=accounting", description = "accounting tasks")
public class AccountingProcessor {

	private static final Log logger = LogFactory.getLog(AccountingProcessor.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private BillingService billingService;
	@Autowired private CompanyService companyService;


	/**
	 * Process all the due invoices or close to due day (72 hours in advance)
	 * and sends notifications to the administrators and controllers.
	 */
	@ManagedOperation(description = "sendInvoiceDueReminders")
	public void sendInvoiceDueReminders() {
		logger.debug("****** Sending invoice reminders at " + new Date());
		userNotificationService.sendInvoiceDueReminders();
	}

	/**
	 * Process all the due assignments and sets the company account to a locked status.
	 * CSR can unlock the account and set hours until next suspension if payment not processed.
	 */
	@ManagedOperation(description = "processDueInvoices")
	public void processDueInvoices() {
		logger.debug("****** Processing Due Invoices at " + new Date());

		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		PastDueCompaniesDTO pastDueCompaniesDTO = billingService.findAllCompaniesWithOverdueInvoices(DateUtilities.getCalendarNow());

		/* We should remember companies we have already warned so we don't warn them again for a higher amount of time */
		/* This could happen if we have several assignments due in different days. */
		/* We should also remove all companies that were locked, they will not be warned */
		Set<Long> warnedOrLockedCompanies = Sets.newHashSet();

		for (Long companyId : pastDueCompaniesDTO.getCompaniesToLock()) {
			try {
				companyService.lockCompanyAccount(companyId);
				warnedOrLockedCompanies.add(companyId);
			} catch (Exception e) {
				logger.error("Error locking company: " + companyId, e);
			}
		}

		Map<Integer, Set<Long>> companiesToWarn = pastDueCompaniesDTO.getCompaniesToOverdueWarn();
		for (Integer warningDays : Constants.LOCKED_ACCOUNT_OVERDUE_WARNING_DAYS) {
			@SuppressWarnings("unchecked") Set<Long> companyIdsToWarn = (Set<Long>) MapUtils.getObject(companiesToWarn, warningDays, Sets.newHashSet());
			if (isNotEmpty(companyIdsToWarn)) {
				companyIdsToWarn.removeAll(warnedOrLockedCompanies);
				/* Send the warning mails */
				try {
					userNotificationService.onCompanyAccountLockedOverdueWarning(companyIdsToWarn, warningDays);
				} catch (Exception e) {
					logger.error("Error sending overdue warning: " + companyIdsToWarn, e);
				}
				warnedOrLockedCompanies.addAll(companyIdsToWarn);
			}
		}

		Map<Long, Calendar> companiesTo24HsWarn = pastDueCompaniesDTO.getCompaniesTo24HsWarn();
		Map<Long, Long> invoiceIdsToWarnOwners = pastDueCompaniesDTO.getInvoiceIdsToWarnOwners();
		companiesTo24HsWarn.keySet().removeAll(warnedOrLockedCompanies);
		invoiceIdsToWarnOwners.keySet().removeAll(warnedOrLockedCompanies);

		try {
			/* Send the 24Hs warnings */
			userNotificationService.on24HourInvoiceDueWarnings(companiesTo24HsWarn, invoiceIdsToWarnOwners);
		} catch (Exception e) {
			logger.error("Error sending 24 hours overdue warning: " + companiesTo24HsWarn, e);
		}
	}

	@ManagedOperation(description = "sendLowBalanceAlerts")
	public void sendLowBalanceAlerts() {
		logger.debug("****** Processing Low Balance Alerts at " + new Date());
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		Set<Long> companiesToAlertLowBalance = userNotificationService.getCompaniesWithLowBalanceForAlert();

		for (Long companyId : companiesToAlertLowBalance) {
			try {
				userNotificationService.onLowBalanceAlert(companyId, DateUtilities.getCalendarWithTime(12, 0));
			} catch (Exception ex) {
				logger.error("Error sending low balance alert to company: " + companyId, ex);
			}
		}
	}

	@ManagedOperation(description = "resetAllLowBalanceAlertSentToday")
	public void resetAllLowBalanceAlertSentToday() {
		logger.debug("****** Reset Sent Low Balance Alert Today Flag at " + new Date());
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		Set<Long> companiesToAlertLowBalance = userNotificationService.getCompaniesWithLowBalanceForAlert();
		for (Long companyId : companiesToAlertLowBalance) {
			try {
				companyService.resetLowBalanceAlertSentToday(companyId);
			} catch (Exception e) {
				logger.error("Error clear low balance alert flag to company: " + companyId, e);
			}
		}
	}

	/**
	 * Statement generation process for current period.
	 */
	@ManagedOperation(description = "generateStatements")
	public void generateStatements() {
		logger.debug("****** Processing Statements Generation at " + new Date());
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		List<Company> companyList = billingService.findAllCompaniesWithStatementsAsOfToday();
		logger.debug("*** Found " + companyList.size() + " Statements to generate ");

		Map<Long, Exception> exceptionMap = Maps.newHashMapWithExpectedSize(companyList.size());

		for (Company company : companyList) {
			Statement statement = null;

			do {
				try {
					statement = billingService.generateStatement(company.getId());
					if (statement != null) {
						userNotificationService.onNewStatement(statement.getId());
					}
				} catch (Exception e) {
					logger.error("Error generating statement " + company.getPaymentConfiguration() + e);
					exceptionMap.put(company.getId(), e);
				}
			}
			while (statement != null);
		}

		if (MapUtils.isNotEmpty(exceptionMap)) {
			userNotificationService.onFailedStatement(exceptionMap);
		}
	}
}
