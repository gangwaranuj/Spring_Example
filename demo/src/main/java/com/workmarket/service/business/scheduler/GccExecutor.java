package com.workmarket.service.business.scheduler;

import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccountPagination;
import com.workmarket.domains.model.banking.GlobalCashCardAccount;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.payment.GCCPaymentAdapterImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.util.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;


@Service
@ManagedResource(objectName = "bean:name=gccExecutor", description = "GCC accounting tasks")
public class GccExecutor {

	private static final Log logger = LogFactory.getLog(GccExecutor.class);
	@Autowired private AuthenticationService authenticationService;
	@Autowired private BankingService bankingService;
	@Autowired private GCCPaymentAdapterImpl globalCashCardService;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl") private AccountRegisterService accountRegisterService;
	@Autowired private PricingService pricingService;

	/**
	 * Activation GCC Accounts
	 */
	@SuppressWarnings("unchecked") @ManagedOperation(description = "activateGCCAccounts")
	public void activateGCCAccounts() {
		logger.info("***** Processing GCC Accounts activation");

		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int totalProcessed = 0;
		List unconfirmed = new ArrayList<>();
		List confirmed = new ArrayList<>();

		BankAccountPagination pagination = new BankAccountPagination();
		pagination = bankingService.findAllUnConfirmedGccAccounts(pagination);


		for (int i = 0; i < pagination.getNumberOfPages(); i++) {
			for (AbstractBankAccount a : pagination.getResults()) {
				try {
					totalProcessed += 1;
					GlobalCashCardAccount account = (GlobalCashCardAccount) a;
					String keyfield = account.getAccountNumber();
					if (Boolean.TRUE.equals(globalCashCardService.isActive(keyfield))) {
						bankingService.confirmGCCAccount(account.getId());
						confirmed.add(keyfield);
					} else {
						unconfirmed.add(keyfield);
					}
				} catch (Exception ex) {
					logger.error("GCC Account exception: " + ExceptionUtils.getFullStackTrace(ex));
				}
			}
			pagination.nextPage();
			pagination = bankingService.findAllUnConfirmedGccAccounts(pagination);
		}

		stopWatch.stop();
		logger.info(
				String.format("processing time: %d , total processed: %d , total confirmed: %d , total unconfirmed: %d , confirmed in this run: %s , still unconfirmed: %s ",
						stopWatch.getTotalTimeMillis(),
						totalProcessed,
						CollectionUtils.size(confirmed),
						CollectionUtils.size(unconfirmed),
						CollectionUtils.size(confirmed) == 0 ? "none" : StringUtils.join(confirmed, ","),
						CollectionUtils.size(unconfirmed) == 0 ? "none" : StringUtils.join(unconfirmed, ",")
				)
		);
	}


	@ManagedOperation(description = "autoWithdrawalOfFunds")
	public void autoWithdrawalOfFunds() {
		logger.info("***** Process AutoWithdrawal");
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		List<Map<String, Object>> accountsWithWithdrawals = bankingService.findFieldsForAutoWithdrawalAccounts();

		if (isNotEmpty(accountsWithWithdrawals)) {
			logger.info(String.format("%d accounts will be credited based on AutoWithdrawal", accountsWithWithdrawals.size()));
			for (Map<String, Object> row : accountsWithWithdrawals) {
				Long userId = Long.valueOf(row.get("user_id").toString());
				Long companyId = Long.valueOf(row.get("company_id").toString());
				Long bankAccountId = Long.valueOf(row.get("bank_account_id").toString());
				String amount = String.valueOf(row.get("withdrawable_cash"));

				logger.info(String.format("**** AutoWithdrawal: will auto request withdrawal for: " +
								"userId: %d companyId: %d bankAccountId: %d amount: %s",
						userId, companyId, bankAccountId, amount));

				try {
					AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(companyId);
					if (accountRegister != null) {
						AccountRegisterSummaryFields accountRegisterSummaryFields = accountRegister.getAccountRegisterSummaryFields();
						if (0 > accountRegisterSummaryFields.getWithdrawableCash().compareTo(new BigDecimal(amount))) {
							logger.error("User " + userId + " doesn't have enough available cash to withdraw $" + amount);
						} else {
							accountRegisterService.withdrawFundsFromRegister(userId, bankAccountId, amount);
						}
					}
				} catch (Exception ex) {
					logger.error("**** AutoWithdrawal exception: \n\n" + ExceptionUtils.getFullStackTrace(ex));
				}
			}
		} else {
			logger.info("***** AutoWithdrawal: 0 accounts to process for auto withdrawal");
		}

	}

	@ManagedOperation(description = "deActivateGccCard")
	public void deActivateGccCard() {
		logger.info("**** Processing GCC card deactivation");
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int totalProcessed = 0;
		List<String> deactivatedCards = new ArrayList<>();

		BankAccountPagination pagination = new BankAccountPagination();
		pagination = bankingService.findAllActiveGlobalCashCardAccounts(pagination);

		for (int i = 0; i < pagination.getNumberOfPages(); i++) {
			for (AbstractBankAccount a : pagination.getResults()) {
				try {
					totalProcessed += 1;
					GlobalCashCardAccount account = (GlobalCashCardAccount) a;
					String keyfield = account.getAccountNumber();

					if (Boolean.TRUE.equals(globalCashCardService.isDeleted(keyfield))) {
						bankingService.deactivateBankAccount(account.getId(), account.getCompany().getId());
						deactivatedCards.add(keyfield);
					}

				} catch (Exception ex) {
					logger.error("GCC Disable Account exception", ex);
				}
			}
			pagination.nextPage();
			pagination = bankingService.findAllActiveGlobalCashCardAccounts(pagination);
		}

		stopWatch.stop();
		logger.info(
				String.format(
						"processing time: %d ms , total processed: %d , total deactivated: %d , deactivated in this run: %s",
						stopWatch.getTotalTimeMillis(),
						totalProcessed,
						deactivatedCards.size(),
						StringUtils.join(deactivatedCards, ",")
				)
		);

	}
}
