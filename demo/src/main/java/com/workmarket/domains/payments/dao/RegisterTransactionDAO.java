package com.workmarket.domains.payments.dao;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.DateFilter;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.CreditCardTransaction;
import com.workmarket.domains.model.account.InvoicePaymentTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.WorkBundleTransaction;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.postalcode.Country;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RegisterTransactionDAO extends PaginatableDAOInterface<RegisterTransaction>{


	BigDecimal findPaymentTermsCommitmentBalance(Long accountRegisterId);

	BigDecimal findTotalWithdrawalsForTodayBalance(Long accountRegisterId);

	/**
	 * Gets the authorization transaction (if any) against the work's company available cash
	 * @param workId
	 * @return
	 */
	WorkResourceTransaction findWorkResourcePendingCommitmentTransaction(Long workId);

	List<BankAccountTransaction> findACHVerificationTransactions(Long bankAccountId);
	
	List<BankAccountTransaction> findBankAccountTransactions(String type, String status);

	List<BankAccountTransaction> findBankAccountTransactions(String type, String status, Country country);

	List<WorkResourceTransaction> findWorkResourceTransactionPaymentTermsCommitmentReceivePay(Long workResourceId);

	BigDecimal sumSpentAvailableCash(Long accountRegisterId);

	BigDecimal paymentsByAccountRegisterIdAndDate(Long accountRegisterId, Date fromDate);

	RegisterTransaction findPendingAuthorizationTransactionsByWorkId(Long workId);

	List<WorkResourceTransaction> findWorkResourceTransactionWorkIdPending(Long workId);

	/**
	 * Gets the authorization transaction (if any) against the work's company payment terms
	 * @param workId
	 * @return
	 */
	WorkResourceTransaction findWorkResourcePendingPaymentTermsCommitmentTransaction(Long workId);

	WorkResourceTransaction findWorkResourcePendingPaymentTermsCommitmentReceivePayTransaction(Long workId);

	List<RegisterTransaction> findFundingTransactionsByDate(DateFilter datefilter);

	BigDecimal calculateAvailableCashByAccountRegister(Long accountRegisterId);

	List<RegisterTransaction> findAllWorkResourceTransactionsPending(Long workId);

	Collection<RegisterTransaction> findAllRegisterTransactions(Long accountRegisterId);
	
	List<InvoicePaymentTransaction> findAllInvoicePaymentTransactionsByFulfillmentStatus(String paymentFulfillmentStatusTypeCode);

	List<RegisterTransaction> findChildTransactions(Long txId);

	List<RegisterTransaction> findProjectChildTransactions(Long txId);

	List<RegisterTransaction> findGeneralChildTransactions(Long txId);

	/**
	 * Returns a list of all the service transactions pending invoice
	 * K = Account register id
	 * V = List of all register transactions
	 * @return
	 */
	Map<Long, List<Long>> findAllSubscriptionTransactionPendingInvoice();

	/**
	 * Returns a list of service transactions pending invoice for the given company and subscription payment period
	 * @param companyId
	 * @param subscriptionPaymentPeriodId
	 * @return
	 */
	BigDecimal calculateIncrementalSubscriptionTransactionPendingInvoiceForPaymentPeriod(Long companyId, Long subscriptionPaymentPeriodId, String registerTransactionType);

	CreditCardTransaction findCreditCardTransaction(Long transactionId, Long companyId);

	BankAccountTransaction findBankAccountTransaction (Long transactionId, Long companyId);

	RegisterTransaction findWireOrCheckTransaction(Long transactionId, Long companyId);

	WorkBundleTransaction findWorkBundlePendingAuthorizationTransaction(long workBundleId);

	WorkBundleTransaction findWorkBundleAuthorizationTransaction(long workBundleId);

	void markBankAccountTransactionProcessing(List<Long> transactionIds);

	BigDecimal calculateSubscriptionAssignmentThroughput(long accountRegisterId, Calendar fromDate);

	BigDecimal calculateSubscriptionVORAssignmentThroughput(long accountRegisterId, Calendar fromDate);

	InvoicePaymentTransaction findInvoicePaymentTransactionByInvoice(AbstractInvoice invoiceId);
}
