 package com.workmarket.service.business;

import com.autotask.ws.Account;
import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementFilters;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountingPricingServiceTypeSummary;
import com.workmarket.domains.model.account.AccountingSummary;
import com.workmarket.domains.model.account.AccountingSummaryDetail;
import com.workmarket.domains.model.account.CreditDebitRegisterTransactionsSummary;
import com.workmarket.domains.model.account.OfflinePaymentSummary;
import com.workmarket.domains.model.account.payment.AccountingProcessTime;
import com.workmarket.domains.model.account.payment.BiweeklyPaymentDays;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.payment.PaymentCycle;
import com.workmarket.domains.model.account.payment.PaymentTermsDays;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.AccountServiceTypeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionAddOnType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriodType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionType;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoicePagination;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.PaymentFulfillmentStatusType;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.invoice.SubscriptionInvoiceType;
import com.workmarket.domains.model.invoice.item.InvoiceLineItemType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.payments.dao.PaymentConfigurationDAO;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkMilestonesService;
import com.workmarket.service.business.account.JournalEntrySummaryService;
import com.workmarket.service.business.account.PaymentConfigurationService;
import com.workmarket.service.business.account.summary.AccountingSummaryService;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.PaymentConfigurationDTO;
import com.workmarket.service.business.dto.StopPaymentDTO;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionAddOnDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentTierDTO;
import com.workmarket.service.business.dto.invoice.InvoiceDTO;
import com.workmarket.service.business.dto.invoice.InvoiceLineItemDTO;
import com.workmarket.service.business.dto.invoice.InvoiceSummaryDTO;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.httpclient.util.DateUtil;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class OfflinePaymentIT extends BaseServiceIT {

	@Autowired private BillingService billingService;
	@Autowired private JournalEntrySummaryService journalEntrySummaryService;
	@Autowired private PaymentConfigurationService paymentConfigurationService;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl") private AccountRegisterService accountRegisterService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired AccountingSummaryService accountingSummaryService;

	@Test
	public void offlinePayment_transactional_nvor() throws Exception {

		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		User contractor = createContractorWithUSAddress();

		Calendar yesterday = DateUtilities.getMidnightYesterday();
		Calendar tomorrow = DateUtilities.getMidnightTomorrow();
		Calendar startFiscalYear = journalEntrySummaryService.findOrCreateStartFiscalYearForDate(Calendar.getInstance());

		setPaymentConfiguration(employee,
			new AccountPricingType(AccountPricingType.TRANSACTIONAL_PRICING_TYPE),
			new AccountServiceType(AccountServiceType.NONE));

		OfflinePaymentSummary offlinePaymentSummaryBefore = new OfflinePaymentSummary();
		journalEntrySummaryService.calculateOfflinePayments(offlinePaymentSummaryBefore, yesterday, tomorrow, startFiscalYear);

		createAndCompleteOfflinePayAssignment(employee, contractor);

		OfflinePaymentSummary offlinePaymentSummaryAfter = new OfflinePaymentSummary();
		journalEntrySummaryService.calculateOfflinePayments(offlinePaymentSummaryAfter, yesterday, tomorrow, startFiscalYear);

		// only transNvor should have changed
		BigDecimal offlineSubsNvorDiff = offlinePaymentSummaryAfter.getOfflineSubsNvor().subtract(offlinePaymentSummaryBefore.getOfflineSubsNvor());
		BigDecimal offlineSubsNvorHisoricalDiff = offlinePaymentSummaryAfter.getOfflineSubsNvorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineSubsNvorHistorical());
		BigDecimal offlineSubsVorDiff = offlinePaymentSummaryAfter.getOfflineSubsVor().subtract(offlinePaymentSummaryBefore.getOfflineSubsVor());
		BigDecimal offlineSubsVorHistoricalDiff = offlinePaymentSummaryAfter.getOfflineSubsVorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineSubsVorHistorical());
		BigDecimal offlineTransNvorDiff = offlinePaymentSummaryAfter.getOfflineTransNvor().subtract(offlinePaymentSummaryBefore.getOfflineTransNvor());
		BigDecimal offlineTransNvorHistoricalDiff = offlinePaymentSummaryAfter.getOfflineTransNvorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineTransNvorHistorical());
		BigDecimal offlineTransVorDiff = offlinePaymentSummaryAfter.getOfflineTransVor().subtract(offlinePaymentSummaryBefore.getOfflineTransVor());
		BigDecimal offlineTransVorHistoricalDiff = offlinePaymentSummaryAfter.getOfflineTransVorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineTransVorHistorical());

		assertEquals(0, BigDecimal.ZERO.compareTo(offlineSubsNvorDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineSubsNvorHisoricalDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineSubsVorDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineSubsVorHistoricalDiff));
		assertEquals(0, BigDecimal.valueOf(DEFAULT_WORK_FLAT_PRICE).compareTo(offlineTransNvorDiff));
		assertEquals(0, BigDecimal.valueOf(DEFAULT_WORK_FLAT_PRICE).compareTo(offlineTransNvorHistoricalDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineTransVorDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineTransVorHistoricalDiff));

		checkAccountingSummaryDetails();
	}

	@Test
	public void offlinePayment_transactional_vor() throws Exception {

		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		User contractor = createContractorWithUSAddress();

		Calendar yesterday = DateUtilities.getMidnightYesterday();
		Calendar tomorrow = DateUtilities.getMidnightTomorrow();
		Calendar startFiscalYear = journalEntrySummaryService.findOrCreateStartFiscalYearForDate(Calendar.getInstance());

		setPaymentConfiguration(employee,
			new AccountPricingType(AccountPricingType.TRANSACTIONAL_PRICING_TYPE),
			new AccountServiceType(AccountServiceType.VENDOR_OF_RECORD));

		OfflinePaymentSummary offlinePaymentSummaryBefore = new OfflinePaymentSummary();
		journalEntrySummaryService.calculateOfflinePayments(offlinePaymentSummaryBefore, yesterday, tomorrow, startFiscalYear);

		createAndCompleteOfflinePayAssignment(employee, contractor);

		OfflinePaymentSummary offlinePaymentSummaryAfter = new OfflinePaymentSummary();
		journalEntrySummaryService.calculateOfflinePayments(offlinePaymentSummaryAfter, yesterday, tomorrow, startFiscalYear);

		// only transNvor should have changed
		BigDecimal offlineSubsNvorDiff = offlinePaymentSummaryAfter.getOfflineSubsNvor().subtract(offlinePaymentSummaryBefore.getOfflineSubsNvor());
		BigDecimal offlineSubsNvorHisoricalDiff = offlinePaymentSummaryAfter.getOfflineSubsNvorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineSubsNvorHistorical());
		BigDecimal offlineSubsVorDiff = offlinePaymentSummaryAfter.getOfflineSubsVor().subtract(offlinePaymentSummaryBefore.getOfflineSubsVor());
		BigDecimal offlineSubsVorHistoricalDiff = offlinePaymentSummaryAfter.getOfflineSubsVorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineSubsVorHistorical());
		BigDecimal offlineTransNvorDiff = offlinePaymentSummaryAfter.getOfflineTransNvor().subtract(offlinePaymentSummaryBefore.getOfflineTransNvor());
		BigDecimal offlineTransNvorHistoricalDiff = offlinePaymentSummaryAfter.getOfflineTransNvorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineTransNvorHistorical());
		BigDecimal offlineTransVorDiff = offlinePaymentSummaryAfter.getOfflineTransVor().subtract(offlinePaymentSummaryBefore.getOfflineTransVor());
		BigDecimal offlineTransVorHistoricalDiff = offlinePaymentSummaryAfter.getOfflineTransVorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineTransVorHistorical());

		assertEquals(0, BigDecimal.ZERO.compareTo(offlineSubsNvorDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineSubsNvorHisoricalDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineSubsVorDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineSubsVorHistoricalDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineTransNvorDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineTransNvorHistoricalDiff));
		assertEquals(0, BigDecimal.valueOf(DEFAULT_WORK_FLAT_PRICE).compareTo(offlineTransVorDiff));
		assertEquals(0, BigDecimal.valueOf(DEFAULT_WORK_FLAT_PRICE).compareTo(offlineTransVorHistoricalDiff));

		checkAccountingSummaryDetails();
	}

	@Test
	public void offlinePayment_subscription_nvor() throws Exception {

		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		User contractor = createContractorWithUSAddress();

		Calendar yesterday = DateUtilities.getMidnightYesterday();
		Calendar tomorrow = DateUtilities.getMidnightTomorrow();
		Calendar startFiscalYear = journalEntrySummaryService.findOrCreateStartFiscalYearForDate(Calendar.getInstance());

		setPaymentConfiguration(employee,
			new AccountPricingType(AccountPricingType.SUBSCRIPTION_PRICING_TYPE),
			new AccountServiceType(AccountServiceType.NONE));

		OfflinePaymentSummary offlinePaymentSummaryBefore = new OfflinePaymentSummary();
		journalEntrySummaryService.calculateOfflinePayments(offlinePaymentSummaryBefore, yesterday, tomorrow, startFiscalYear);

		createAndCompleteOfflinePayAssignment(employee, contractor);

		OfflinePaymentSummary offlinePaymentSummaryAfter = new OfflinePaymentSummary();
		journalEntrySummaryService.calculateOfflinePayments(offlinePaymentSummaryAfter, yesterday, tomorrow, startFiscalYear);

		// only transNvor should have changed
		BigDecimal offlineSubsNvorDiff = offlinePaymentSummaryAfter.getOfflineSubsNvor().subtract(offlinePaymentSummaryBefore.getOfflineSubsNvor());
		BigDecimal offlineSubsNvorHisoricalDiff = offlinePaymentSummaryAfter.getOfflineSubsNvorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineSubsNvorHistorical());
		BigDecimal offlineSubsVorDiff = offlinePaymentSummaryAfter.getOfflineSubsVor().subtract(offlinePaymentSummaryBefore.getOfflineSubsVor());
		BigDecimal offlineSubsVorHistoricalDiff = offlinePaymentSummaryAfter.getOfflineSubsVorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineSubsVorHistorical());
		BigDecimal offlineTransNvorDiff = offlinePaymentSummaryAfter.getOfflineTransNvor().subtract(offlinePaymentSummaryBefore.getOfflineTransNvor());
		BigDecimal offlineTransNvorHistoricalDiff = offlinePaymentSummaryAfter.getOfflineTransNvorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineTransNvorHistorical());
		BigDecimal offlineTransVorDiff = offlinePaymentSummaryAfter.getOfflineTransVor().subtract(offlinePaymentSummaryBefore.getOfflineTransVor());
		BigDecimal offlineTransVorHistoricalDiff = offlinePaymentSummaryAfter.getOfflineTransVorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineTransVorHistorical());

		assertEquals(0, BigDecimal.valueOf(DEFAULT_WORK_FLAT_PRICE).compareTo(offlineSubsNvorDiff));
		assertEquals(0, BigDecimal.valueOf(DEFAULT_WORK_FLAT_PRICE).compareTo(offlineSubsNvorHisoricalDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineSubsVorDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineSubsVorHistoricalDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineTransNvorDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineTransNvorHistoricalDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineTransVorDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineTransVorHistoricalDiff));

		checkAccountingSummaryDetails();
	}

	@Test
	public void offlinePayment_subscription_vor() throws Exception {

		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		User contractor = createContractorWithUSAddress();

		Calendar yesterday = DateUtilities.getMidnightYesterday();
		Calendar tomorrow = DateUtilities.getMidnightTomorrow();
		Calendar startFiscalYear = journalEntrySummaryService.findOrCreateStartFiscalYearForDate(Calendar.getInstance());

		setPaymentConfiguration(employee,
			new AccountPricingType(AccountPricingType.SUBSCRIPTION_PRICING_TYPE),
			new AccountServiceType(AccountServiceType.VENDOR_OF_RECORD));

		OfflinePaymentSummary offlinePaymentSummaryBefore = new OfflinePaymentSummary();
		journalEntrySummaryService.calculateOfflinePayments(offlinePaymentSummaryBefore, yesterday, tomorrow, startFiscalYear);

		createAndCompleteOfflinePayAssignment(employee, contractor);

		OfflinePaymentSummary offlinePaymentSummaryAfter = new OfflinePaymentSummary();
		journalEntrySummaryService.calculateOfflinePayments(offlinePaymentSummaryAfter, yesterday, tomorrow, startFiscalYear);

		// only transNvor should have changed
		BigDecimal offlineSubsNvorDiff = offlinePaymentSummaryAfter.getOfflineSubsNvor().subtract(offlinePaymentSummaryBefore.getOfflineSubsNvor());
		BigDecimal offlineSubsNvorHisoricalDiff = offlinePaymentSummaryAfter.getOfflineSubsNvorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineSubsNvorHistorical());
		BigDecimal offlineSubsVorDiff = offlinePaymentSummaryAfter.getOfflineSubsVor().subtract(offlinePaymentSummaryBefore.getOfflineSubsVor());
		BigDecimal offlineSubsVorHistoricalDiff = offlinePaymentSummaryAfter.getOfflineSubsVorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineSubsVorHistorical());
		BigDecimal offlineTransNvorDiff = offlinePaymentSummaryAfter.getOfflineTransNvor().subtract(offlinePaymentSummaryBefore.getOfflineTransNvor());
		BigDecimal offlineTransNvorHistoricalDiff = offlinePaymentSummaryAfter.getOfflineTransNvorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineTransNvorHistorical());
		BigDecimal offlineTransVorDiff = offlinePaymentSummaryAfter.getOfflineTransVor().subtract(offlinePaymentSummaryBefore.getOfflineTransVor());
		BigDecimal offlineTransVorHistoricalDiff = offlinePaymentSummaryAfter.getOfflineTransVorHistorical().subtract(offlinePaymentSummaryBefore.getOfflineTransVorHistorical());

		assertEquals(0, BigDecimal.ZERO.compareTo(offlineSubsNvorDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineSubsNvorHisoricalDiff));
		assertEquals(0, BigDecimal.valueOf(DEFAULT_WORK_FLAT_PRICE).compareTo(offlineSubsVorDiff));
		assertEquals(0, BigDecimal.valueOf(DEFAULT_WORK_FLAT_PRICE).compareTo(offlineSubsVorHistoricalDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineTransNvorDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineTransNvorHistoricalDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineTransVorDiff));
		assertEquals(0, BigDecimal.ZERO.compareTo(offlineTransVorHistoricalDiff));

		checkAccountingSummaryDetails();
	}

	private void createAndCompleteOfflinePayAssignment(User employee, User contractor) throws Exception {
		authenticationService.setCurrentUser(employee);

		BigDecimal availableCashBefore = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());

		Work work = createWorkAndSendToResourceWithOfflinePayment(employee, contractor);
		assertTrue(workService.isOfflinePayment(work));

		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		Invoice invoice = billingService.findInvoiceByWorkId(work.getId());
		assertNotNull(invoice);

		work = workService.findWork(work.getId());
		assertTrue(work.isPaid());

		assertEquals(InvoiceStatusType.PAID_OFFLINE, invoice.getInvoiceStatusType().getCode());
		assertEquals(invoice.getPaymentFulfillmentStatusType().getCode(), PaymentFulfillmentStatusType.FULFILLED);

		BigDecimal availableCashAfter = accountRegisterService.calcSufficientBuyerFundsByCompany(employee.getCompany().getId());

		assertEquals(availableCashAfter, availableCashBefore);
	}

	private void setPaymentConfiguration(User worker, AccountPricingType accountPricingType,
		AccountServiceType accountServiceType) {

		PaymentConfiguration paymentConfiguration = worker.getCompany().getPaymentConfiguration();
		paymentConfiguration.setAccountPricingType(accountPricingType);

		accountPricingService.updatePaymentConfigurationAccountServiceType(paymentConfiguration, accountServiceType);

		paymentConfigurationService.savePaymentConfiguration(paymentConfiguration);
	}

	private User createContractorWithUSAddress() throws Exception {
		User contractor = newContractor();
		Address address = new Address();
		address.setAddress1("7 High Street");
		address.setCity("Huntington");
		address.setState(invariantDataService.findState("NY"));
		address.setPostalCode("11743");
		address.setCountry(Country.USA_COUNTRY);
		address.setAddressType(new AddressType("business"));

		profileService.updateAddress(contractor.getProfile().getId(), address);

		return contractor;
	}

	private void checkAccountingSummaryDetails() {

		AccountingSummary summary = getBaseAccountingSummary();

		Calendar end = summary.getRequestDate();
		Calendar start = summary.getPreviousRequestDate();
		Calendar startFiscalYear = journalEntrySummaryService.findOrCreateStartFiscalYearForDate(Calendar.getInstance());

		OfflinePaymentSummary offlinePaymentSummary = new OfflinePaymentSummary();
		journalEntrySummaryService.calculateOfflinePayments(offlinePaymentSummary, start, end, startFiscalYear);
		summary.setOfflinePaymentSummary(offlinePaymentSummary);

		journalEntrySummaryService.saveSummary(summary);

		assertEquals(0,
			getDetailTotal(journalEntrySummaryService.getOfflinePaymentDetails(summary.getId(), false, false, false))
				.compareTo(summary.getOfflineTransactionNVOR()));
		assertEquals(0,
			getDetailTotal(journalEntrySummaryService.getOfflinePaymentDetails(summary.getId(), false, true, false))
				.compareTo(summary.getOfflineTransactionVOR()));
		assertEquals(0,
			getDetailTotal(journalEntrySummaryService.getOfflinePaymentDetails(summary.getId(), true, false, false))
				.compareTo(summary.getOfflineSubscriptionNVOR()));
		assertEquals(0,
			getDetailTotal(journalEntrySummaryService.getOfflinePaymentDetails(summary.getId(), true, true, false))
				.compareTo(summary.getOfflineSubscriptionVOR()));

		assertEquals(0,
			getDetailTotal(journalEntrySummaryService.getOfflinePaymentDetails(summary.getId(), false, false, true))
				.compareTo(summary.getOfflineTransactionNVORHistorical()));
		assertEquals(0,
			getDetailTotal(journalEntrySummaryService.getOfflinePaymentDetails(summary.getId(), false, true, true))
				.compareTo(summary.getOfflineTransactionVORHistorical()));
		assertEquals(0,
			getDetailTotal(journalEntrySummaryService.getOfflinePaymentDetails(summary.getId(), true, false, true))
				.compareTo(summary.getOfflineSubscriptionNVORHistorical()));
		assertEquals(0,
			getDetailTotal(journalEntrySummaryService.getOfflinePaymentDetails(summary.getId(), true, true, true))
				.compareTo(summary.getOfflineSubscriptionVORHistorical()));
	}

	private BigDecimal getDetailTotal(List<AccountingSummaryDetail> transactions) {
		BigDecimal total = BigDecimal.ZERO;
		for(AccountingSummaryDetail transaction : transactions) {
			total = total.add(transaction.getAmount());
		}
		return total;
	}

}
