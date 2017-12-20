package com.workmarket.domains.payments.service;

import com.google.common.base.Optional;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateFilter;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.AccountTransactionReportRowPagination;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.CreditCardTransaction;
import com.workmarket.domains.model.account.CreditMemoTransaction;
import com.workmarket.domains.model.account.InvoicePaymentTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionActivityPagination;
import com.workmarket.domains.model.account.ServiceTransaction;
import com.workmarket.domains.model.account.WeekReportDetail;
import com.workmarket.domains.model.account.WeeklyReportRow;
import com.workmarket.domains.model.account.WeeklyReportRowPagination;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.request.FundsRequest;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.item.CreditMemoIssuableInvoiceLineItem;
import com.workmarket.domains.model.invoice.item.InvoiceLineItem;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.screening.DrugTest;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkWorkResourceAccountRegister;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentDTO;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;
import com.workmarket.service.exception.account.CreditCardErrorException;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.account.InvalidBankAccountException;
import com.workmarket.service.exception.account.PaymentTermsAPCreditLimitException;
import com.workmarket.service.exception.account.WithdrawalExceedsDailyMaximumException;
import com.workmarket.service.exception.project.InsufficientBudgetException;
import com.workmarket.service.exception.tax.InvalidTaxEntityException;
import org.springframework.util.AutoPopulatingList;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AccountRegisterService {

	Long[] acceptWork(WorkResource workResource) throws InsufficientFundsException, PaymentTermsAPCreditLimitException;

	/**
	 * Calculate the cost of an assignment before is accepted by any resource.
	 *
	 * @param work
	 * @return WorkCostDTO
	 */
	WorkCostDTO calculateCostOnSentWork(Work work);

	/**
	 *
	 * @param work
	 * @param workResource
	 * @return WorkCostDTO
	 */
	WorkCostDTO calculateCostOnCompleteWork(Work work, WorkResource workResource);

	WorkCostDTO calculateOfflinePaymentCostOnCompleteWork(Work work, WorkResource workResource);

	boolean completeWork(WorkResource workResource);

	boolean completeOfflinePayment(WorkResource workResource);

	WorkResourceTransaction createAndExecuteResourceWorkPaymentWorkResourceTransaction(WorkResource workResource, AccountRegister resourceAccountRegister, BigDecimal amount);

	WorkResourceTransaction createAndExecuteFastFundsFeeWorkResourceTransactionTransaction(WorkResource workResource, AccountRegister resourceAccountRegister, BigDecimal amount);

	WorkResourceTransaction createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction(WorkResource workResource, AccountRegister resourceAccountRegister, BigDecimal amount);

	WorkResourceTransaction createAndExecuteFastFundsDebitWorkResourceTransactionTransaction(WorkResource workResource, AccountRegister resourceAccountRegister, BigDecimal amount);

	/**
	 * @param userId
	 * @param bankAccount
	 * @throws Exception
	 */
	void createACHVerificationTransactions(Long userId, BankAccount bankAccount);

	void updateAccountRegisterWorkFeeData(Long accountRegisterId);

	boolean payForBackgroundCheckUsingBalance(Long userId, BackgroundCheck backgroundCheck, String countryCode) throws InsufficientFundsException;

	boolean payForBackgroundCheckUsingCreditCard(Long userId, BackgroundCheck backgroundCheck, PaymentDTO paymentDTO, String countryCode) throws Exception;

	/**
	 * @param userId
	 * @param drugTest
	 * @return
	 * @throws Exception
	 * @throws InsufficientFundsException
	 */
	Long payForDrugTestUsingBalance(Long userId, DrugTest drugTest) throws InsufficientFundsException;

	/**
	 * @param userId
	 * @param drugTest
	 * @param paymentDTO
	 * @return
	 * @throws Exception
	 */
	boolean payForDrugTestUsingCreditCard(Long userId, DrugTest drugTest, PaymentDTO paymentDTO) throws Exception;

	/**
	 * @param bankAccountId
	 * @return
	 * @throws Exception
	 */
	List<BankAccountTransaction> findACHVerificationTransactions(Long bankAccountId);

	/**
	 * @param userId
	 * @param paymentDTO
	 * @return
	 * @throws Exception
	 * @throws CreditCardErrorException
	 */

	PaymentResponseDTO addFundsToRegisterFromCreditCard(AutoPopulatingList<String> projectIds, AutoPopulatingList<Float> projectAmounts, Long userId, PaymentDTO paymentDTO, boolean hasFee) throws CreditCardErrorException;

	PaymentResponseDTO addFundsToRegisterFromCreditCard(User user, PaymentDTO paymentDTO, boolean hasFee);

	/**
	 * @param fundsRequest
	 * @return
	 */
	RegisterTransaction addFundsToRegisterAsCredit(FundsRequest fundsRequest);

	/**
	 * @param fundsRequest
	 * @return
	 * @throws Exception
	 */
	Long removeFundsFromRegisterAsCash(FundsRequest fundsRequest) throws InsufficientFundsException;

	/**
	 * @param userId
	 * @param bankAccountId
	 * @param amount
	 * @return
	 * @throws Exception
	 */

	Long addFundsToRegisterFromAch(AutoPopulatingList<String> projectIds, AutoPopulatingList<Float> projectAmounts, Long userId, Long bankAccountId, String amount) throws Exception;

	Long addFundsToRegisterFromAch(Long userId, Long bankAccountId, String amount) throws Exception;

	/**
	 * @param companyId
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	RegisterTransaction addFundsToRegisterFromWire(Long companyId, String amount);


	/**
	 * @param company
	 * @param note
	 * @param amount
	 * @return
	 * @throws Exception
	 */

	RegisterTransaction addFundsToRegisterFromWire(Company company, String note, String amount) throws AccountRegisterConcurrentException;

	/**
	 * @param userId
	 * @param bankAccountId
	 * @param amount
	 * @return
	 * @throws Exception
	 * @throws InsufficientFundsException
	 * @throws com.workmarket.service.exception.account.WithdrawalExceedsDailyMaximumException
	 *
	 */
	Long withdrawFundsFromRegister(Long userId, Long bankAccountId, String amount) throws InsufficientFundsException, WithdrawalExceedsDailyMaximumException, InvalidBankAccountException, InvalidTaxEntityException;

	/**
	 * @param work
	 * @return
	 * @throws Exception
	 * @throws InsufficientFundsException
	 */
	WorkAuthorizationResponse authorizeWork(Work work) throws InsufficientFundsException, InsufficientBudgetException;

	/**
	 * Check against the buyer's available funds, project budget and user's spend limit.
	 * Won't create account register transactions.
	 *
	 * @param workTotalCost
	 * @param userBuyer
	 * @param project
	 * @return WorkAuthorizationResponse
	 * @throws InsufficientFundsException
	 * @throws InsufficientBudgetException
	 */
	WorkAuthorizationResponse verifyFundsForAuthorization(BigDecimal workTotalCost, User userBuyer, Project project) throws InsufficientFundsException, InsufficientBudgetException;

	/**
	 * After a work bundle is authorized, this method will create the account register transactions
	 * for a work on the bundle without deducting the amount from the available cash.
	 *
	 * @param work
	 * @return WorkAuthorizationResponse
	 */
	WorkAuthorizationResponse registerWorkInBundleAuthorization(Work work);
	/**
	 * @param totalOfMultipleWorkCost
	 * @param userBuyer
	 * @param project
	 * @param uniqueWorkCosts
	 * @return
	 */
	WorkAuthorizationResponse authorizeMultipleWork(BigDecimal totalOfMultipleWorkCost, User userBuyer, Project project, Set<BigDecimal> uniqueWorkCosts);

	/**
	 * @param work
	 * @param accountRegisterMoniesDTO
	 * @throws InsufficientBudgetException
	 */
	void verifySufficientProjectRemainingBudget(Work work, WorkCostDTO accountRegisterMoniesDTO) throws InsufficientBudgetException;

	/**
	 * @param project
	 * @param accountRegisterMoniesDTO
	 * @throws InsufficientBudgetException
	 */
	void verifySufficientProjectRemainingBudget(Project project, WorkCostDTO accountRegisterMoniesDTO) throws InsufficientBudgetException;

	boolean isWorkAuthorized(Work work);
	/**
	 * @param work
	 * @return
	 * @throws Exception
	 * @throws InsufficientFundsException
	 */
	void repriceWork(Work work) throws InsufficientFundsException, InsufficientBudgetException;

	/**
	 * @param work
	 * @throws AccountRegisterConcurrentException
	 *
	 * @throws Exception
	 */
	Work voidWork(Work work) throws AccountRegisterConcurrentException;

	/**
	 * @param workId
	 * @throws AccountRegisterConcurrentException
	 *
	 */
	void revertAccountRegisterPendingTransactions(Long workId) throws AccountRegisterConcurrentException;

	/**
	 * @param work
	 * @throws AccountRegisterConcurrentException
	 *
	 */
	void revertAccountRegisterPendingTransactions(Work work) throws AccountRegisterConcurrentException;

	/**
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	BigDecimal calcAvailableCash(Long userId);

	/**
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	BigDecimal calcAvailableCashByCompany(Long companyId);

	/**
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	BigDecimal calcSufficientBuyerFundsByCompany(Long companyId);

	BigDecimal calculateGeneralCashByCompany(Long companyId);

	BigDecimal calculateProjectCashByCompany(Long companyId);

	BigDecimal calcPendingCashByCompany(Long companyId);

	/**
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	BigDecimal calcEarnedInProgressByCompany(Long companyId);

	/**
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	BigDecimal calcEarnedPendingByCompany(Long companyId);

	/**
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	BigDecimal calculateWithdrawableCashByCompany(Long companyId);

	/**
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	AccountRegisterSummaryFields getAccountRegisterSummaryFields(Long companyId);

	AccountRegister getAccountRegisterById(Long accountRegisterId);

	/**
	 * @return
	 * @throws Exception
	 */
	List<BankAccountTransaction> findAccountFundingTransaction();

	Collection<RegisterTransaction> findAllRegisterTransactions(long companyId);

	/**
	 * @return
	 * @throws Exception
	 */
	List<BankAccountTransaction> findACHAccountWithdrawalTransactions();

	List<BankAccountTransaction> findGCCAccountWithdrawalTransactions();

	List<BankAccountTransaction> findPayPalAccountWithdrawalTransactions();

	/**
	 * @return
	 * @throws Exception
	 */
	List<BankAccountTransaction> findBankACHVerificationTransactions();

	/**
	 * @param company
	 * @return
	 * @throws Exception
	 */
	AccountRegister findDefaultRegisterForCompany(Company company);

	/**
	 * This will come in handy when users have a different register based on their currency
	 *
	 * @param companyId
	 * @return
	 */
	AccountRegister findDefaultRegisterForCompany(Long companyId);

	RegisterTransactionActivityPagination getLedgerForCompany(Long companyId, RegisterTransactionActivityPagination pagination);

	RegisterTransactionActivityPagination getOfflineLedgerForCompany(Long companyId, RegisterTransactionActivityPagination pagination);

	RegisterTransactionActivityPagination getPendingTransactions(Long companyId, RegisterTransactionActivityPagination pagination);

	RegisterTransactionActivityPagination getAccountRegisterTransactionReport(Long companyId, RegisterTransactionActivityPagination pagination);

	List<WeeklyReportRow> getAccountRegisterWeeklyReport(WeeklyReportRowPagination pagination);

	WeekReportDetail getCurrentWeekTrend(Long companyId);

	List<RegisterTransaction> findFundingTransactionsByDate(DateFilter datefilter);


	/**
	 * Pays an invoice bundle, an statement a.k.a Invoice Summary or a Subscription Invoice
	 * Creates a register transaction for the remaining balance of the invoice.
	 * For bundles and statements, a Payment Executor will run every X number of minutes to pick up the bundles or statements with invoice status = PAID
	 * and payment fulfillment status = PENDING FULFILLMENT to fulfill the bundled invoices and create the
	 * required register transactions to actually pay the resources.
	 *
	 * @param <T>
	 * @param invoice
	 * @return
	 * @throws InsufficientFundsException
	 */
	<T extends AbstractInvoice> boolean payInvoice(T invoice) throws InsufficientFundsException;

	boolean fulfillWorkPayment(Work work, AbstractInvoice invoiceSummary) throws InsufficientFundsException, InsufficientBudgetException;

	List<InvoicePaymentTransaction> findAllInvoicePaymentTransactionsPendingFulfillment();

	List<Long> findAllAccountRegisterIds();

	BigDecimal calculateAvailableCashByAccountRegister(Long accountRegisterId);

	BigDecimal getSumSpentAvailableCash(Long accountRegisterId);

	void processPaymentSummationsForAccountRegister(Long accountRegisterId, Date fromDate);

	AccountTransactionReportRowPagination findFundingTransactionsByTransactionDate(DateFilter transactionDateFilter, AccountTransactionReportRowPagination pagination);

	Map<Long, Long> payPaymentTerms(List<WorkWorkResourceAccountRegister> workWorkResourceAccountRegisters) throws InsufficientFundsException;

	BigDecimal getActualCashFundsByCompany(Long companyId);

	<T extends InvoiceLineItem> ServiceTransaction createInvoiceItemRegisterTransaction(Company company, T invoiceLineItem, boolean pending);

	<T extends InvoiceLineItem> CreditMemoTransaction createCreditMemoInvoiceItemRegisterTransaction(Company company, CreditMemoIssuableInvoiceLineItem invoiceLineItem, boolean originalInvoicePaid);

	void createSubscriptionIncrementalTransactions(Company company, SubscriptionPaymentPeriod paymentPeriod, SubscriptionPaymentDTO subscriptionPaymentDTO, boolean pending);

	RegisterTransaction findRegisterTransaction(long id);

	void resetAccountRegisterForAccountPricingType(Company company);

	/**
	 * Returns a list of all the service transactions pending invoice
	 * K = Account register id
	 * V = List of all register transactions
	 * @return
	 */
	Map<Long, List<Long>> findAllSubscriptionTransactionPendingInvoice();

	BigDecimal findPaymentTermsCommitmentBalance(Long accountRegisterId);

	Optional<CreditCardTransaction> findCreditCardTransaction(Long transactionId, Long companyId);

	Optional<BankAccountTransaction> findBankAccountTransaction(Long transactionId, Long companyId);

	Optional<RegisterTransaction> findWireOrCheckTransaction(Long transactionId, Long companyId);

	void transferFundsToGeneral(Long projectId, Long companyId, BigDecimal amount);

	void transferFundsToProject(Long projectId, Long companyId, BigDecimal amount);

	void transferFundsBetweenProjects(Long projectFrom, Long projectTo, Long companyId, BigDecimal amount);

	void addFundsToProjectFromAch(Long projectId, Long companyId, BigDecimal amount, Long parentTxId);

	void addFundsToGeneralFromAch(Long companyId, BigDecimal amount, Long parentTxId);

	void addFundsToProject(Long projectId, Long companyId, BigDecimal amount);

	void removeFundsFromProject(Long projectId, Long companyId, BigDecimal amount);

	void removeFundsFromGeneral(Long companyId, BigDecimal amount);

	boolean validatePayWork(Work work);

	void updateApLimit(long companyId, BigDecimal amount);

	BigDecimal findRemainingAuthorizedAmountByWorkBundle(long workBundleId);

	void reconcileSubscriptionThroughput(long companyId, Calendar fromDate);

	BigDecimal getPaymentSummation(Long companyId);

	BigDecimal getCurrentWorkFeePercentage(Long companyId);

	BigDecimal getAccountsPayableBalance(Long companyId);

	BigDecimal getAPLimit(Long companyId);

	public InvoicePaymentTransaction findInvoicePaymentTransactionByInvoice(AbstractInvoice invoice);
}
