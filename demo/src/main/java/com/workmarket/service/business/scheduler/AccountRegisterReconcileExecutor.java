package com.workmarket.service.business.scheduler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @since 5/19/2011
 */
@Service
@ManagedResource(objectName="bean:name=accountRegisterReconciliator", description="runs recnociliation report")
public class AccountRegisterReconcileExecutor implements ScheduledExecutor {

	/*
	 * Instance variables and constants
	 */
	private static final Log logger = LogFactory.getLog("accountRegisterReconciliationLog");


	private static final Map<Long, BigDecimal> KNOWN_AVAILABLE_CASH_ISSUES = ImmutableMap.of(
		13663l, new BigDecimal("0.04"),
		39177l, new BigDecimal("-0.20"),
		4296l, new BigDecimal("0.01")
		);

	@Autowired private AuthenticationService authenticationService;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterService;
	@Autowired private UserNotificationService userNotificationService;
	@Value("${baseurl}") public String baseurl;

	@Override
	@ManagedOperation(description = "execute reconcilation report")
	public void execute() {
		logger.info("AccountRegisterReconciliation starting...");

		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		runBalance();

		logger.info("AccountRegisterReconciliation has completed....");
	}

	void runBalance() {
		logger.info("Starting runBalance...");

		// get a list of account register ids
		List<Long> accountRegisterIds = accountRegisterService.findAllAccountRegisterIds();

		Map<Long, String> differences = reconcile(accountRegisterIds);
		userNotificationService.onAccountRegisterReconciliationDifference(differences.values());

	}

	protected  Map<Long, String> reconcile(List<Long> accountRegisterIds) {
		Map<Long, String> differences = Maps.newConcurrentMap();
		for (Long accountRegisterId : accountRegisterIds) {
			try {
				Future<String> reconciliation = reconciliateAccount(accountRegisterId);
				String difference = reconciliation.get();
				if (isNotBlank(difference) && !differences.containsKey(accountRegisterId)) {
					differences.put(accountRegisterId, difference);
				}
			} catch (Exception e) {
				logger.error("Error with account register " + accountRegisterId, e);
			}
		}

		return differences;
	}

	@Async
	protected Future<String> reconciliateAccount(Long accountRegisterId) {
		return new AsyncResult<>(reconcileAccount(accountRegisterId));
	}


	// this is broken out from reconciliateAccount so that unit tests can be written (unit tests don't
	// work against @Async methods). It should not be called directly.
	protected String reconcileAccount(Long accountRegisterId) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);

		BigDecimal sumAvailableCash = accountRegisterService.calculateAvailableCashByAccountRegister(accountRegisterId);
		BigDecimal sumSpentAvailableCash = accountRegisterService.getSumSpentAvailableCash(accountRegisterId);
		BigDecimal difference = sumAvailableCash.add(sumSpentAvailableCash);

		/**
		 * Cash Balance Differences
		 */
		if (sumAvailableCash.compareTo(sumSpentAvailableCash) == -1) {
			formatter.format("URGENT: AccountRegisterId:%1$s has a negative balance of:%2$s<br>", accountRegisterId, difference);
			logger.info("accountRegisterId:" + accountRegisterId + " has negative:" + difference);
		}

		AccountRegister accountRegister = accountRegisterService.getAccountRegisterById(accountRegisterId);
		if (accountRegister.getAccountRegisterSummaryFields() == null) {
			logger.info("NULL Account Register Summary!!");
		}
		BigDecimal availableCashFromSummaries = accountRegister.getAccountRegisterSummaryFields().getAvailableCash();

		if (availableCashFromSummaries.compareTo(difference) != 0) {
			BigDecimal b = availableCashFromSummaries.subtract(difference);
			String isKnown = StringUtils.EMPTY;
			if (KNOWN_AVAILABLE_CASH_ISSUES.containsKey(accountRegisterId) && KNOWN_AVAILABLE_CASH_ISSUES.get(accountRegisterId).equals(b)) {
				isKnown = "KNOWN ISSUE: ";
			}

			formatter.format(isKnown + "Company: %1$s:%2$s, accountRegisterId:%3$s, availableCash:%4$s - registerTransaction-Balance:%5$s = %6$s %n<br>", accountRegister.getCompany().getName(),
				accountRegister.getCompany().getId(), accountRegisterId, availableCashFromSummaries, difference, b);

			logger.info("accountRegisterId: " + accountRegisterId + " availableCash: " + availableCashFromSummaries
				+ " - registerTransaction-Balance: " + difference + " equals: " + b);
		}

		/**
		 * Payment terms Differences
		 */
		BigDecimal accountsPayableBalanceFromRegister = accountRegister.getAccountRegisterSummaryFields().getAccountsPayableBalance();
		BigDecimal accountsPayableBalance = accountRegisterService.findPaymentTermsCommitmentBalance(accountRegisterId);
		BigDecimal differenceInPaymentTermsBalance = accountsPayableBalanceFromRegister.abs().subtract(accountsPayableBalance.abs());
		if (differenceInPaymentTermsBalance.compareTo(BigDecimal.ZERO) != 0) {
			logger.info("accountRegisterId: " + accountRegisterId + " accountsPayableBalanceFromRegister: " + accountsPayableBalanceFromRegister
				+ " accountsPayableBalance: " + accountsPayableBalance
				+ " differenceInPaymentTermsBalance: " + differenceInPaymentTermsBalance);

			formatter.format("accountRegisterId: %1$s, accountsPayableBalanceFromRegister:%2$s, accountsPayableBalance:%3$s, differenceInPaymentTermsBalance:%4$s %n<br>",
				accountRegisterId, accountsPayableBalanceFromRegister, accountsPayableBalance, differenceInPaymentTermsBalance);
		}

		formatter.flush();
		formatter.close();

		return sb.toString();
	}
}
