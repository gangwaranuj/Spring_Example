package com.workmarket.service.business.account;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.payments.dao.PaymentPeriodDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionPaymentTierDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.domains.payments.dao.ServiceInvoiceDAO;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.SubscriptionAccountServiceTypeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionFeeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.invoice.item.InvoiceLineItem;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionCalculatorImplTest {

	@Mock SubscriptionPaymentTierDAO subscriptionPaymentTierDAO;
	@Mock PaymentPeriodDAO subscriptionPaymentPeriodDAO;
	@Mock ServiceInvoiceDAO serviceInvoiceDAO;
	@Mock RegisterTransactionDAO registerTransactionDAO;
	@Mock SubscriptionService subscriptionService;

	@InjectMocks SubscriptionCalculatorImpl subscriptionCalculator;

	private SubscriptionConfiguration subscriptionConfiguration;
	private SubscriptionFeeConfiguration subscriptionFeeConfiguration;
	private SubscriptionPaymentTier firstTier;
	private SubscriptionPaymentTier secondTier;
	private SubscriptionPaymentTier thirdTier;
	private Calendar endDate = DateUtilities.newCalendar(2016, Calendar.JANUARY, 1, 0, 0, 0);
	private Calendar nextThroughputResetDate = DateUtilities.newCalendar(2015, Calendar.JANUARY, 1, 0, 0, 0);
	private Calendar transactionDate = DateUtilities.newCalendar(2014, Calendar.JANUARY, 1, 0, 0, 0);
	private DateRange dateRange;

	private SubscriptionPaymentPeriod subscriptionPaymentPeriod;
	private SubscriptionAccountServiceTypeConfiguration vendorOfRecordServiceType = new SubscriptionAccountServiceTypeConfiguration();
	private SubscriptionAccountServiceTypeConfiguration nonVendorOfRecordServiceType = new SubscriptionAccountServiceTypeConfiguration();

	private SubscriptionFeeConfiguration feeConfiguration1;
	private SubscriptionFeeConfiguration feeConfiguration2;
	private SubscriptionFeeConfiguration feeConfiguration3;

	private Calendar date1;
	private Calendar date2;
	private Calendar date3;

	private Long companyId = new Long(10);

	@Before
	public void setUp() throws Exception {
		subscriptionConfiguration = new SubscriptionConfiguration();
		subscriptionConfiguration.setId(1L);
		subscriptionConfiguration.setEndDate(Calendar.getInstance());
		subscriptionFeeConfiguration = new SubscriptionFeeConfiguration();
		subscriptionFeeConfiguration.setId(2L);
		subscriptionFeeConfiguration.setActive(true);
		subscriptionFeeConfiguration.setEffectiveDate(Calendar.getInstance());
		vendorOfRecordServiceType.setAccountServiceType(new AccountServiceType(AccountServiceType.VENDOR_OF_RECORD));
		nonVendorOfRecordServiceType.setAccountServiceType(new AccountServiceType(AccountServiceType.NONE));

		firstTier = new SubscriptionPaymentTier();
		secondTier = new SubscriptionPaymentTier();
		thirdTier = new SubscriptionPaymentTier();

		firstTier.setId(1L);
		firstTier.setMinimum(BigDecimal.ZERO);
		firstTier.setMaximum(BigDecimal.TEN);

		secondTier.setId(2L);
		secondTier.setMinimum(BigDecimal.TEN);
		secondTier.setMaximum(BigDecimal.valueOf(1000));

		thirdTier.setId(3L);
		thirdTier.setMinimum(BigDecimal.valueOf(1000));
		thirdTier.setMaximum(BigDecimal.valueOf(1000000));

		firstTier.setPaymentAmount(BigDecimal.TEN);
		secondTier.setPaymentAmount(BigDecimal.valueOf(100));
		thirdTier.setPaymentAmount(BigDecimal.valueOf(1000));

		firstTier.setVendorOfRecordAmount(BigDecimal.valueOf(5));
		secondTier.setVendorOfRecordAmount(BigDecimal.valueOf(50));
		thirdTier.setVendorOfRecordAmount(BigDecimal.valueOf(500));

		subscriptionFeeConfiguration.setSubscriptionPaymentTiers(Lists.newArrayList(firstTier, secondTier, thirdTier));

		Set<SubscriptionFeeConfiguration> subscriptionFeeConfigurations = Sets.newHashSet(subscriptionFeeConfiguration);
		subscriptionConfiguration.setSubscriptionFeeConfigurations(subscriptionFeeConfigurations);

		subscriptionConfiguration.setSubscriptionPeriod(SubscriptionPeriod.MONTHLY);
		subscriptionConfiguration.setAccountServiceTypeConfigurations(Sets.newHashSet(vendorOfRecordServiceType));
		subscriptionConfiguration.setEndDate(endDate);
		subscriptionConfiguration.setNextThroughputResetDate(nextThroughputResetDate);
		subscriptionFeeConfiguration.setSubscriptionConfiguration(subscriptionConfiguration);

		when(subscriptionPaymentTierDAO.findActiveSubscriptionPaymentTier(anyLong(), any(SubscriptionPaymentTier.PaymentTierCategory.class))).thenReturn(secondTier);

		subscriptionPaymentPeriod = mock(SubscriptionPaymentPeriod.class);
		SubscriptionInvoice invoice = mock(SubscriptionInvoice.class);
		InvoiceLineItem invoiceLineItem = mock(InvoiceLineItem.class);
		dateRange = mock(DateRange.class);
		when(dateRange.getFrom()).thenReturn(transactionDate);
		when(subscriptionPaymentPeriod.getPeriodDateRange()).thenReturn(dateRange);
		when(subscriptionPaymentPeriod.getId()).thenReturn(10l);

		when(invoiceLineItem.getType()).thenReturn(InvoiceLineItem.SUBSCRIPTION_VOR_SOFTWARE_FEE_INVOICE_LINE_ITEM);
		when(invoiceLineItem.getAmount()).thenReturn(BigDecimal.TEN);
		Set<InvoiceLineItem> invoiceLineItems = Sets.newHashSet(invoiceLineItem);

		when(invoice.getInvoiceLineItems()).thenReturn(invoiceLineItems);
		when(subscriptionPaymentPeriod.getSubscriptionInvoice()).thenReturn(invoice);
		when(serviceInvoiceDAO.get(anyLong())).thenReturn(invoice);

		feeConfiguration1 = new SubscriptionFeeConfiguration();
		feeConfiguration2 = new SubscriptionFeeConfiguration();
		feeConfiguration3 = new SubscriptionFeeConfiguration();

		date1 = DateUtilities.newCalendar(2014, Calendar.JUNE, 1, 0, 0, 0, TimeZone.getTimeZone("UTC"));
		date2 = DateUtilities.newCalendar(2014, Calendar.NOVEMBER, 1, 0, 0, 0, TimeZone.getTimeZone("UTC"));
		date3 = DateUtilities.newCalendar(2015, Calendar.JANUARY, 1, 0, 0, 0, TimeZone.getTimeZone("UTC"));

		feeConfiguration1.setEffectiveDate(date1);
		feeConfiguration2.setEffectiveDate(date2);
		feeConfiguration3.setEffectiveDate(date3);

		feeConfiguration1.setId(1L);
		feeConfiguration2.setId(2L);
		feeConfiguration3.setId(3L);

		when(registerTransactionDAO.calculateIncrementalSubscriptionTransactionPendingInvoiceForPaymentPeriod(anyLong(), anyLong(), anyString())).thenReturn(BigDecimal.ZERO);

		when(subscriptionService.findActiveSubscriptionConfigurationByCompanyId(companyId)).thenReturn(subscriptionConfiguration);
	}


	@Test(expected = IllegalArgumentException.class)
	public void calculateSubscriptionPayment_withNullValues_fail() {
		subscriptionCalculator.calculateSubscriptionPayment(null, null, null);
	}

	@Test
	public void calculateSubscriptionPayment_withoutNextThroughputResetDateAndFirstTier_returnsActivePaymentTier() {
		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateSubscriptionPayment(subscriptionConfiguration, BigDecimal.valueOf(1000), Calendar.getInstance());
		assertNotNull(subscriptionPaymentDTO);
		assertEquals(BigDecimal.valueOf(10), subscriptionPaymentDTO.getSoftwareFeeAmount());
		assertEquals(BigDecimal.valueOf(5), subscriptionPaymentDTO.getVorFeeAmount());
		verify(subscriptionPaymentTierDAO, times(0)).findActiveSubscriptionPaymentTier(anyLong(), any(SubscriptionPaymentTier.PaymentTierCategory.class));
	}

	@Test
	public void calculateSubscriptionPayment_withoutNextThroughputResetDateAndFirstTierNoVOR_returnsActivePaymentTier() {
		subscriptionConfiguration.setAccountServiceTypeConfigurations(Sets.newHashSet(nonVendorOfRecordServiceType));
		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateSubscriptionPayment(subscriptionConfiguration, BigDecimal.valueOf(1000), Calendar.getInstance());
		assertNotNull(subscriptionPaymentDTO);
		assertEquals(BigDecimal.valueOf(10), subscriptionPaymentDTO.getSoftwareFeeAmount());
		assertEquals(BigDecimal.ZERO, subscriptionPaymentDTO.getVorFeeAmount());
		verify(subscriptionPaymentTierDAO, times(0)).findActiveSubscriptionPaymentTier(anyLong(), any(SubscriptionPaymentTier.PaymentTierCategory.class));
	}

	@Test
	public void calculateSubscriptionPayment_withNextThroughputResetDateAfterTxDate_returnsActivePaymentTier() {
		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateSubscriptionPayment(subscriptionConfiguration, BigDecimal.valueOf(1000), transactionDate);
		assertNotNull(subscriptionPaymentDTO);
		assertEquals(subscriptionPaymentDTO.getSoftwareFeeAmount(), BigDecimal.valueOf(100));
		assertEquals(subscriptionPaymentDTO.getVorFeeAmount(), BigDecimal.valueOf(50));
		verify(subscriptionPaymentTierDAO, times(2)).findActiveSubscriptionPaymentTier(anyLong(), any(SubscriptionPaymentTier.PaymentTierCategory.class));
	}

	@Test
	public void calculateSubscriptionPayment_withNextThroughputResetDateEqualsToEndDate_returnsActivePaymentTier() {
		subscriptionConfiguration.setNextThroughputResetDate(endDate);

		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateSubscriptionPayment(subscriptionConfiguration, BigDecimal.valueOf(1000), transactionDate);
		assertNotNull(subscriptionPaymentDTO);
		assertEquals(subscriptionPaymentDTO.getSoftwareFeeAmount(), BigDecimal.valueOf(100));
		assertEquals(subscriptionPaymentDTO.getVorFeeAmount(), BigDecimal.valueOf(50));
		verify(subscriptionPaymentTierDAO, times(2)).findActiveSubscriptionPaymentTier(anyLong(), any(SubscriptionPaymentTier.PaymentTierCategory.class));
	}

	@Test
	public void calculateSubscriptionPayment_withNextThroughputResetDateFarFarAway_returnsActivePaymentTier() {
		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateSubscriptionPayment(subscriptionConfiguration, BigDecimal.valueOf(1000), transactionDate);
		assertNotNull(subscriptionPaymentDTO);
		assertEquals(subscriptionPaymentDTO.getSoftwareFeeAmount(), BigDecimal.valueOf(100));
		assertEquals(subscriptionPaymentDTO.getVorFeeAmount(), BigDecimal.valueOf(50));
		verify(subscriptionPaymentTierDAO, times(2)).findActiveSubscriptionPaymentTier(anyLong(), any(SubscriptionPaymentTier.PaymentTierCategory.class));
	}

	@Test
	public void calculateSubscriptionPayment_withNextThroughputResetDateEqualsToTransactionDate_returnsFirstPaymentTier() {
		Calendar endDate = DateUtilities.newCalendar(2016, Calendar.JANUARY, 1, 0, 0, 0);
		Calendar transactionDate = DateUtilities.newCalendar(2015, Calendar.JANUARY, 1, 0, 0, 0);
		subscriptionConfiguration.setEndDate(endDate);

		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateSubscriptionPayment(subscriptionConfiguration, BigDecimal.valueOf(1000), transactionDate);
		assertNotNull(subscriptionPaymentDTO);
		assertEquals(subscriptionPaymentDTO.getSoftwareFeeAmount(), BigDecimal.TEN);
		assertEquals(subscriptionPaymentDTO.getVorFeeAmount(), BigDecimal.valueOf(5));
		verify(subscriptionPaymentTierDAO, never()).findActiveSubscriptionPaymentTier(anyLong(), any(SubscriptionPaymentTier.PaymentTierCategory.class));
	}


	@Test(expected = IllegalArgumentException.class)
	public void calculateIncrementalSubscriptionPaymentForFutureInvoice_withNullArgs_fails() {
		subscriptionCalculator.calculateIncrementalSubscriptionPaymentForFutureInvoice(null, null, null, null, false, false);
	}

	@Test
	public void calculateIncrementalSubscriptionPaymentForFutureInvoice_SWIncrement_returnsSecondTier() {
		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateIncrementalSubscriptionPaymentForFutureInvoice(companyId, subscriptionPaymentPeriod, BigDecimal.valueOf(1000), subscriptionFeeConfiguration, true, false);
		assertNotNull(subscriptionPaymentDTO);
		assertEquals(subscriptionPaymentDTO.getSoftwareFeeAmount(), BigDecimal.valueOf(90));
		assertEquals(subscriptionPaymentDTO.getVorFeeAmount(), BigDecimal.ZERO);
	}

	@Test
	public void calculateIncrementalSubscriptionPaymentForFutureInvoice_VORIncrement_returnsSecondTier() {
		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateIncrementalSubscriptionPaymentForFutureInvoice(companyId, subscriptionPaymentPeriod, BigDecimal.valueOf(1000), subscriptionFeeConfiguration, false, true);
		assertNotNull(subscriptionPaymentDTO);
		assertEquals(subscriptionPaymentDTO.getVorFeeAmount(), BigDecimal.valueOf(50));
		assertEquals(subscriptionPaymentDTO.getSoftwareFeeAmount(), BigDecimal.ZERO);
	}

	@Test
	public void calculateIncrementalSubscriptionPaymentForFutureInvoice_SWAndVORIncrement_returnsSecondTier() {
		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateIncrementalSubscriptionPaymentForFutureInvoice(companyId, subscriptionPaymentPeriod, BigDecimal.valueOf(1000), subscriptionFeeConfiguration, true, true);
		assertNotNull(subscriptionPaymentDTO);
		assertEquals(subscriptionPaymentDTO.getSoftwareFeeAmount(), BigDecimal.valueOf(90));
		assertEquals(subscriptionPaymentDTO.getVorFeeAmount(), BigDecimal.valueOf(50));
	}

	@Test
	public void calculateIncrementalSubscriptionPaymentForFutureInvoice_SWIncrement_returnsDiffThirdTierMinusFirst() {
		when(registerTransactionDAO.calculateIncrementalSubscriptionTransactionPendingInvoiceForPaymentPeriod(companyId, subscriptionPaymentPeriod.getId(), RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT)).thenReturn(new BigDecimal(90));

		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateIncrementalSubscriptionPaymentForFutureInvoice(companyId, subscriptionPaymentPeriod, BigDecimal.valueOf(1500), subscriptionFeeConfiguration, true, false);
		assertNotNull(subscriptionPaymentDTO);
		assertEquals(BigDecimal.valueOf(900), subscriptionPaymentDTO.getSoftwareFeeAmount());
		assertEquals(BigDecimal.ZERO, subscriptionPaymentDTO.getVorFeeAmount());
	}

		@Test
		public void calculateIncrementalSubscriptionPaymentForFutureInvoice_VORIncrement__returnsDiffThirdTierMinusFirst() {
			when(registerTransactionDAO.calculateIncrementalSubscriptionTransactionPendingInvoiceForPaymentPeriod(companyId, subscriptionPaymentPeriod.getId(), RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT)).thenReturn(new BigDecimal(45));

			// by default we are not setting a subscription vor fee on the invoice, but we need one here for this test
			// so the following code is establishing that set of mock data
			InvoiceLineItem subVorFee = mock(InvoiceLineItem.class);
			when(subVorFee.getType()).thenReturn(InvoiceLineItem.SUBSCRIPTION_VOR_INVOICE_LINE_ITEM);
			when(subVorFee.getAmount()).thenReturn(new BigDecimal(5));
			Set<InvoiceLineItem> invoiceLineItems = Sets.newHashSet(subVorFee);

			SubscriptionInvoice invoice = mock(SubscriptionInvoice.class);
			when(invoice.getInvoiceLineItems()).thenReturn(invoiceLineItems);
			when(subscriptionPaymentPeriod.getSubscriptionInvoice()).thenReturn(invoice);
			when(serviceInvoiceDAO.get(anyLong())).thenReturn(invoice);

			// now we can run our test
			SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateIncrementalSubscriptionPaymentForFutureInvoice(companyId, subscriptionPaymentPeriod, BigDecimal.valueOf(1500), subscriptionFeeConfiguration, false, true);
			assertNotNull(subscriptionPaymentDTO);
			assertEquals(BigDecimal.valueOf(450), subscriptionPaymentDTO.getVorFeeAmount());
			assertEquals(BigDecimal.ZERO, subscriptionPaymentDTO.getSoftwareFeeAmount());
		}

		@Test
		public void calculateIncrementalSubscriptionPaymentForFutureInvoice_SWAndVORIncrement__returnsDiffThirdTierMinusFirst () {
			when(registerTransactionDAO.calculateIncrementalSubscriptionTransactionPendingInvoiceForPaymentPeriod(companyId, subscriptionPaymentPeriod.getId(), RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT)).thenReturn(new BigDecimal(90));
			when(registerTransactionDAO.calculateIncrementalSubscriptionTransactionPendingInvoiceForPaymentPeriod(companyId, subscriptionPaymentPeriod.getId(), RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT)).thenReturn(new BigDecimal(45));

			// mock out invoice line items for both software and vor fees (by default it is only showing invoicing of a
			// software fee)
			InvoiceLineItem subSoftwareFee = mock(InvoiceLineItem.class);
			when(subSoftwareFee.getType()).thenReturn(InvoiceLineItem.SUBSCRIPTION_VOR_SOFTWARE_FEE_INVOICE_LINE_ITEM);
			when(subSoftwareFee.getAmount()).thenReturn(BigDecimal.TEN);

			InvoiceLineItem subVorFee = mock(InvoiceLineItem.class);
			when(subVorFee.getType()).thenReturn(InvoiceLineItem.SUBSCRIPTION_VOR_INVOICE_LINE_ITEM);
			when(subVorFee.getAmount()).thenReturn(new BigDecimal(5));

			Set<InvoiceLineItem> invoiceLineItems = Sets.newHashSet(subSoftwareFee, subVorFee);

			SubscriptionInvoice invoice = mock(SubscriptionInvoice.class);
			when(invoice.getInvoiceLineItems()).thenReturn(invoiceLineItems);
			when(subscriptionPaymentPeriod.getSubscriptionInvoice()).thenReturn(invoice);
			when(serviceInvoiceDAO.get(anyLong())).thenReturn(invoice);

			// now we can run the test
			SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateIncrementalSubscriptionPaymentForFutureInvoice(companyId, subscriptionPaymentPeriod, BigDecimal.valueOf(1500), subscriptionFeeConfiguration, true, true);
			assertNotNull(subscriptionPaymentDTO);
			assertEquals(BigDecimal.valueOf(900), subscriptionPaymentDTO.getSoftwareFeeAmount());
			assertEquals(BigDecimal.valueOf(450), subscriptionPaymentDTO.getVorFeeAmount());
		}


	@Test
	public void calculateIncrementalSubscriptionPaymentForFutureInvoice_WithNextResetDayOnTxDate_returnsSecondTier() {
		Calendar transactionDate = DateUtilities.newCalendar(2015, Calendar.JANUARY, 1, 0, 0, 0);
		when(dateRange.getFrom()).thenReturn(transactionDate);

		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateIncrementalSubscriptionPaymentForFutureInvoice(companyId, subscriptionPaymentPeriod, BigDecimal.valueOf(1000), subscriptionFeeConfiguration, true, true);
		assertNotNull(subscriptionPaymentDTO);
		assertEquals(subscriptionPaymentDTO.getSoftwareFeeAmount(), BigDecimal.ZERO);
		assertEquals(subscriptionPaymentDTO.getVorFeeAmount(), BigDecimal.valueOf(5));
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateIncrementalSubscriptionPayment_withNullArguments_fails() {
		subscriptionCalculator.calculateIncrementalSubscriptionPayment(null, null, null, false, false);
	}

	@Test
	public void calculateIncrementalSubscriptionPayment_withSWIncrementBothTiersAreTheSameAmount_success() {
		firstTier.setPaymentAmount(BigDecimal.valueOf(1000));
		secondTier.setPaymentAmount(BigDecimal.valueOf(1000));
		thirdTier.setPaymentAmount(BigDecimal.valueOf(1000));
		Calendar transactionDate = DateUtilities.newCalendar(2015, Calendar.MARCH, 13, 0, 0, 0);
		Calendar nextPaymentDate = DateUtilities.newCalendar(2015, Calendar.APRIL, 1, 0, 0, 0);
		DateRange currentDateRange = new DateRange(DateUtilities.newCalendar(2015, Calendar.MARCH, 1, 0, 0, 0), DateUtilities.newCalendar(2015, Calendar.MARCH, 31, 0, 0, 0));
		SubscriptionPaymentPeriod nextSubscriptionPaymentPeriod = mock(SubscriptionPaymentPeriod.class);
		when(nextSubscriptionPaymentPeriod.getPeriodDateRange()).thenReturn(new DateRange(nextPaymentDate));
		when(subscriptionPaymentPeriod.getPeriodDateRange()).thenReturn(currentDateRange);

		when(subscriptionPaymentPeriodDAO.findBySubscriptionConfigurationIdAndDateInRange(anyLong(), any(Calendar.class))).thenReturn(subscriptionPaymentPeriod);
		when(subscriptionPaymentPeriodDAO.findNextFromDateBySubscriptionConfigurationId(anyLong(), any(Calendar.class))).thenReturn(nextSubscriptionPaymentPeriod);
		when(subscriptionPaymentTierDAO.findActiveSubscriptionPaymentTier(anyLong(), any(SubscriptionPaymentTier.PaymentTierCategory.class))).thenReturn(firstTier);

		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateIncrementalSubscriptionPayment(subscriptionConfiguration, transactionDate, secondTier, true, false);
		assertEquals(subscriptionPaymentDTO.getSoftwareFeeAmount().intValue(), 0);
		assertEquals(subscriptionPaymentDTO.getVorFeeAmount().intValue(), 0);
	}

	@Test
	public void findSubscriptionFeeConfigurationEffectiveOnDate_withFutureDateIn2015_success() {
		Set<SubscriptionFeeConfiguration> feeConfigurationSet = Sets.newHashSet();
		feeConfigurationSet.add(feeConfiguration2);
		feeConfigurationSet.add(feeConfiguration3);
		feeConfigurationSet.add(feeConfiguration1);
		List<SubscriptionFeeConfiguration> orderedConfigurations = CollectionUtilities.asSortedList(feeConfigurationSet);
		SubscriptionFeeConfiguration feeConfiguration = subscriptionCalculator.findSubscriptionFeeConfigurationEffectiveOnDate(orderedConfigurations, DateUtilities.newCalendar(2015, Calendar.MARCH, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")));
		assertNotNull(feeConfiguration);
		assertEquals(feeConfiguration, feeConfiguration3);
	}

	@Test
	public void findSubscriptionFeeConfigurationEffectiveOnDate_withFutureDateInLate2014_success() {
		Set<SubscriptionFeeConfiguration> feeConfigurationSet = Sets.newHashSet();
		feeConfigurationSet.add(feeConfiguration2);
		feeConfigurationSet.add(feeConfiguration3);
		feeConfigurationSet.add(feeConfiguration1);
		List<SubscriptionFeeConfiguration> orderedConfigurations = CollectionUtilities.asSortedList(feeConfigurationSet);
		SubscriptionFeeConfiguration feeConfiguration = subscriptionCalculator.findSubscriptionFeeConfigurationEffectiveOnDate(orderedConfigurations, DateUtilities.newCalendar(2014, Calendar.DECEMBER, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")));
		assertNotNull(feeConfiguration);
		assertEquals(feeConfiguration, feeConfiguration2);
	}

	@Test
	public void findSubscriptionFeeConfigurationEffectiveOnDate_withExactDateMatch_success() {
		Set<SubscriptionFeeConfiguration> feeConfigurationSet = Sets.newHashSet();
		feeConfigurationSet.add(feeConfiguration2);
		feeConfigurationSet.add(feeConfiguration3);
		feeConfigurationSet.add(feeConfiguration1);
		List<SubscriptionFeeConfiguration> orderedConfigurations = CollectionUtilities.asSortedList(feeConfigurationSet);
		SubscriptionFeeConfiguration feeConfiguration = subscriptionCalculator.findSubscriptionFeeConfigurationEffectiveOnDate(orderedConfigurations, DateUtilities.newCalendar(2014, Calendar.NOVEMBER, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")));
		assertNotNull(feeConfiguration);
		assertEquals(feeConfiguration, feeConfiguration2);
	}

	@Test
	public void findSubscriptionFeeConfigurationEffectiveOnDate_withInBetweenDates_success() {
		Set<SubscriptionFeeConfiguration> feeConfigurationSet = Sets.newHashSet();
		feeConfigurationSet.add(feeConfiguration2);
		feeConfigurationSet.add(feeConfiguration3);
		feeConfigurationSet.add(feeConfiguration1);
		List<SubscriptionFeeConfiguration> orderedConfigurations = CollectionUtilities.asSortedList(feeConfigurationSet);
		SubscriptionFeeConfiguration feeConfiguration = subscriptionCalculator.findSubscriptionFeeConfigurationEffectiveOnDate(orderedConfigurations, DateUtilities.newCalendar(2014, Calendar.DECEMBER, 15, 0, 0, 0, TimeZone.getTimeZone("UTC")));
		assertNotNull(feeConfiguration);
		assertEquals(feeConfiguration, feeConfiguration2);
	}

	@Test
	public void getCurrentSubscriptionDetails_ifNoActiveSubscription_returnEmptyMap() {
		when(subscriptionService.findActiveSubscriptionConfigurationByCompanyId(companyId)).thenReturn(null);

		assertTrue(subscriptionCalculator.getCurrentSubscriptionDetails(companyId).isEmpty());
	}

	@Test
	public void getCurrentSubscriptionDetails_ifNoActiveSubscription_returnConfigMap() {
		Map<String, Object> configMap = subscriptionCalculator.getCurrentSubscriptionDetails(companyId);
		assertEquals(subscriptionConfiguration.getId(), configMap.get("subscriptionConfigurationId"));
		assertEquals(subscriptionFeeConfiguration.getId(), configMap.get("subscriptionFeeConfigurationId"));
		assertEquals(subscriptionFeeConfiguration.getEffectiveDate().getTime(), configMap.get("effectiveDate"));
		assertEquals(subscriptionConfiguration.getEndDate().getTime(), configMap.get("endDate"));
		assertEquals(firstTier.getId(), configMap.get("currentTierId"));
		assertEquals(firstTier.getId(), configMap.get("currentTierVORId"));
		assertEquals(firstTier.getPaymentAmount(), configMap.get("currentTierAmount"));
		assertEquals(firstTier.getVendorOfRecordAmount(), configMap.get("currentTierVORAmount"));
	}

}
