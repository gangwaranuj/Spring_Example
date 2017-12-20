package com.workmarket.service.business.account;

import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.account.pricing.CompanyAccountPricingTypeChangeDAO;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.payments.dao.PaymentPeriodDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionAccountServiceTypeConfigurationDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionAddOnTypeAssociationDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionCancellationDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionConfigurationDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionFeeConfigurationDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionPaymentTierDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionPaymentTierRenewalDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionRenewalRequestDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionThroughputIncrementTransactionDAO;
import com.workmarket.domains.payments.dao.InvoiceLineItemDAO;
import com.workmarket.domains.payments.dao.ServiceInvoiceDAO;
import com.workmarket.dao.note.NoteDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.TransactionStatus;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionFeeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionThroughputIncrementTransaction;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.SubscriptionInvoiceType;
import com.workmarket.domains.model.invoice.WMInvoiceNumberType;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentDTO;
import com.workmarket.service.business.dto.invoice.InvoiceLineItemDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionServiceTest {

	@Mock private AuthenticationService authenticationService;
	@Mock private SubscriptionFeeConfigurationDAO subscriptionFeeConfigurationDAO;
	@Mock private SubscriptionConfigurationDAO subscriptionConfigurationDAO;
	@Mock private SubscriptionAddOnTypeAssociationDAO subscriptionAddOnTypeAssociationDAO;
	@Mock private NoteDAO noteDAO;
	@Mock private LookupEntityDAO lookupEntityDAO;
	@Mock private CompanyDAO companyDAO;
	@Mock private ServiceInvoiceDAO serviceInvoiceDAO;
	@Mock private InvoiceLineItemDAO invoiceLineItemDAO;
	@Mock private PaymentPeriodDAO paymentPeriodDAO;
	@Mock private InvoiceLineItemFactory invoiceLineItemFactory;
	@Mock private CompanyAccountPricingTypeChangeDAO companyAccountPricingTypeChangeDAO;
	@Mock private SubscriptionPaymentTierDAO subscriptionPaymentTierDAO;
	@Mock
	@Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterService;
	@Mock private SubscriptionThroughputIncrementTransactionDAO subscriptionThroughputIncrementInvoiceDAO;
	@Mock private SubscriptionCancellationDAO subscriptionCancellationDAO;
	@Mock private UserNotificationService userNotificationService;
	@Mock private SubscriptionCalculator subscriptionCalculator;
	@Mock private SubscriptionAccountServiceTypeConfigurationDAO subscriptionAccountServiceTypeConfigurationDAO;
	@Mock private SubscriptionRenewalRequestDAO subscriptionRenewalRequestDAO;
	@Mock private BillingService billingService;
	@Mock private AccountPricingService accountPricingService;
	@Mock private SubscriptionPaymentTierRenewalDAO subscriptionPaymentTierRenewalDAO;
	@Mock private PricingService pricingService;
	@InjectMocks SubscriptionServiceImpl subscriptionService;

	private SubscriptionConfiguration subscriptionConfiguration;
	private Calendar effectiveDate;
	private Calendar endDate;
	private Company company;
	private Calendar today;
	private int thisYear;
	private int thisMonth;
	private SubscriptionFeeConfiguration subscriptionFeeConfiguration;
	private AccountRegister accountRegister;
	private PaymentConfiguration paymentConfiguration;
	private SubscriptionThroughputIncrementTransaction subscriptionThroughputIncrementTransaction;
	private WorkResourceTransaction workResourceTransaction;
	private SubscriptionPaymentTier subscriptionPaymentTier;
	private SubscriptionPaymentPeriod subscriptionPaymentPeriod;
	private AccountRegisterSummaryFields accountRegisterSummaryFields;
	private Work work;

	@Before
	public void setUp() throws Exception {
		company = mock(Company.class);
		accountRegister = mock(AccountRegister.class);
		subscriptionFeeConfiguration = mock(SubscriptionFeeConfiguration.class);
		paymentConfiguration = mock(PaymentConfiguration.class);
		today = Calendar.getInstance();
		thisYear = today.get(Calendar.YEAR);
		thisMonth = today.get(Calendar.MONTH);
		subscriptionConfiguration = mock(SubscriptionConfiguration.class);
		work = mock(Work.class);

		when(company.getId()).thenReturn(1L);
		when(companyDAO.get(anyLong())).thenReturn(company);
		when(accountRegister.getAccountRegisterSummaryFields()).thenReturn(mock(AccountRegisterSummaryFields.class));
		when(pricingService.findDefaultRegisterForCompany(anyLong())).thenReturn(accountRegister);
		when(subscriptionConfiguration.isActive()).thenReturn(true);
		when(subscriptionConfiguration.isPendingRenewal()).thenReturn(false);
		when(subscriptionConfiguration.getCompany()).thenReturn(company);
		when(subscriptionConfiguration.getPaymentTermsDays()).thenReturn(30);
		when(subscriptionConfigurationDAO.get(anyLong())).thenReturn(subscriptionConfiguration);
		effectiveDate = DateUtilities.newCalendar(thisYear -1, thisMonth, 1, 0, 0, 0);
		endDate = DateUtilities.newCalendar(thisYear + 2, thisMonth, 1, 0, 0, 0);
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		when(subscriptionConfiguration.getEndDate()).thenReturn(endDate);
		when(subscriptionConfiguration.getActiveSubscriptionFeeConfiguration()).thenReturn(subscriptionFeeConfiguration);
		when(company.getPaymentConfiguration()).thenReturn(paymentConfiguration);
		when(paymentConfiguration.isSubscriptionPricing()).thenReturn(true);
		when(company.getAccountPricingType()).thenReturn(new AccountPricingType(AccountPricingType.SUBSCRIPTION_PRICING_TYPE));

		when(work.getCompany()).thenReturn(company);

		//Increment transaction
		subscriptionThroughputIncrementTransaction = mock(SubscriptionThroughputIncrementTransaction.class);
		workResourceTransaction = mock(WorkResourceTransaction.class);
		subscriptionPaymentTier = mock(SubscriptionPaymentTier.class);
		subscriptionPaymentPeriod = mock(SubscriptionPaymentPeriod.class);
		accountRegisterSummaryFields = mock(AccountRegisterSummaryFields.class);

		when(workResourceTransaction.getId()).thenReturn(1L);
		when(workResourceTransaction.getTransactionDate()).thenReturn(Calendar.getInstance());
		when(workResourceTransaction.getWork()).thenReturn(work);
		when(accountRegisterSummaryFields.getAssignmentSoftwareThroughput()).thenReturn(BigDecimal.TEN);
		when(subscriptionPaymentTier.getId()).thenReturn(1L);
		when(subscriptionPaymentPeriod.getPeriodDateRange()).thenReturn(new DateRange(DateUtilities.getCalendarWithLastDayOfTheMonth(Calendar.getInstance(), TimeZone.getTimeZone("UTC")), Calendar.getInstance()));
		when(subscriptionThroughputIncrementTransaction.getTriggeredByRegisterTransaction()).thenReturn(workResourceTransaction);
		when(subscriptionThroughputIncrementTransaction.getSubscriptionPaymentTier()).thenReturn(subscriptionPaymentTier);
		when(accountRegisterService.findRegisterTransaction(anyLong())).thenReturn(workResourceTransaction);
		when(subscriptionPaymentTierDAO.get(anyLong())).thenReturn(subscriptionPaymentTier);
		when(subscriptionPaymentTier.getSubscriptionFeeConfiguration()).thenReturn(subscriptionFeeConfiguration);
		when(subscriptionFeeConfiguration.getSubscriptionConfiguration()).thenReturn(subscriptionConfiguration);
		when(paymentPeriodDAO.findBySubscriptionConfigurationIdAndDateInRange(anyLong(), any(Calendar.class))).thenReturn(subscriptionPaymentPeriod);
		when(workResourceTransaction.getAccountRegisterSummaryFields()).thenReturn(accountRegisterSummaryFields);
		when(subscriptionPaymentTierDAO.findActiveSubscriptionPaymentTier(anyLong(), any(SubscriptionPaymentTier.PaymentTierCategory.class))).thenReturn(mock(SubscriptionPaymentTier.class));
	}

	@Test
	public void updateYearlySubscriptionThroughput_withNullThroughputDate() throws Exception {
		when(subscriptionConfiguration.getNextThroughputResetDate()).thenReturn(null);
		subscriptionService.updateYearlySubscriptionThroughput(1L);
		verify(accountRegisterService, never()).resetAccountRegisterForAccountPricingType(any(Company.class));
	}

	@Test
	public void updateYearlySubscriptionThroughput_withThroughputDateInTheFuture_success() throws Exception {
		Calendar nextThroughputDate = Calendar.getInstance();
		nextThroughputDate.add(Calendar.DAY_OF_MONTH, 1);
		when(subscriptionConfiguration.getNextThroughputResetDate()).thenReturn(nextThroughputDate);
		subscriptionService.updateYearlySubscriptionThroughput(1L);
		verify(accountRegisterService, never()).resetAccountRegisterForAccountPricingType(any(Company.class));
		verify(subscriptionConfiguration, never()).setNextThroughputResetDate(any(Calendar.class));
	}

	@Test
	public void updateYearlySubscriptionThroughput_withThroughputDateInThePast_success() throws Exception {
		Calendar nextThroughputDate = Calendar.getInstance();
		nextThroughputDate.add(Calendar.DAY_OF_MONTH, -1);
		when(subscriptionConfiguration.getNextThroughputResetDate()).thenReturn(nextThroughputDate);
		subscriptionService.updateYearlySubscriptionThroughput(1L);
		verify(accountRegisterService, times(1)).resetAccountRegisterForAccountPricingType(any(Company.class));
		verify(subscriptionConfiguration, times(1)).setNextThroughputResetDate(any(Calendar.class));
	}

	@Test
	public void calculateNextThroughputResetDate_withNullEffectiveDate() throws Exception {
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(null);
		when(subscriptionConfiguration.getEndDate()).thenReturn(null);
		assertNull(subscriptionService.calculateNextThroughputResetDate(subscriptionConfiguration));
	}

	@Test
	public void calculateNextThroughputResetDate_effectiveDateLessThan12Month_nextThroughputDateOneYearLater() throws Exception {
		/*
			Effective date 2014-05-01
			Today date  2014-10-01
			Next throughput date should be 2015-05-01
		*/

		Calendar effectiveDate = DateUtilities.newCalendar(thisYear, thisMonth, 1, 0, 0, 0);
		effectiveDate.add(Calendar.MONTH, -9);
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		Calendar nextThroughputResetDate = subscriptionService.calculateNextThroughputResetDate(subscriptionConfiguration);
		assertNotNull(nextThroughputResetDate);
		assertEquals(nextThroughputResetDate.get(Calendar.YEAR), effectiveDate.get(Calendar.YEAR) + 1);
		assertEquals(nextThroughputResetDate.get(Calendar.MONTH), effectiveDate.get(Calendar.MONTH));
		assertEquals(nextThroughputResetDate.get(Calendar.DAY_OF_MONTH), 1);
	}

	@Test
	public void calculateNextThroughputResetDate_effectiveDateMoreThan12Month_nextThroughputDateTwoYearLater() throws Exception {
		/*
			Effective date 2013-05-01
			Today date  2014-10-01
			Next throughput date should be 2015-05-01
		*/

		Calendar effectiveDate = DateUtilities.newCalendar(thisYear, thisMonth, 1, 0, 0, 0);
		effectiveDate.add(Calendar.MONTH, -15);
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		Calendar nextThroughputResetDate = subscriptionService.calculateNextThroughputResetDate(subscriptionConfiguration);
		assertNotNull(nextThroughputResetDate);
		assertEquals(nextThroughputResetDate.get(Calendar.YEAR), effectiveDate.get(Calendar.YEAR) + 2);
		assertEquals(nextThroughputResetDate.get(Calendar.MONTH), effectiveDate.get(Calendar.MONTH));
		assertEquals(nextThroughputResetDate.get(Calendar.DAY_OF_MONTH), 1);
	}

	@Test
	public void newSubscriptionInvoiceFromSubscriptionPaymentPeriod_withRegularInvoiceType_success() throws Exception {
		DateRange dateRange = mock(DateRange.class);
		TimeZone utc = TimeZone.getTimeZone("UTC");
		when(dateRange.getFrom()).thenReturn(DateUtilities.getCalendarWithFirstDayOfTheMonth(DateUtilities.newCalendar(2014, Calendar.JANUARY, 1, 0, 0, 0, utc), utc));
		SubscriptionPaymentPeriod subscriptionPaymentPeriod = mock(SubscriptionPaymentPeriod.class);
		when(subscriptionPaymentPeriod.getPeriodDateRange()).thenReturn(dateRange);
		when(subscriptionPaymentPeriod.getSubscriptionConfiguration()).thenReturn(subscriptionConfiguration);
		SubscriptionPaymentDTO subscriptionPaymentDTO = new SubscriptionPaymentDTO();
		subscriptionPaymentDTO.setSoftwareFeeAmount(BigDecimal.TEN);
		subscriptionPaymentDTO.setVorFeeAmount(BigDecimal.ONE);
		when(subscriptionCalculator.calculateSubscriptionPayment(any(SubscriptionConfiguration.class), any(BigDecimal.class), any(Calendar.class))).thenReturn(subscriptionPaymentDTO);
		SubscriptionInvoiceType subscriptionInvoiceType = new SubscriptionInvoiceType(SubscriptionInvoiceType.REGULAR);
		subscriptionInvoiceType.setDescription("Software Fees");
		when(lookupEntityDAO.findByCode(eq(SubscriptionInvoiceType.class), anyString())).thenReturn(subscriptionInvoiceType);

		SubscriptionInvoice subscriptionInvoice = mock(SubscriptionInvoice.class);
		when(billingService.addInvoiceLineItems(any(SubscriptionInvoice.class), anyListOf(InvoiceLineItemDTO.class))).thenReturn(subscriptionInvoice);

		subscriptionService.newSubscriptionInvoiceFromSubscriptionPaymentPeriod(SubscriptionInvoiceType.REGULAR, subscriptionPaymentPeriod);

		verify(subscriptionPaymentPeriod).setSubscriptionFeeConfigurationId(anyLong());
		verify(subscriptionPaymentPeriod).setSubscriptionPaymentTierSWId(anyLong());
		verify(subscriptionPaymentPeriod).setSubscriptionPaymentTierVORId(anyLong());
		verify(billingService, times(1)).getNextWorkMarketInvoiceNumber(eq(WMInvoiceNumberType.WORK_MARKET_INC_INVOICE));
		verify(userNotificationService, times(1)).onNewInvoice(any(Invoice.class));
	}

	@Test
	public void newSubscriptionInvoiceFromSubscriptionPaymentPeriod_withRegularInvoiceTypeAndRenewal_success() throws Exception {
		DateRange dateRange = mock(DateRange.class);
		TimeZone utc = TimeZone.getTimeZone("UTC");
		when(subscriptionConfiguration.isPendingRenewal()).thenReturn(true);
		when(subscriptionConfiguration.isActive()).thenReturn(false);
		when(dateRange.getFrom()).thenReturn(DateUtilities.getCalendarWithFirstDayOfTheMonth(DateUtilities.newCalendar(2014, Calendar.JANUARY, 1, 0, 0, 0, utc), utc));
		SubscriptionPaymentPeriod subscriptionPaymentPeriod = mock(SubscriptionPaymentPeriod.class);
		when(subscriptionPaymentPeriod.getPeriodDateRange()).thenReturn(dateRange);
		when(subscriptionPaymentPeriod.getSubscriptionConfiguration()).thenReturn(subscriptionConfiguration);
		SubscriptionPaymentDTO subscriptionPaymentDTO = new SubscriptionPaymentDTO();
		subscriptionPaymentDTO.setSoftwareFeeAmount(BigDecimal.TEN);
		subscriptionPaymentDTO.setVorFeeAmount(BigDecimal.ONE);
		when(subscriptionCalculator.calculateSubscriptionPayment(any(SubscriptionConfiguration.class), any(BigDecimal.class), any(Calendar.class))).thenReturn(subscriptionPaymentDTO);
		SubscriptionInvoiceType subscriptionInvoiceType = new SubscriptionInvoiceType(SubscriptionInvoiceType.REGULAR);
		subscriptionInvoiceType.setDescription("Software Fees");
		when(lookupEntityDAO.findByCode(eq(SubscriptionInvoiceType.class), anyString())).thenReturn(subscriptionInvoiceType);
		subscriptionService.newSubscriptionInvoiceFromSubscriptionPaymentPeriod(SubscriptionInvoiceType.REGULAR, subscriptionPaymentPeriod);
		verify(billingService, times(1)).getNextWorkMarketInvoiceNumber(eq(WMInvoiceNumberType.WORK_MARKET_INC_INVOICE));
		verify(userNotificationService, times(1)).onNewInvoice(any(Invoice.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void changeCompanyAccountPricingTypeFromSubscriptionToTransactional_withNoExpirationDate_failed() {
		subscriptionService.changeCompanyAccountPricingTypeFromSubscriptionToTransactional(subscriptionConfiguration, Calendar.getInstance(), false);
		verify(accountPricingService, times(1)).updatePaymentConfigurationAccountServiceType(any(PaymentConfiguration.class), eq(new AccountServiceType(AccountServiceType.NONE)));
		verify(accountRegisterService, times(1)).resetAccountRegisterForAccountPricingType(eq(subscriptionConfiguration.getCompany()));
	}

	@Test
	public void changeCompanyAccountPricingTypeFromSubscriptionToTransactional_success() {
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.DAY_OF_MONTH, 1);
		when(subscriptionConfiguration.getEndDate()).thenReturn(endDate);
		subscriptionService.changeCompanyAccountPricingTypeFromSubscriptionToTransactional(subscriptionConfiguration, endDate, false);
		verify(accountPricingService, times(1)).updatePaymentConfigurationAccountServiceType(any(PaymentConfiguration.class), eq(new AccountServiceType(AccountServiceType.NONE)));
		verify(accountRegisterService, times(1)).resetAccountRegisterForAccountPricingType(eq(subscriptionConfiguration.getCompany()));
	}

	@Test
	public void processThroughputIncrementTransaction_withSWIncrement_success() {
		when(subscriptionThroughputIncrementTransaction.isSoftwareIncrement()).thenReturn(true);
		subscriptionService.processThroughputIncrementTransaction(subscriptionThroughputIncrementTransaction);

		verify(subscriptionCalculator, times(1)).calculateIncrementalSubscriptionPayment(eq(subscriptionConfiguration), any(Calendar.class), eq(subscriptionPaymentTier), eq(true), eq(false));
		verify(accountRegisterService, times(1)).createSubscriptionIncrementalTransactions(any(Company.class), eq(subscriptionPaymentPeriod), any(SubscriptionPaymentDTO.class), eq(true));
		verify(subscriptionThroughputIncrementTransaction, times(1)).setTransactionStatus(eq(new TransactionStatus(TransactionStatus.PROCESSED)));
	}

	@Test
	public void processThroughputIncrementTransaction_withVORIncrement_success() {
		when(subscriptionThroughputIncrementTransaction.isVorIncrement()).thenReturn(true);
		subscriptionService.processThroughputIncrementTransaction(subscriptionThroughputIncrementTransaction);

		verify(subscriptionCalculator, times(1)).calculateIncrementalSubscriptionPayment(eq(subscriptionConfiguration), any(Calendar.class), eq(subscriptionPaymentTier), eq(false), eq(true));
		verify(accountRegisterService, times(1)).createSubscriptionIncrementalTransactions(any(Company.class), eq(subscriptionPaymentPeriod), any(SubscriptionPaymentDTO.class), eq(true));
		verify(subscriptionThroughputIncrementTransaction, times(1)).setTransactionStatus(eq(new TransactionStatus(TransactionStatus.PROCESSED)));
	}
}
