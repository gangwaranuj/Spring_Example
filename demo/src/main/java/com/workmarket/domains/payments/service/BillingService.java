package com.workmarket.domains.payments.service;

import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.data.report.work.AccountStatementFilters;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateFilter;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.AccountTransactionReportRowPagination;
import com.workmarket.domains.model.account.payment.AccountingProcessTime;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.payment.PaymentCycle;
import com.workmarket.domains.model.account.payment.PaymentMethod;
import com.workmarket.domains.model.account.payment.PaymentTermsDays;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.CreditMemo;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoicePagination;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.ServiceInvoicePagination;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.StatementPagination;
import com.workmarket.domains.model.invoice.WorkMarketSummaryInvoicePagination;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.project.ProjectInvoiceBundle;
import com.workmarket.dto.AggregatesDTO;
import com.workmarket.service.business.dto.PaymentConfigurationDTO;
import com.workmarket.service.business.dto.account.PastDueCompaniesDTO;
import com.workmarket.service.business.dto.invoice.InvoiceDTO;
import com.workmarket.service.business.dto.invoice.InvoiceLineItemDTO;
import com.workmarket.service.business.dto.invoice.InvoiceSummaryDTO;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.infra.business.wrapper.FastFundInvoiceResponse;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface BillingService {

	Integer countAllDueWorkByCompany(long companyId);

	Invoice generateInvoiceForWork(Work work);

	<T extends AbstractInvoice> T findInvoiceById(long invoiceId);
	Invoice findInvoiceByWorkId(long workId);

	List<? extends AbstractInvoice> findInvoicesById(List<Long> invoiceIds);

	WorkMarketSummaryInvoicePagination findAllWorkMarketSummaryInvoices(WorkMarketSummaryInvoicePagination pagination);

	ServiceInvoicePagination findAllServiceInvoices(ServiceInvoicePagination pagination);

	AggregatesDTO getAllServiceInvoicesTotalsByStatus();

	InvoicePagination findAllInvoicesByCompany(long companyId, InvoicePagination pagination);

	AccountStatementDetailPagination getStatementDashboard(AccountStatementFilters filters, AccountStatementDetailPagination pagination);
	AccountStatementDetailPagination getStatementDashboardWithBundledInvoices(AccountStatementFilters filters, AccountStatementDetailPagination pagination);

	AccountStatementDetailPagination getStatementDashboardForUser(AccountStatementFilters filters, AccountStatementDetailPagination pagination, User user);

	AccountStatementDetailRow findAccountStatementDetailByInvoiceId(Long invoiceId);

	void emailInvoiceForWork(long workId);

	<T extends AbstractInvoice> AccountStatementDetailPagination findAllPendingPaymentInvoicesForInvoiceSummary(T invoiceSummary, AccountStatementDetailPagination pagination);

	Map<String, List<ConstraintViolation>> payInvoice(long userId, long invoiceId) throws InsufficientFundsException;

	Map<String, List<ConstraintViolation>> payInvoices(long userId, List<Long> invoiceIds) throws InsufficientFundsException;

	boolean hasAtLeastOneFastFundableInvoice(long userId);

	List<Invoice> findAllFastFundableInvoicesForWorker(long userId);

	/**
	 * Sum of resource cost for all invoices that are fast fundable for a given user.
	 * @param userId
	 * @return
	 */
	BigDecimal calculateTotalFastFundableResourceCostForWorker(long userId);

	BigDecimal getFastFundsFeePercentage(Long invoiceCompanyId);

	int getFastFundsAvailabilityThresholdHours();

	BigDecimal calculateFastFundsFeeCost(BigDecimal amountEarnedByResource, Long invoiceCompanyId);

	/**
	 * Calculate the total cost earned by a resource on a given work assignment.
	 *
	 * @param workId
	 * @return
	 */
	BigDecimal calculateTotalResourceCostOnWork(long workId);

	BigDecimal calculateTotalResourceCostOnWork(WorkResource workResource);
	/**
	 * Calculate the cost of Fast Funds fee given the amount earned by a resource on a given work assignment.
	 *
	 * @param workId
	 * @param amountEarnedByResource
	 * @return
	 */
	BigDecimal calculateFastFundsFeeCostOnWork(long workId, BigDecimal amountEarnedByResource);

	FastFundInvoiceResponse fastFundInvoice(long invoiceId);

	FastFundInvoiceResponse fastFundInvoice(long invoiceId, long workId);

	List<ConstraintViolation> payAssignment(Long workId) throws InsufficientFundsException;

	boolean validateAccessToInvoice(long invoiceId);

	boolean validateAccessToFastFundInvoice(long invoiceId);

	void emailInvoiceToUser(String toEmail, Long invoiceId);
	void emailInvoiceToUser(String toEmail, Long invoiceId, String sourceFilePath, String mimeType);

	void updateInvoiceLastDownloadedDate(List<Long> invoiceIds, Calendar date, Long loggedInUserId);

	void updateInvoiceLastDownloadedDate(Long invoiceId, Calendar date);
	void unlockInvoice(Long invoiceId);

	InvoiceSummary saveInvoiceSummary(InvoiceSummaryDTO invoiceSummaryDTO) throws IllegalArgumentException;

	<T extends InvoiceSummary> T addInvoiceToInvoiceSummary(long invoiceSummaryId, long invoiceId);

	InvoiceSummary removeInvoiceFromInvoiceSummary(long invoiceSummaryId, Long invoiceId);

	/**
	 * Calculate the rounded up nearest payment terms days period, based on the PaymentCycle, PaymentMethod and AccountingProcessTime selected for each company if statements are turned ON.
	 *
	 * @param paymentCycle
	 * @param paymentMethod
	 * @param accountingProcessTime
	 * @return PaymentTermsDays
	 */
	PaymentTermsDays calculateStatementPaymentTermsDays(PaymentCycle paymentCycle, PaymentMethod paymentMethod, AccountingProcessTime accountingProcessTime);

	PaymentConfiguration findStatementPaymentConfigurationByCompany(long companyId);

	PaymentConfiguration saveStatementPaymentConfigurationForCompany(long companyId, PaymentConfigurationDTO dto) throws IllegalArgumentException;

	void saveStatementPaymentConfiguration(PaymentConfiguration paymentConfiguration);

	/** Process all the due assignments to lock the company accounts.
	 * Sends warning email 24 hrs prior to account lockdown.
	 * @param now The current date (used for testing) */
	PastDueCompaniesDTO findAllCompaniesWithOverdueInvoices(Calendar now);

	List<Company> findAllCompaniesWithStatementsAsOfToday();

	/**
	 * Statement generation process for current period for company.
	 * @param companyId
	 * @return {@link com.workmarket.domains.model.invoice.Statement Statement}
	 */
	Statement generateStatement(long companyId);

	Statement findStatementById(long statementId);

	Statement payStatement(long statementId) throws InsufficientFundsException;

	StatementPagination findAllStatements(StatementPagination pagination);

	Company turnStatementsOff(long companyId) throws IllegalArgumentException;

	List<Integer> getAutoPayAssignmentList();

	List<Integer> getAutoPayInvoiceList();

	AccountTransactionReportRowPagination getPaymentPendingInvoicesRunningTotalsByCompany(DateFilter invoiceDueDateFilter, AccountTransactionReportRowPagination pagination);

	boolean voidWorkInvoice(Work work);

	String getServiceInvoicePdfView(long invoiceId);

	AdHocInvoice issueAdHocInvoice(InvoiceDTO invoiceDTO);

	String getNextWorkMarketInvoiceNumber(String invoiceNumberTypeCode);

	<T extends AbstractServiceInvoice> T addInvoiceLineItems(T invoice, List<InvoiceLineItemDTO> invoiceLineItemDTOs);

	public <T extends AbstractServiceInvoice> T generateServiceInvoiceSummary(T invoiceSummary);

	List<ProjectInvoiceBundle> groupInvoicesByProject(List<Long> invoiceIds, Long companyId);

	List<? extends AbstractInvoice> findInvoicesWithoutProjectBudget(List<Long> invoiceIds, Long companyId);

	BigDecimal findGeneralTotalDue(List<Long> invoiceIds, Long companyId);

	AccountStatementDetailRow findAccountStatementDetailByInvoiceId(Long invoiceId, User user);

	CreditMemo issueCreditMemo(Long invoiceId);

	boolean isCreditMemoIssuable(long invoiceId);

	void updateInvoiceLastDownloadDate(List<Long> invoiceIds);
}

