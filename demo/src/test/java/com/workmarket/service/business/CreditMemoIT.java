package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.AccountingCreditMemoSummaryDetail;
import com.workmarket.domains.model.account.AccountingPricingServiceTypeSummary;
import com.workmarket.domains.model.account.CreditMemoTransaction;
import com.workmarket.domains.model.account.RegisterTransactionActivity;
import com.workmarket.domains.model.account.RegisterTransactionActivityPagination;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionAddOnType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionType;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.CreditMemo;
import com.workmarket.domains.model.invoice.CreditMemoAudit;
import com.workmarket.domains.model.invoice.CreditMemoReasons;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.invoice.item.InvoiceLineItem;
import com.workmarket.domains.model.invoice.item.InvoiceLineItemType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.service.CreditMemoAuditService;
import com.workmarket.service.business.account.JournalEntrySummaryService;
import com.workmarket.service.business.accountregister.CreditMemoRegisterTransaction;
import com.workmarket.service.business.accountregister.CreditMemoType;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionAddOnDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentTierDTO;
import com.workmarket.service.business.dto.invoice.InvoiceDTO;
import com.workmarket.service.business.dto.invoice.InvoiceLineItemDTO;
import com.workmarket.service.business.scheduler.subscription.SubscriptionMonthlyInvoiceExecutor;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import edu.emory.mathcs.backport.java.util.Collections;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class CreditMemoIT extends BaseServiceIT {

	final String SUBSCRIPTION_CLIENT_REF_ID = "subscription_client_ref_id";

	@Autowired SubscriptionMonthlyInvoiceExecutor executor;
	@Autowired JournalEntrySummaryService journalEntrySummaryService;
	@Autowired CreditMemoAuditService creditMemoAuditService;

	private User user;
	private BigDecimal totalAddOnCost = BigDecimal.ZERO;

	private List<AccountingCreditMemoSummaryDetail> unpaidVORCreditMemoSoftwareTransactionsBefore;
	private List<AccountingCreditMemoSummaryDetail> paidVORCreditMemoSoftwareTransactionsBefore;
	private List<AccountingCreditMemoSummaryDetail> unpaidVORCreditMemoSoftwareTransactionsAfter;
	private List<AccountingCreditMemoSummaryDetail> paidVORCreditMemoSoftwareTransactionsAfter;

	private List<AccountingCreditMemoSummaryDetail> unpaidNonVORCreditMemoSoftwareTransactionsBefore;
	private List<AccountingCreditMemoSummaryDetail> paidNonVORCreditMemoSoftwareTransactionsBefore;
	private List<AccountingCreditMemoSummaryDetail> unpaidNonVORCreditMemoSoftwareTransactionsAfter;
	private List<AccountingCreditMemoSummaryDetail> paidNonVORCreditMemoSoftwareTransactionsAfter;

	private List<AccountingCreditMemoSummaryDetail> unpaidCreditMemoVORTransactionsBefore;
	private List<AccountingCreditMemoSummaryDetail> paidCreditMemoVORTransactionsBefore;
	private List<AccountingCreditMemoSummaryDetail> unpaidCreditMemoVORTransactionsAfter;
	private List<AccountingCreditMemoSummaryDetail> paidCreditMemoVORTransactionsAfter;

	private List<AccountingCreditMemoSummaryDetail> unpaidCreditMemoProfServicesTransactionsBefore;
	private List<AccountingCreditMemoSummaryDetail> paidCreditMemoProfServicesTransactionsBefore;
	private List<AccountingCreditMemoSummaryDetail> unpaidCreditMemoProfServicesTransactionsAfter;
	private List<AccountingCreditMemoSummaryDetail> paidCreditMemoProfServicesTransactionsAfter;

	private List<AccountingCreditMemoSummaryDetail> unpaidCreditMemoSubscriptionTransactionsBefore;
	private List<AccountingCreditMemoSummaryDetail> paidCreditMemoSubscriptionTransactionsBefore;
	private List<AccountingCreditMemoSummaryDetail> unpaidCreditMemoSubscriptionTransactionsAfter;
	private List<AccountingCreditMemoSummaryDetail> paidCreditMemoSubscriptionTransactionsAfter;


	@Before
	public void initUser() throws Exception {
		user = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(user);
	}

	/**
	 * Create invoice, pay it and then create credit invoice against it. The amount invoiced should be credited
	 * to the companies account
	 */
	@Test
	public void testIssueCreditMemo_paidInvoice_VOR() throws Exception {

		// Get JES report totals before invoice/creditMemo
		Calendar start = DateUtilities.getCalendarNow();
		start.add(Calendar.DAY_OF_MONTH, -1);
		Calendar end = DateUtilities.getCalendarNow();
		end.add(Calendar.DAY_OF_MONTH, 1);
		AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummaryPre = new AccountingPricingServiceTypeSummary();
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryRevenueValues(
			accountingPricingServiceTypeSummaryPre, start, end, start);

		countTransactionsBefore();

		BigDecimal availableCashBeforePayingInvoice = accountRegisterService.calcSufficientBuyerFundsByCompany(
			user.getCompany().getId());

		SubscriptionInvoice invoice = getSubscriptionInvoice(true, SubscriptionPeriod.QUARTERLY);
		BigDecimal amount = invoice.getBalance();

		// Pay invoice
		billingService.payInvoice(user.getId(), invoice.getId());

		invoice = billingService.findInvoiceById(invoice.getId());

		BigDecimal availableCashAfter = accountRegisterService.calcSufficientBuyerFundsByCompany(
			user.getCompany().getId());

		assertEquals(availableCashBeforePayingInvoice.subtract(amount), availableCashAfter);

		// Issue credit memo for paid invoice
		CreditMemo creditMemo = billingService.issueCreditMemo(invoice.getId());
		CreditMemoAudit creditMemoAudit = creditMemo.getCreditMemoAudit();
		creditMemoAudit.setReasonId(CreditMemoReasons.REFUND.getValue());
		creditMemoAudit.setNote("CreditMemoIT - testIssueCreditMemo_paidInvoice_VOR");
		creditMemoAuditService.saveOrUpdate(creditMemoAudit);
		
		// Credit memo and invoice should now have 'paid' status
		assertEquals(InvoiceStatusType.PAID, invoice.getInvoiceStatusType().getCode());
		assertEquals(InvoiceStatusType.PAID, creditMemo.getInvoiceStatusType().getCode());

		// Credit memo balance should equal invoice balance
		assertTrue(invoice.getBalance().compareTo(creditMemo.getBalance()) == 0);
		assertTrue(amount.compareTo(invoice.getBalance()) == 0);

		// Credit memo and invoice should have no remaining balance
		assertTrue(BigDecimal.ZERO.compareTo(invoice.getRemainingBalance()) == 0);
		assertTrue(BigDecimal.ZERO.compareTo(creditMemo.getRemainingBalance()) == 0);

		// Verify that credit amount was applied to company balance
		BigDecimal availableCashAfterCredit = accountRegisterService.calcSufficientBuyerFundsByCompany(
			user.getCompany().getId());
		assertTrue(availableCashBeforePayingInvoice.compareTo(availableCashAfterCredit) == 0);

		assertNotNull(creditMemo.getCreditMemoAudit());

		assertEquals(invoice, creditMemo.getCreditMemoAudit().getServiceInvoice());

		assertEquals(creditMemo.getInvoiceLineItems().size(), invoice.getInvoiceLineItems().size());
		for (InvoiceLineItem invoiceLineItem : creditMemo.getInvoiceLineItems()) {
			assertNotNull(invoiceLineItem.getRegisterTransaction());

			// Register Transactions are not pending
			assertFalse(invoiceLineItem.getRegisterTransaction().isPending());

			assertTrue(((CreditMemoTransaction) invoiceLineItem.getRegisterTransaction()).isOriginalInvoicePaid());

			// Register Transactions have 'creditMemoItem' register_transaction_type
			assertTrue(invoiceLineItem.getRegisterTransaction().getRegisterTransactionType().getCode().equals(
				RegisterTransactionType.CREDIT_MEMO_ITEM));
		}

		// Verify JES report accounting is correct
		AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummaryPost = new AccountingPricingServiceTypeSummary();
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryRevenueValues(accountingPricingServiceTypeSummaryPost, start, end, start);

		BigDecimal diffVorSoftwareFee = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionVorSoftwareFee().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionVorSoftwareFee());
		assertTrue(diffVorSoftwareFee.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffVorSoftwareFeeHistorical = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionVorSoftwareFeeHistorical().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionVorSoftwareFeeHistorical());
		assertTrue(diffVorSoftwareFeeHistorical.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffVorVorFee = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionVorVorFee().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionVorVorFee());
		assertTrue(diffVorVorFee.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffVorVorFeeHistorical = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionVorVorFeeHistorical().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionVorVorFeeHistorical());
		assertTrue(diffVorVorFeeHistorical.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffProfessionalServiceFee = accountingPricingServiceTypeSummaryPost.getCreditProfessionalServiceFee().subtract(accountingPricingServiceTypeSummaryPre.getCreditProfessionalServiceFee());
		assertTrue(diffProfessionalServiceFee.compareTo(totalAddOnCost.negate()) == 0);

		BigDecimal diffProfessionalServiceFeeHistorical = accountingPricingServiceTypeSummaryPost.getCreditProfessionalServiceFeeHistorical().subtract(accountingPricingServiceTypeSummaryPre.getCreditProfessionalServiceFeeHistorical());
		assertTrue(diffProfessionalServiceFeeHistorical.compareTo(totalAddOnCost.negate()) == 0);

		countTransactionsAfter();

		// VOR Software Fees
		assertTrue(unpaidVORCreditMemoSoftwareTransactionsAfter.size() == unpaidVORCreditMemoSoftwareTransactionsBefore.size());
		assertTrue(paidVORCreditMemoSoftwareTransactionsAfter.size() > paidVORCreditMemoSoftwareTransactionsBefore.size());

		// Non VOR Software Fees
		assertTrue(unpaidNonVORCreditMemoSoftwareTransactionsAfter.size() == unpaidNonVORCreditMemoSoftwareTransactionsBefore.size());
		assertTrue(paidNonVORCreditMemoSoftwareTransactionsAfter.size() == paidNonVORCreditMemoSoftwareTransactionsBefore.size());

		// VOR Fees
		assertTrue(unpaidCreditMemoVORTransactionsAfter.size() == unpaidCreditMemoVORTransactionsBefore.size());
		assertTrue(paidCreditMemoVORTransactionsAfter.size() > paidCreditMemoVORTransactionsBefore.size());

		// Subscription Fees
		assertTrue(unpaidCreditMemoSubscriptionTransactionsAfter.size() == unpaidCreditMemoSubscriptionTransactionsBefore.size());
		assertTrue(paidCreditMemoSubscriptionTransactionsAfter.size() > paidCreditMemoSubscriptionTransactionsBefore.size());

		// Professional Services Fees
		assertTrue(unpaidCreditMemoProfServicesTransactionsAfter.size() == unpaidCreditMemoProfServicesTransactionsBefore.size());
		assertTrue(paidCreditMemoProfServicesTransactionsAfter.size() > paidCreditMemoProfServicesTransactionsBefore.size());

		// Verify Credit Memo appears in Ledger
		assertTrue(invoiceInLedger(creditMemo.getInvoiceNumber()));
	}

	/**
	 * Create invoice, pay it and then create credit invoice against it. The amount invoiced should be credited
	 * to the companies account
	 */
	@Test
	public void testIssueCreditMemo_paidInvoice_nonVOR() {

		// Get JES report totals before invoice/creditMemo
		Calendar start = DateUtilities.getCalendarNow();
		start.add(Calendar.DAY_OF_MONTH, -1);
		Calendar end = DateUtilities.getCalendarNow();
		end.add(Calendar.DAY_OF_MONTH, 1);
		AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummaryPre = new AccountingPricingServiceTypeSummary();
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryRevenueValues(accountingPricingServiceTypeSummaryPre, start, end, start);

		countTransactionsBefore();

		BigDecimal availableCashBeforePayingInvoice = accountRegisterService.calcSufficientBuyerFundsByCompany(
			user.getCompany().getId());

		SubscriptionInvoice invoice = getSubscriptionInvoice(false, SubscriptionPeriod.QUARTERLY);
		BigDecimal amount = invoice.getBalance();

		// Pay invoice
		billingService.payInvoice(user.getId(), invoice.getId());

		invoice = billingService.findInvoiceById(invoice.getId());

		BigDecimal availableCashAfter = accountRegisterService.calcSufficientBuyerFundsByCompany(
			user.getCompany().getId());

		assertEquals(availableCashBeforePayingInvoice.subtract(amount), availableCashAfter);

		// Issue credit memo for paid invoice
		CreditMemo creditMemo = billingService.issueCreditMemo(invoice.getId());
		CreditMemoAudit creditMemoAudit = creditMemo.getCreditMemoAudit();
		creditMemoAudit.setReasonId(CreditMemoReasons.OTHER.getValue());
		creditMemoAudit.setNote("CreditMemoIT - testIssueCreditMemo_paidInvoice_nonVOR");
		creditMemoAuditService.saveOrUpdate(creditMemoAudit);

		// Credit memo and invoice should now have 'paid' status
		assertEquals(InvoiceStatusType.PAID, invoice.getInvoiceStatusType().getCode());
		assertEquals(InvoiceStatusType.PAID, creditMemo.getInvoiceStatusType().getCode());

		// Credit memo balance should equal invoice balance
		assertTrue(invoice.getBalance().compareTo(creditMemo.getBalance()) == 0);
		assertTrue(amount.compareTo(invoice.getBalance()) == 0);

		// Credit memo and invoice should have no remaining balance
		assertTrue(BigDecimal.ZERO.compareTo(invoice.getRemainingBalance()) == 0);
		assertTrue(BigDecimal.ZERO.compareTo(creditMemo.getRemainingBalance()) == 0);

		// Verify that credit amount was applied to company balance
		BigDecimal availableCashAfterCredit = accountRegisterService.calcSufficientBuyerFundsByCompany(
			user.getCompany().getId());
		assertTrue(availableCashBeforePayingInvoice.compareTo(availableCashAfterCredit) == 0);

		assertNotNull(creditMemo.getCreditMemoAudit());

		assertEquals(invoice, creditMemo.getCreditMemoAudit().getServiceInvoice());

		assertEquals(creditMemo.getInvoiceLineItems().size(), invoice.getInvoiceLineItems().size());
		for (InvoiceLineItem invoiceLineItem : creditMemo.getInvoiceLineItems()) {
			assertNotNull(invoiceLineItem.getRegisterTransaction());

			// Register Transactions are not pending
			assertFalse(invoiceLineItem.getRegisterTransaction().isPending());

			assertTrue(((CreditMemoTransaction) invoiceLineItem.getRegisterTransaction()).isOriginalInvoicePaid());

			// Register Transactions have 'creditMemoItem' register_transaction_type
			assertTrue(invoiceLineItem.getRegisterTransaction().getRegisterTransactionType().getCode().equals(
				RegisterTransactionType.CREDIT_MEMO_ITEM));
		}

		// Verify JES report accounting is correct
		AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummaryPost = new AccountingPricingServiceTypeSummary();
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryRevenueValues(accountingPricingServiceTypeSummaryPost, start, end, start);

		BigDecimal diffNonVorSoftwareFee = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionNonVorSoftwareFee().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionNonVorSoftwareFee());
		assertTrue(diffNonVorSoftwareFee.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffNonVorSoftwareFeeHistorical = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionNonVorSoftwareFeeHistorical().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionNonVorSoftwareFeeHistorical());
		assertTrue(diffNonVorSoftwareFeeHistorical.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffProfessionalServiceFee = accountingPricingServiceTypeSummaryPost.getCreditProfessionalServiceFee().subtract(accountingPricingServiceTypeSummaryPre.getCreditProfessionalServiceFee());
		assertTrue(diffProfessionalServiceFee.compareTo(totalAddOnCost.negate()) == 0);

		BigDecimal diffProfessionalServiceFeeHistorical = accountingPricingServiceTypeSummaryPost.getCreditProfessionalServiceFeeHistorical().subtract(accountingPricingServiceTypeSummaryPre.getCreditProfessionalServiceFeeHistorical());
		assertTrue(diffProfessionalServiceFeeHistorical.compareTo(totalAddOnCost.negate()) == 0);

		countTransactionsAfter();

		// VOR Software Fees
		assertTrue(unpaidVORCreditMemoSoftwareTransactionsAfter.size() == unpaidVORCreditMemoSoftwareTransactionsBefore.size());
		assertTrue(paidVORCreditMemoSoftwareTransactionsAfter.size() == paidVORCreditMemoSoftwareTransactionsBefore.size());

		// Non VOR Software Fees
		assertTrue(unpaidNonVORCreditMemoSoftwareTransactionsAfter.size() == unpaidNonVORCreditMemoSoftwareTransactionsBefore.size());
		assertTrue(paidNonVORCreditMemoSoftwareTransactionsAfter.size() > paidNonVORCreditMemoSoftwareTransactionsBefore.size());

		// VOR Fees
		assertTrue(unpaidCreditMemoVORTransactionsAfter.size() == unpaidCreditMemoVORTransactionsBefore.size());
		assertTrue(paidCreditMemoVORTransactionsAfter.size() == paidCreditMemoVORTransactionsBefore.size());

		// Subscription Fees
		assertTrue(unpaidCreditMemoSubscriptionTransactionsAfter.size() == unpaidCreditMemoSubscriptionTransactionsBefore.size());
		assertTrue(paidCreditMemoSubscriptionTransactionsAfter.size() > paidCreditMemoSubscriptionTransactionsBefore.size());

		// Professional Services Fees
		assertTrue(unpaidCreditMemoProfServicesTransactionsAfter.size() == unpaidCreditMemoProfServicesTransactionsBefore.size());
		assertTrue(paidCreditMemoProfServicesTransactionsAfter.size() > paidCreditMemoProfServicesTransactionsBefore.size());

		// Verify Credit Memo appears in Ledger
		assertTrue(invoiceInLedger(creditMemo.getInvoiceNumber()));
	}

	/**
	 * Create an invoice and then create a credit memo against it. This should update invoice status to 'paid'
	 * and update payables amount to reflect that the invoice is paid
	 */
	@Test
	public void testIssueCreditMemo_unpaidInvoice_VOR() {

		// Get JES report totals before invoice/creditMemo
		Calendar start = DateUtilities.getCalendarNow();
		start.add(Calendar.DAY_OF_MONTH, -1);
		Calendar end = DateUtilities.getCalendarNow();
		end.add(Calendar.DAY_OF_MONTH, 1);
		AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummaryPre = new AccountingPricingServiceTypeSummary();
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryRevenueValues(accountingPricingServiceTypeSummaryPre, start, end, start);

		countTransactionsBefore();

		BigDecimal payablesBalanceBeforeInvoice = accountRegisterService.getAccountsPayableBalance(
			user.getCompany().getId());

		SubscriptionInvoice invoice = getSubscriptionInvoice(true, SubscriptionPeriod.QUARTERLY);
		BigDecimal amount = invoice.getBalance();

		AccountRegisterSummaryFields summaryFieldsBefore = accountRegisterService.getAccountRegisterSummaryFields(
			user.getCompany().getId());

		// Issue credit memo for paid invoice
		CreditMemo creditMemo = billingService.issueCreditMemo(invoice.getId());
		CreditMemoAudit creditMemoAudit = creditMemo.getCreditMemoAudit();
		creditMemoAudit.setReasonId(CreditMemoReasons.REFUND.getValue());
		creditMemoAudit.setNote("CreditMemoIT - testIssueCreditMemo_unpaidInvoice_VOR");
		creditMemoAuditService.saveOrUpdate(creditMemoAudit);

		AccountRegisterSummaryFields summaryFieldsAfter = accountRegisterService.getAccountRegisterSummaryFields(
			user.getCompany().getId());

		assertTrue(summaryFieldsBefore.equals(summaryFieldsAfter));

		invoice = billingService.findInvoiceById(invoice.getId());

		// Credit memo and invoice should now have 'paid' status
		assertEquals(InvoiceStatusType.PAID, invoice.getInvoiceStatusType().getCode());
		assertEquals(InvoiceStatusType.PAID, creditMemo.getInvoiceStatusType().getCode());

		// Credit memo balance should equal invoice balance
		assertTrue(invoice.getBalance().compareTo(creditMemo.getBalance()) == 0);
		assertTrue(amount.compareTo(invoice.getBalance()) == 0);

		// Credit memo and invoice should have no remaining balance
		assertTrue(BigDecimal.ZERO.compareTo(invoice.getRemainingBalance()) == 0);
		assertTrue(BigDecimal.ZERO.compareTo(creditMemo.getRemainingBalance()) == 0);

		// Verify that payables was updated now that invoice was 'paid'
		BigDecimal payablesBalanceAfterCredit = accountRegisterService.getAccountsPayableBalance(
			user.getCompany().getId());
		assertTrue(payablesBalanceBeforeInvoice.compareTo(payablesBalanceAfterCredit) == 0);

		assertNotNull(creditMemo.getCreditMemoAudit());

		assertEquals(invoice, creditMemo.getCreditMemoAudit().getServiceInvoice());

		assertEquals(creditMemo.getInvoiceLineItems().size(), invoice.getInvoiceLineItems().size());
		for (InvoiceLineItem invoiceLineItem : creditMemo.getInvoiceLineItems()) {
			assertNotNull(invoiceLineItem.getRegisterTransaction());

			// Register Transactions are not pending
			assertFalse(invoiceLineItem.getRegisterTransaction().isPending());

			assertFalse(((CreditMemoTransaction) invoiceLineItem.getRegisterTransaction()).isOriginalInvoicePaid());

			// Register Transactions have 'creditMemoItem' register_transaction_type
			assertTrue(invoiceLineItem.getRegisterTransaction().getRegisterTransactionType().getCode().equals(
				RegisterTransactionType.CREDIT_MEMO_ITEM));
		}

		// Verify JES report accounting is correct
		AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummaryPost = new AccountingPricingServiceTypeSummary();
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryRevenueValues(accountingPricingServiceTypeSummaryPost, start, end, start);

		BigDecimal diffVorSoftwareFee = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionVorSoftwareFee().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionVorSoftwareFee());
		assertTrue(diffVorSoftwareFee.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffVorSoftwareFeeHistorical = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionVorSoftwareFeeHistorical().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionVorSoftwareFeeHistorical());
		assertTrue(diffVorSoftwareFeeHistorical.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffVorVorFee = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionVorVorFee().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionVorVorFee());
		assertTrue(diffVorVorFee.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffVorVorFeeHistorical = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionVorVorFeeHistorical().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionVorVorFeeHistorical());
		assertTrue(diffVorVorFeeHistorical.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffProfessionalServiceFee = accountingPricingServiceTypeSummaryPost.getCreditProfessionalServiceFee().subtract(accountingPricingServiceTypeSummaryPre.getCreditProfessionalServiceFee());
		assertTrue(diffProfessionalServiceFee.compareTo(totalAddOnCost.negate()) == 0);

		BigDecimal diffProfessionalServiceFeeHistorical = accountingPricingServiceTypeSummaryPost.getCreditProfessionalServiceFeeHistorical().subtract(accountingPricingServiceTypeSummaryPre.getCreditProfessionalServiceFeeHistorical());
		assertTrue(diffProfessionalServiceFeeHistorical.compareTo(totalAddOnCost.negate()) == 0);

		countTransactionsAfter();

		// VOR Software Fees
		assertTrue(unpaidVORCreditMemoSoftwareTransactionsAfter.size() > unpaidVORCreditMemoSoftwareTransactionsBefore.size());
		assertTrue(paidVORCreditMemoSoftwareTransactionsAfter.size() == paidVORCreditMemoSoftwareTransactionsBefore.size());

		// Non VOR Software Fees
		assertTrue(unpaidNonVORCreditMemoSoftwareTransactionsAfter.size() == unpaidNonVORCreditMemoSoftwareTransactionsBefore.size());
		assertTrue(paidNonVORCreditMemoSoftwareTransactionsAfter.size() == paidNonVORCreditMemoSoftwareTransactionsBefore.size());

		// VOR Fees
		assertTrue(unpaidCreditMemoVORTransactionsAfter.size() > unpaidCreditMemoVORTransactionsBefore.size());
		assertTrue(paidCreditMemoVORTransactionsAfter.size() == paidCreditMemoVORTransactionsBefore.size());

		// Subscription Fees
		assertTrue(unpaidCreditMemoSubscriptionTransactionsAfter.size() > unpaidCreditMemoSubscriptionTransactionsBefore.size());
		assertTrue(paidCreditMemoSubscriptionTransactionsAfter.size() == paidCreditMemoSubscriptionTransactionsBefore.size());

		// Professional Services Fees
		assertTrue(unpaidCreditMemoProfServicesTransactionsAfter.size() > unpaidCreditMemoProfServicesTransactionsBefore.size());
		assertTrue(paidCreditMemoProfServicesTransactionsAfter.size() == paidCreditMemoProfServicesTransactionsBefore.size());

		// Verify Credit Memo does not appear in Ledger
		assertFalse(invoiceInLedger(creditMemo.getInvoiceNumber()));
	}

	/**
	 * Create an invoice and then create a credit memo against it. This should update invoice status to 'paid'
	 * and update payables amount to reflect that the invoice is paid
	 */
	@Test
	public void testIssueCreditMemo_unpaidInvoice_nonVOR() {

		// Get JES report totals before invoice/creditMemo
		Calendar start = DateUtilities.getCalendarNow();
		start.add(Calendar.DAY_OF_MONTH, -1);
		Calendar end = DateUtilities.getCalendarNow();
		end.add(Calendar.DAY_OF_MONTH, 1);
		AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummaryPre = new AccountingPricingServiceTypeSummary();
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryRevenueValues(accountingPricingServiceTypeSummaryPre, start, end, start);

		countTransactionsBefore();

		BigDecimal payablesBalanceBeforeInvoice = accountRegisterService.getAccountsPayableBalance(
			user.getCompany().getId());

		SubscriptionInvoice invoice = getSubscriptionInvoice(false, SubscriptionPeriod.QUARTERLY);
		BigDecimal amount = invoice.getBalance();

		AccountRegisterSummaryFields summaryFieldsBefore = accountRegisterService.getAccountRegisterSummaryFields(
			user.getCompany().getId());

		// Issue credit memo for paid invoice
		CreditMemo creditMemo = billingService.issueCreditMemo(invoice.getId());
		CreditMemoAudit creditMemoAudit = creditMemo.getCreditMemoAudit();
		creditMemoAudit.setReasonId(CreditMemoReasons.REFUND.getValue());
		creditMemoAudit.setNote("CreditMemoIT - testIssueCreditMemo_unpaidInvoice_nonVOR");
		creditMemoAuditService.saveOrUpdate(creditMemoAudit);

		AccountRegisterSummaryFields summaryFieldsAfter = accountRegisterService.getAccountRegisterSummaryFields(
			user.getCompany().getId());

		assertTrue(summaryFieldsBefore.equals(summaryFieldsAfter));

		invoice = billingService.findInvoiceById(invoice.getId());

		// Credit memo and invoice should now have 'paid' status
		assertEquals(InvoiceStatusType.PAID, invoice.getInvoiceStatusType().getCode());
		assertEquals(InvoiceStatusType.PAID, creditMemo.getInvoiceStatusType().getCode());

		// Credit memo balance should equal invoice balance
		assertTrue(invoice.getBalance().compareTo(creditMemo.getBalance()) == 0);
		assertTrue(amount.compareTo(invoice.getBalance()) == 0);

		// Credit memo and invoice should have no remaining balance
		assertTrue(BigDecimal.ZERO.compareTo(invoice.getRemainingBalance()) == 0);
		assertTrue(BigDecimal.ZERO.compareTo(creditMemo.getRemainingBalance()) == 0);

		// Verify that payables was updated now that invoice was 'paid'
		BigDecimal payablesBalanceAfterCredit = accountRegisterService.getAccountsPayableBalance(
			user.getCompany().getId());
		assertTrue(payablesBalanceBeforeInvoice.compareTo(payablesBalanceAfterCredit) == 0);

		assertNotNull(creditMemo.getCreditMemoAudit());

		assertEquals(invoice, creditMemo.getCreditMemoAudit().getServiceInvoice());

		assertEquals(creditMemo.getInvoiceLineItems().size(), invoice.getInvoiceLineItems().size());
		for (InvoiceLineItem invoiceLineItem : creditMemo.getInvoiceLineItems()) {
			assertNotNull(invoiceLineItem.getRegisterTransaction());

			// Register Transactions are not pending
			assertFalse(invoiceLineItem.getRegisterTransaction().isPending());

			assertFalse(((CreditMemoTransaction) invoiceLineItem.getRegisterTransaction()).isOriginalInvoicePaid());

			// Register Transactions have 'creditMemoItem' register_transaction_type
			assertTrue(invoiceLineItem.getRegisterTransaction().getRegisterTransactionType().getCode().equals(
				RegisterTransactionType.CREDIT_MEMO_ITEM));
		}

		// Verify JES report accounting is correct
		AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummaryPost = new AccountingPricingServiceTypeSummary();
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryRevenueValues(accountingPricingServiceTypeSummaryPost, start, end, start);

		BigDecimal diffNonVorSoftwareFee = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionNonVorSoftwareFee().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionNonVorSoftwareFee());
		assertTrue(diffNonVorSoftwareFee.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffNonVorSoftwareFeeHistorical = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionNonVorSoftwareFeeHistorical().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionNonVorSoftwareFeeHistorical());
		assertTrue(diffNonVorSoftwareFeeHistorical.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffProfessionalServiceFee = accountingPricingServiceTypeSummaryPost.getCreditProfessionalServiceFee().subtract(accountingPricingServiceTypeSummaryPre.getCreditProfessionalServiceFee());
		assertTrue(diffProfessionalServiceFee.compareTo(totalAddOnCost.negate()) == 0);

		BigDecimal diffProfessionalServiceFeeHistorical = accountingPricingServiceTypeSummaryPost.getCreditProfessionalServiceFeeHistorical().subtract(accountingPricingServiceTypeSummaryPre.getCreditProfessionalServiceFeeHistorical());
		assertTrue(diffProfessionalServiceFeeHistorical.compareTo(totalAddOnCost.negate()) == 0);

		countTransactionsAfter();

		// VOR Software Fees
		assertTrue(unpaidVORCreditMemoSoftwareTransactionsAfter.size() == unpaidVORCreditMemoSoftwareTransactionsBefore.size());
		assertTrue(paidVORCreditMemoSoftwareTransactionsAfter.size() == paidVORCreditMemoSoftwareTransactionsBefore.size());

		// Non VOR Software Fees
		assertTrue(unpaidNonVORCreditMemoSoftwareTransactionsAfter.size() > unpaidNonVORCreditMemoSoftwareTransactionsBefore.size());
		assertTrue(paidNonVORCreditMemoSoftwareTransactionsAfter.size() == paidNonVORCreditMemoSoftwareTransactionsBefore.size());

		// VOR Fees
		assertTrue(unpaidCreditMemoVORTransactionsAfter.size() == unpaidCreditMemoVORTransactionsBefore.size());
		assertTrue(paidCreditMemoVORTransactionsAfter.size() == paidCreditMemoVORTransactionsBefore.size());

		// Subscription Fees
		assertTrue(unpaidCreditMemoSubscriptionTransactionsAfter.size() > unpaidCreditMemoSubscriptionTransactionsBefore.size());
		assertTrue(paidCreditMemoSubscriptionTransactionsAfter.size() == paidCreditMemoSubscriptionTransactionsBefore.size());

		// Professional Services Fees
		assertTrue(unpaidCreditMemoProfServicesTransactionsAfter.size() > unpaidCreditMemoProfServicesTransactionsBefore.size());
		assertTrue(paidCreditMemoProfServicesTransactionsAfter.size() == paidCreditMemoProfServicesTransactionsBefore.size());

		// Verify Credit Memo does not appear in Ledger
		assertFalse(invoiceInLedger(creditMemo.getInvoiceNumber()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIssueCreditMemo_adHoc_nonSubscriptionItems() {

		AdHocInvoice adHocInvoice = createAdHocInvoice(Collections.singletonList(InvoiceLineItemType.LATE_PAYMENT_FEE));

		// Issue credit memo for paid invoice should throw exception for non-subscription item
		billingService.issueCreditMemo(adHocInvoice.getId());
	}

	@Test
	public void testIssueCreditMemo_unpaidAdHoc_miscItems() {

		AdHocInvoice adHocInvoice = createAdHocInvoice(Collections.singletonList(InvoiceLineItemType.MISC_FEE));

		// Issue credit memo for paid invoice should throw exception for non-subscription item
		billingService.issueCreditMemo(adHocInvoice.getId());
	}

	@Test
	public void testIssueCreditMemo_paidAdHoc_miscItems() {

		AdHocInvoice adHocInvoice = createAdHocInvoice(Collections.singletonList(InvoiceLineItemType.MISC_FEE));

		billingService.payInvoice(user.getId(), adHocInvoice.getId());

		// Issue credit memo for paid invoice should throw exception for non-subscription item
		CreditMemo creditMemo = billingService.issueCreditMemo(adHocInvoice.getId());

		assertEquals(1, creditMemo.getInvoiceLineItems().size());
		assertEquals("misc", creditMemo.getInvoiceLineItems().iterator().next().getType());
		assertEquals("cmItem", creditMemo.getInvoiceLineItems().iterator().next().getRegisterTransaction().getRegisterTransactionType().getCode());
		assertEquals(CreditMemoType.MISC_CREDIT.ordinal(), ((CreditMemoTransaction)creditMemo.getInvoiceLineItems().iterator().next().getRegisterTransaction()).getCreditMemoType());
	}

	/**
	 * Test issue credit invoice against adhoc invoice with only subscription items
	 */
	@Test
	public void testIssueCreditMemo_unpaid_adHocInvoice_subscriptionItems() {

		BigDecimal payablesBalanceBeforeInvoice = accountRegisterService.getAccountsPayableBalance(
			user.getCompany().getId());

		AdHocInvoice invoice = createAdHocInvoice(Collections.singletonList(InvoiceLineItemType.SUBSCRIPTION_SETUP_FEE));
		BigDecimal amount = invoice.getBalance();

		AccountRegisterSummaryFields summaryFieldsBefore = accountRegisterService.getAccountRegisterSummaryFields(
			user.getCompany().getId());

		// Issue credit memo for paid invoice
		CreditMemo creditMemo = billingService.issueCreditMemo(invoice.getId());
		CreditMemoAudit creditMemoAudit = creditMemo.getCreditMemoAudit();
		creditMemoAudit.setReasonId(CreditMemoReasons.WRITE_OFF.getValue());
		creditMemoAudit.setNote("CreditMemoIT - testIssueCreditMemo_unpaid_adHocInvoice_subscriptionItems");
		creditMemoAuditService.saveOrUpdate(creditMemoAudit);

		AccountRegisterSummaryFields summaryFieldsAfter = accountRegisterService.getAccountRegisterSummaryFields(
			user.getCompany().getId());

		assertTrue(summaryFieldsBefore.equals(summaryFieldsAfter));

		invoice = billingService.findInvoiceById(invoice.getId());

		// Credit memo and invoice should now have 'paid' status
		assertEquals(InvoiceStatusType.PAID, invoice.getInvoiceStatusType().getCode());
		assertEquals(InvoiceStatusType.PAID, creditMemo.getInvoiceStatusType().getCode());

		// Credit memo balance should equal invoice balance
		assertTrue(invoice.getBalance().compareTo(creditMemo.getBalance()) == 0);
		assertTrue(amount.compareTo(invoice.getBalance()) == 0);

		// Credit memo and invoice should have no remaining balance
		assertTrue(BigDecimal.ZERO.compareTo(invoice.getRemainingBalance()) == 0);
		assertTrue(BigDecimal.ZERO.compareTo(creditMemo.getRemainingBalance()) == 0);

		// Verify that payables was updated now that invoice was 'paid'
		BigDecimal payablesBalanceAfterCredit = accountRegisterService.getAccountsPayableBalance(
			user.getCompany().getId());
		assertTrue(payablesBalanceBeforeInvoice.compareTo(payablesBalanceAfterCredit) == 0);

		assertNotNull(creditMemo.getCreditMemoAudit());

		assertEquals(invoice, creditMemo.getCreditMemoAudit().getServiceInvoice());

		assertEquals(creditMemo.getInvoiceLineItems().size(), invoice.getInvoiceLineItems().size());
		for (InvoiceLineItem invoiceLineItem : creditMemo.getInvoiceLineItems()) {
			assertNotNull(invoiceLineItem.getRegisterTransaction());

			// Register Transactions are not pending
			assertFalse(invoiceLineItem.getRegisterTransaction().isPending());

			assertFalse(((CreditMemoTransaction) invoiceLineItem.getRegisterTransaction()).isOriginalInvoicePaid());

			// Register Transactions have 'creditMemoItem' register_transaction_type
			assertTrue(invoiceLineItem.getRegisterTransaction().getRegisterTransactionType().getCode().equals(
				RegisterTransactionType.CREDIT_MEMO_ITEM));
		}
	}

	/**
	 * Test issue credit invoice against adhoc invoice with only subscription items
	 */
	@Test
	public void testIssueCreditMemo_paid_adHocInvoice_subscriptionItems() {

		BigDecimal availableCashBeforePayingInvoice = accountRegisterService.calcSufficientBuyerFundsByCompany(
			user.getCompany().getId());


		AdHocInvoice invoice = createAdHocInvoice(Collections.singletonList(InvoiceLineItemType.SUBSCRIPTION_ADD_ON));
		BigDecimal amount = invoice.getBalance();
// Pa
		billingService.payInvoice(user.getId(), invoice.getId());

		invoice = billingService.findInvoiceById(invoice.getId());

		invoice = billingService.findInvoiceById(invoice.getId());

		BigDecimal availableCashAfter = accountRegisterService.calcSufficientBuyerFundsByCompany(
			user.getCompany().getId());

		assertEquals(availableCashBeforePayingInvoice.subtract(amount), availableCashAfter);

		// Issue credit memo for paid invoice
		CreditMemo creditMemo = billingService.issueCreditMemo(invoice.getId());
		CreditMemoAudit creditMemoAudit = creditMemo.getCreditMemoAudit();
		creditMemoAudit.setReasonId(CreditMemoReasons.OTHER.getValue());
		creditMemoAudit.setNote("CreditMemoIT - testIssueCreditMemo_paid_adHocInvoice_subscriptionItems");
		creditMemoAuditService.saveOrUpdate(creditMemoAudit);

		// Credit memo and invoice should now have 'paid' status
		assertEquals(InvoiceStatusType.PAID, invoice.getInvoiceStatusType().getCode());
		assertEquals(InvoiceStatusType.PAID, creditMemo.getInvoiceStatusType().getCode());

		// Credit memo balance should equal invoice balance
		assertTrue(invoice.getBalance().compareTo(creditMemo.getBalance()) == 0);
		assertTrue(amount.compareTo(invoice.getBalance()) == 0);

		// Credit memo and invoice should have no remaining balance
		assertTrue(BigDecimal.ZERO.compareTo(invoice.getRemainingBalance()) == 0);
		assertTrue(BigDecimal.ZERO.compareTo(creditMemo.getRemainingBalance()) == 0);

		// Verify that credit amount was applied to company balance
		BigDecimal availableCashAfterCredit = accountRegisterService.calcSufficientBuyerFundsByCompany(
			user.getCompany().getId());
		assertTrue(availableCashBeforePayingInvoice.compareTo(availableCashAfterCredit) == 0);

		assertNotNull(creditMemo.getCreditMemoAudit());

		assertEquals(invoice, creditMemo.getCreditMemoAudit().getServiceInvoice());

		assertEquals(creditMemo.getInvoiceLineItems().size(), invoice.getInvoiceLineItems().size());
		for (InvoiceLineItem invoiceLineItem : creditMemo.getInvoiceLineItems()) {
			assertNotNull(invoiceLineItem.getRegisterTransaction());

			// Register Transactions are not pending
			assertFalse(invoiceLineItem.getRegisterTransaction().isPending());

			assertTrue(((CreditMemoTransaction) invoiceLineItem.getRegisterTransaction()).isOriginalInvoicePaid());

			// Register Transactions have 'creditMemoItem' register_transaction_type
			assertTrue(invoiceLineItem.getRegisterTransaction().getRegisterTransactionType().getCode().equals(
				RegisterTransactionType.CREDIT_MEMO_ITEM));
		}
	}

	private List<SubscriptionPaymentTierDTO> getSubscriptionPaymentTiers() {
		List<SubscriptionPaymentTierDTO> paymentTiers = Lists.newArrayList();
		SubscriptionPaymentTierDTO subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
		subscriptionPaymentTierDTO.setMinimum(BigDecimal.ZERO);
		subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.TEN);
		subscriptionPaymentTierDTO.setVendorOfRecordAmount(BigDecimal.TEN);
		paymentTiers.add(subscriptionPaymentTierDTO);

		return paymentTiers;
	}

	private List<AccountServiceTypeDTO> createAccountServiceTypeDTOs(boolean vor){

		List<AccountServiceTypeDTO> serviceTypeDTOs = Lists.newArrayList();

		if(vor) {
			AccountServiceTypeDTO accountServiceTypeDTO = new AccountServiceTypeDTO();
			accountServiceTypeDTO.setAccountServiceTypeCode(AccountServiceType.VENDOR_OF_RECORD);
			accountServiceTypeDTO.setCountryCode(Country.USA);
			serviceTypeDTOs.add(accountServiceTypeDTO);
		}
		return serviceTypeDTOs;
	}
	private SubscriptionInvoice getSubscriptionInvoice(boolean isVendorOfRecord, SubscriptionPeriod subscriptionPeriod) {

		List<SubscriptionPaymentTierDTO> paymentTiers = getSubscriptionPaymentTiers();

		List<SubscriptionAddOnDTO> addOnDTOs = Lists.newArrayList();

		for (String addOnTypeCode : SubscriptionAddOnType.addOnTypeCodes) {
			SubscriptionAddOnDTO addOnDTO = new SubscriptionAddOnDTO();
			addOnDTO.setAddOnTypeCode(addOnTypeCode);
			addOnDTO.setEffectiveDate(DateUtilities.getCalendarWithFirstDayOfTheMonth(
				DateUtilities.getCalendarNow(), Constants.EST_TIME_ZONE));
			addOnDTO.setCostPerPeriod(BigDecimal.TEN);
			addOnDTOs.add(addOnDTO);

			totalAddOnCost = totalAddOnCost.add(addOnDTO.getCostPerPeriod());
		}

		SubscriptionConfigurationDTO subscriptionDTO = new SubscriptionConfigurationDTO();
		subscriptionDTO.setSubscriptionPaymentTierDTOs(paymentTiers);
		subscriptionDTO.setEffectiveDate(DateUtilities.getCalendarWithFirstDayOfTheMonth(
			DateUtilities.getMidnightNextMonth(), Constants.EST_TIME_ZONE));
		subscriptionDTO.setNumberOfPeriods(2);
		subscriptionDTO.setSetUpFee(BigDecimal.TEN);
		subscriptionDTO.setSubscriptionPeriod(subscriptionPeriod);
		subscriptionDTO.setClientRefId(SUBSCRIPTION_CLIENT_REF_ID);
		subscriptionDTO.setSubscriptionAddOnDTOs(addOnDTOs);

		subscriptionDTO.setAccountServiceTypeDTOs(createAccountServiceTypeDTOs(isVendorOfRecord));

		subscriptionDTO.setNumberOfPeriods(12);
		subscriptionDTO.setSubscriptionPeriod(subscriptionPeriod);
		subscriptionDTO.setClientRefId("What's up?");
		subscriptionDTO.setSubscriptionTypeCode(SubscriptionType.BAND);

		SubscriptionConfiguration configuration =
			subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(
				user.getCompany().getId(), subscriptionDTO, true);

		companyService.saveOrUpdateSubscriptionInvoicesEmailToCompany(
			user.getCompany().getId(), Lists.newArrayList(new EmailAddressDTO("workmarket@workmarket.com")));
		subscriptionService.approveSubscriptionConfiguration(configuration.getId());

		// We want to test that past invoices will be generated so we set the 'current date' to 6 months after
		// the subscription effective date
		Calendar threeMonthsAfterEffectiveDate = DateUtilities.getCalendarInUTC(DateUtilities.getCalendarNow());
		threeMonthsAfterEffectiveDate.add(Calendar.MONTH, 6);
		Calendar invoiceDate = DateUtilities.getCalendarWithFirstDayOfTheMonth(
			threeMonthsAfterEffectiveDate, TimeZone.getTimeZone("UTC"));

		executor.generateRegularSubscriptionInvoices(invoiceDate);

		SubscriptionInvoice subscriptionInvoice = null;
		List<SubscriptionPaymentPeriod> paymentPeriods = subscriptionService.findAllSubscriptionPaymentPeriods(
			configuration.getId());
		for (SubscriptionPaymentPeriod paymentPeriod : paymentPeriods) {

			if (paymentPeriod.getSubscriptionInvoice() != null) {
				subscriptionInvoice = billingService.findInvoiceById(paymentPeriod.getSubscriptionInvoice().getId());
			}
		}
		return subscriptionInvoice;
	}

	private boolean invoiceInLedger(String invoiceNumber) {
		RegisterTransactionActivityPagination pagination = new RegisterTransactionActivityPagination();
		pagination.setReturnAllRows();
		pagination.setSortColumn(RegisterTransactionActivityPagination.SORTS.TRANSACTION_DATE);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		pagination = accountRegisterService.getLedgerForCompany(user.getCompany().getId(), pagination);
		List<RegisterTransactionActivity> results = pagination.getResults();
		boolean creditMemoInLedger = false;
		for (RegisterTransactionActivity result : results) {
			if (result.getPaidInvoiceSummaryNumber() != null && result.getPaidInvoiceSummaryNumber().equals(invoiceNumber)) {
				creditMemoInLedger = true;
			}
		}
		return creditMemoInLedger;
	}

	private AdHocInvoice createAdHocInvoice(List<InvoiceLineItemType> invoiceItemTypes) {

		InvoiceDTO dto = new InvoiceDTO();
		dto.setCompanyId(user.getCompany().getId());
		dto.setDueDate(DateUtilities.getMidnightNextWeek());

		BigDecimal balance = BigDecimal.ZERO;
		List<InvoiceLineItemDTO> invoiceLineItemDTOs = Lists.newArrayList();
		for (InvoiceLineItemType lineItemType : invoiceItemTypes) {
			InvoiceLineItemDTO lineItemDTO = new InvoiceLineItemDTO(lineItemType);
			lineItemDTO.setAmount(BigDecimal.TEN);
			lineItemDTO.setDescription(lineItemType.getDescription());
			invoiceLineItemDTOs.add(lineItemDTO);
			balance = balance.add(lineItemDTO.getAmount());
		}
		dto.setLineItemDTOList(invoiceLineItemDTOs);
		return billingService.issueAdHocInvoice(dto);
	}

	private void countTransactionsBefore(){

		Calendar startDate = DateUtilities.getMidnightWeekAgo();
		Calendar endDate = DateUtilities.getCalendarNow();

		unpaidVORCreditMemoSoftwareTransactionsBefore =
			journalEntrySummaryService.getCreditMemoTransactions(startDate, endDate,
				CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, true, false, true, true);
		paidVORCreditMemoSoftwareTransactionsBefore = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, true, true, true, true);

		unpaidNonVORCreditMemoSoftwareTransactionsBefore = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, true, false, true, false);
		paidNonVORCreditMemoSoftwareTransactionsBefore = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, true, true, true, false);

		unpaidCreditMemoVORTransactionsBefore = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, Collections.singletonList(CreditMemoType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT_CREDIT.ordinal()),
			true, false, false, false);
		paidCreditMemoVORTransactionsBefore = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, Collections.singletonList(CreditMemoType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT_CREDIT.ordinal()),
			true, true, false, true);

		unpaidCreditMemoProfServicesTransactionsBefore = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, Collections.singletonList(CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal()),
			true, false, false, false);
		paidCreditMemoProfServicesTransactionsBefore = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, Collections.singletonList(CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal()),
			true, true, false, false);

		unpaidCreditMemoSubscriptionTransactionsBefore = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, CreditMemoRegisterTransaction.CREDIT_MEMO_SUBSCRIPTION_TYPE_IDS, true, false, false, false);
		paidCreditMemoSubscriptionTransactionsBefore = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, CreditMemoRegisterTransaction.CREDIT_MEMO_SUBSCRIPTION_TYPE_IDS, true, true, false, false);
	}

	private void countTransactionsAfter(){

		Calendar startDate = DateUtilities.getMidnightWeekAgo();
		Calendar endDate = DateUtilities.getCalendarNow();

		unpaidVORCreditMemoSoftwareTransactionsAfter =
			journalEntrySummaryService.getCreditMemoTransactions(startDate, endDate,
				CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, true, false, true, true);
		paidVORCreditMemoSoftwareTransactionsAfter = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, true, true, true, true);

		unpaidNonVORCreditMemoSoftwareTransactionsAfter = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, true, false, true, false);
		paidNonVORCreditMemoSoftwareTransactionsAfter = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, true, true, true, false);

		unpaidCreditMemoVORTransactionsAfter = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, Collections.singletonList(CreditMemoType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT_CREDIT.ordinal()),
			true, false, false, false);
		paidCreditMemoVORTransactionsAfter = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, Collections.singletonList(CreditMemoType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT_CREDIT.ordinal()),
			true, true, false, false);

		unpaidCreditMemoProfServicesTransactionsAfter = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, Collections.singletonList(CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal()),
			true, false, false, false);
		paidCreditMemoProfServicesTransactionsAfter = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, Collections.singletonList(CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal()),
			true, true, false, false);

		unpaidCreditMemoSubscriptionTransactionsAfter = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, CreditMemoRegisterTransaction.CREDIT_MEMO_SUBSCRIPTION_TYPE_IDS, true, false, false, false);
		paidCreditMemoSubscriptionTransactionsAfter = journalEntrySummaryService.getCreditMemoTransactions(
			startDate, endDate, CreditMemoRegisterTransaction.CREDIT_MEMO_SUBSCRIPTION_TYPE_IDS, true, true, false, false);
	}

	/**
	 * Create invoice, pay it and then create credit invoice against it. The amount invoiced should be credited
	 * to the companies account
	 */
	@Test
	public void testIssueCreditMemo_paidInvoice_NonVOR_switchTo_VOR() throws Exception {

		// Get JES report totals before invoice/creditMemo
		Calendar start = DateUtilities.getCalendarNow();
		start.add(Calendar.DAY_OF_MONTH, -1);
		Calendar end = DateUtilities.getCalendarNow();
		end.add(Calendar.DAY_OF_MONTH, 1);
		AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummaryPre = new AccountingPricingServiceTypeSummary();
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryRevenueValues(
			accountingPricingServiceTypeSummaryPre, start, end, start);

		countTransactionsBefore();

		BigDecimal availableCashBeforePayingInvoice = accountRegisterService.calcSufficientBuyerFundsByCompany(
			user.getCompany().getId());

		SubscriptionInvoice invoice = getSubscriptionInvoice(false, SubscriptionPeriod.QUARTERLY);
		BigDecimal amount = invoice.getBalance();

		// Pay invoice
		billingService.payInvoice(user.getId(), invoice.getId());

		invoice = billingService.findInvoiceById(invoice.getId());

		BigDecimal availableCashAfter = accountRegisterService.calcSufficientBuyerFundsByCompany(
			user.getCompany().getId());

		assertEquals(availableCashBeforePayingInvoice.subtract(amount), availableCashAfter);

		// switch to VOR
		SubscriptionConfigurationDTO subscriptionConfigurationDTO = new SubscriptionConfigurationDTO();
		subscriptionConfigurationDTO.setSubscriptionPaymentTierDTOs(getSubscriptionPaymentTiers());
		subscriptionConfigurationDTO.setEffectiveDate(DateUtilities.getCalendarWithFirstDayOfTheMonth(
			DateUtilities.getMidnightNextMonth(), Constants.EST_TIME_ZONE));
		subscriptionConfigurationDTO.setNumberOfPeriods(2);
		subscriptionConfigurationDTO.setSetUpFee(BigDecimal.TEN);
		subscriptionConfigurationDTO.setSubscriptionPeriod(SubscriptionPeriod.QUARTERLY);
		subscriptionConfigurationDTO.setClientRefId(SUBSCRIPTION_CLIENT_REF_ID);
		subscriptionConfigurationDTO.setAccountServiceTypeDTOs(createAccountServiceTypeDTOs(true));
		subscriptionConfigurationDTO.setSubscriptionTypeCode(SubscriptionType.BAND);
		subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(user.getCompany().getId(), subscriptionConfigurationDTO, false);

		// Issue credit memo for paid invoice
		CreditMemo creditMemo = billingService.issueCreditMemo(invoice.getId());
		CreditMemoAudit creditMemoAudit = creditMemo.getCreditMemoAudit();
		creditMemoAudit.setReasonId(CreditMemoReasons.REFUND.getValue());
		creditMemoAudit.setNote("CreditMemoIT - testIssueCreditMemo_paidInvoice_VOR");
		creditMemoAuditService.saveOrUpdate(creditMemoAudit);

		// Credit memo and invoice should now have 'paid' status
		assertEquals(InvoiceStatusType.PAID, invoice.getInvoiceStatusType().getCode());
		assertEquals(InvoiceStatusType.PAID, creditMemo.getInvoiceStatusType().getCode());

		// Credit memo balance should equal invoice balance
		assertTrue(invoice.getBalance().compareTo(creditMemo.getBalance()) == 0);
		assertTrue(amount.compareTo(invoice.getBalance()) == 0);

		// Credit memo and invoice should have no remaining balance
		assertTrue(BigDecimal.ZERO.compareTo(invoice.getRemainingBalance()) == 0);
		assertTrue(BigDecimal.ZERO.compareTo(creditMemo.getRemainingBalance()) == 0);

		// Verify that credit amount was applied to company balance
		BigDecimal availableCashAfterCredit = accountRegisterService.calcSufficientBuyerFundsByCompany(
			user.getCompany().getId());
		assertTrue(availableCashBeforePayingInvoice.compareTo(availableCashAfterCredit) == 0);

		assertNotNull(creditMemo.getCreditMemoAudit());

		assertEquals(invoice, creditMemo.getCreditMemoAudit().getServiceInvoice());

		assertEquals(creditMemo.getInvoiceLineItems().size(), invoice.getInvoiceLineItems().size());
		for (InvoiceLineItem invoiceLineItem : creditMemo.getInvoiceLineItems()) {
			assertNotNull(invoiceLineItem.getRegisterTransaction());

			// Register Transactions are not pending
			assertFalse(invoiceLineItem.getRegisterTransaction().isPending());

			assertTrue(((CreditMemoTransaction) invoiceLineItem.getRegisterTransaction()).isOriginalInvoicePaid());

			// Register Transactions have 'creditMemoItem' register_transaction_type
			assertTrue(invoiceLineItem.getRegisterTransaction().getRegisterTransactionType().getCode().equals(
				RegisterTransactionType.CREDIT_MEMO_ITEM));
		}

		// Verify JES report accounting is correct
		AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummaryPost = new AccountingPricingServiceTypeSummary();
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryRevenueValues(accountingPricingServiceTypeSummaryPost, start, end, start);

		BigDecimal diffNonVorSoftwareFee = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionNonVorSoftwareFee().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionNonVorSoftwareFee());
		assertTrue(diffNonVorSoftwareFee.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffNonVorSoftwareFeeHistorical = accountingPricingServiceTypeSummaryPost.getCreditSubscriptionNonVorSoftwareFeeHistorical().subtract(accountingPricingServiceTypeSummaryPre.getCreditSubscriptionNonVorSoftwareFeeHistorical());
		assertTrue(diffNonVorSoftwareFeeHistorical.compareTo(BigDecimal.TEN.negate()) == 0);

		BigDecimal diffProfessionalServiceFee = accountingPricingServiceTypeSummaryPost.getCreditProfessionalServiceFee().subtract(accountingPricingServiceTypeSummaryPre.getCreditProfessionalServiceFee());
		assertTrue(diffProfessionalServiceFee.compareTo(totalAddOnCost.negate()) == 0);

		BigDecimal diffProfessionalServiceFeeHistorical = accountingPricingServiceTypeSummaryPost.getCreditProfessionalServiceFeeHistorical().subtract(accountingPricingServiceTypeSummaryPre.getCreditProfessionalServiceFeeHistorical());
		assertTrue(diffProfessionalServiceFeeHistorical.compareTo(totalAddOnCost.negate()) == 0);

		countTransactionsAfter();

		// VOR Software Fees
		assertTrue(unpaidVORCreditMemoSoftwareTransactionsAfter.size() == unpaidVORCreditMemoSoftwareTransactionsBefore.size());
		assertTrue(paidVORCreditMemoSoftwareTransactionsAfter.size() == paidVORCreditMemoSoftwareTransactionsBefore.size());

		// Non VOR Software Fees
		assertTrue(unpaidNonVORCreditMemoSoftwareTransactionsAfter.size() == unpaidNonVORCreditMemoSoftwareTransactionsBefore.size());
		assertTrue(paidNonVORCreditMemoSoftwareTransactionsAfter.size() > paidNonVORCreditMemoSoftwareTransactionsBefore.size());

		// VOR Fees
		assertTrue(unpaidCreditMemoVORTransactionsAfter.size() == unpaidCreditMemoVORTransactionsBefore.size());
		assertTrue(paidCreditMemoVORTransactionsAfter.size() == paidCreditMemoVORTransactionsBefore.size());

		// Subscription Fees
		assertTrue(unpaidCreditMemoSubscriptionTransactionsAfter.size() == unpaidCreditMemoSubscriptionTransactionsBefore.size());
		assertTrue(paidCreditMemoSubscriptionTransactionsAfter.size() > paidCreditMemoSubscriptionTransactionsBefore.size());

		// Professional Services Fees
		assertTrue(unpaidCreditMemoProfServicesTransactionsAfter.size() == unpaidCreditMemoProfServicesTransactionsBefore.size());
		assertTrue(paidCreditMemoProfServicesTransactionsAfter.size() > paidCreditMemoProfServicesTransactionsBefore.size());

		// Verify Credit Memo appears in Ledger
		assertTrue(invoiceInLedger(creditMemo.getInvoiceNumber()));
	}

	@Test
	public void subscriptionInvoice_isCreditMemoIssuable() {
		SubscriptionInvoice invoice = getSubscriptionInvoice(false, SubscriptionPeriod.QUARTERLY);
		assertTrue(billingService.isCreditMemoIssuable(invoice.getId()));
	}

	@Test
	public void latePaymentFeeInvoice_isNotCreditMemoIssuable() {
		AdHocInvoice invoice = createAdHocInvoice(Collections.singletonList(InvoiceLineItemType.LATE_PAYMENT_FEE));
		assertFalse(billingService.isCreditMemoIssuable(invoice.getId()));
	}
}
