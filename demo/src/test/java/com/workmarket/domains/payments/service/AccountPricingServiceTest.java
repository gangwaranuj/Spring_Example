package com.workmarket.domains.payments.service;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.util.Assert;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.account.ServiceTransactionRevenueDAO;
import com.workmarket.dao.account.pricing.CompanyAccountPricingTypeChangeDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionConfigurationDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionThroughputIncrementTransactionDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.ServiceTransaction;
import com.workmarket.domains.model.account.ServiceTransactionRevenue;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionFeeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTierStatusType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionThroughputIncrementTransaction;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.account.AccountPricingServiceImpl;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.exception.account.InvalidSubscriptionConfigurationException;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountPricingServiceTest {

	@Mock CompanyAccountPricingTypeChangeDAO companyAccountPricingTypeChangeDAO;
	@Mock SubscriptionConfigurationDAO subscriptionConfigurationDAO;
	@Mock SubscriptionThroughputIncrementTransactionDAO subscriptionThroughputIncrementTxDAO;
	@Mock CompanyDAO companyDAO;
	@Mock LookupEntityDAO lookupEntityDAO;
	@Mock ServiceTransactionRevenueDAO serviceTransactionRevenueDAO;
	@Mock TaxService taxService;

	@InjectMocks AccountPricingServiceImpl accountPricingService;

	private ServiceTransaction serviceTransaction;
	private Company company;
	private PaymentConfiguration paymentConfiguration;
	private AccountServiceTypeDTO accountServiceTypeDTO;
	private AccountServiceType vorAccountServiceType;
	private WorkResourceTransaction workResourceTransaction;
	private AccountRegisterSummaryFields accountRegisterSummaryFields;
	private Work work;
	private AccountPricingType workResourceTransactionAccountPricingType;
	private AccountPricingType companyAccountPricingType;
	private SubscriptionConfiguration subscriptionConfiguration;
	private SubscriptionFeeConfiguration subscriptionFeeConfiguration;
	private SubscriptionPaymentTier subscriptionPaymentTier;
	private SubscriptionPaymentTierStatusType subscriptionPaymentTierStatusType;

	private Company testCompany;

	@Before
	public void setUp() throws Exception {
		serviceTransaction = new ServiceTransaction();
		serviceTransaction.setAmount(BigDecimal.valueOf(100));

		company = mock(Company.class);
		paymentConfiguration = mock(PaymentConfiguration.class);
		vorAccountServiceType = mock(AccountServiceType.class);

		when(companyDAO.findById(1000L)).thenReturn(company);
		when(company.getPaymentConfiguration()).thenReturn(paymentConfiguration);
		when(paymentConfiguration.isTransactionalPricing()).thenReturn(true);
		accountServiceTypeDTO = new AccountServiceTypeDTO();
		accountServiceTypeDTO.setAccountServiceTypeCode(AccountServiceType.VENDOR_OF_RECORD);
		accountServiceTypeDTO.setCountryCode(Country.USA);
		when(vorAccountServiceType.isVendorOfRecord()).thenReturn(true);
		when(vorAccountServiceType.getCode()).thenReturn(AccountServiceType.VENDOR_OF_RECORD);

		when(lookupEntityDAO.findByCode(AccountServiceType.class, AccountServiceType.VENDOR_OF_RECORD)).thenReturn(vorAccountServiceType);

		workResourceTransaction = mock(WorkResourceTransaction.class);
		accountRegisterSummaryFields = mock(AccountRegisterSummaryFields.class);
		work = mock(Work.class);
		companyAccountPricingType = mock(AccountPricingType.class);
		workResourceTransactionAccountPricingType = mock(AccountPricingType.class);
		subscriptionConfiguration = mock(SubscriptionConfiguration.class);
		subscriptionFeeConfiguration = mock(SubscriptionFeeConfiguration.class);
		subscriptionPaymentTier = mock(SubscriptionPaymentTier.class);
		subscriptionPaymentTierStatusType = mock(SubscriptionPaymentTierStatusType.class);

		when(workResourceTransaction.getWork()).thenReturn(work);
		when(work.getCompany()).thenReturn(company);
		when(workResourceTransactionAccountPricingType.isSubscriptionPricing()).thenReturn(true);
		when(companyAccountPricingType.isSubscriptionPricing()).thenReturn(true);
		when(company.getAccountPricingType()).thenReturn(companyAccountPricingType);
		when(subscriptionConfigurationDAO.findActiveSubscriptionConfigurationByCompanyId(anyLong())).thenReturn(subscriptionConfiguration);
		when(workResourceTransaction.getAccountPricingType()).thenReturn(workResourceTransactionAccountPricingType);
		when(subscriptionConfiguration.getActiveSubscriptionFeeConfiguration()).thenReturn(subscriptionFeeConfiguration);
		when(subscriptionConfiguration.isVendorOfRecord()).thenReturn(false);
		when(workResourceTransaction.getAccountRegisterSummaryFields()).thenReturn(accountRegisterSummaryFields);
		when(subscriptionFeeConfiguration.findSubscriptionPaymentTierForThroughputAmount(any(BigDecimal.class))).thenReturn(subscriptionPaymentTier);
		when(subscriptionPaymentTierStatusType.isNotReached()).thenReturn(true);
		when(subscriptionPaymentTier.getSubscriptionPaymentTierSoftwareStatusType()).thenReturn(subscriptionPaymentTierStatusType);

		PaymentConfiguration paymentConfigurationTest = mock(PaymentConfiguration.class);
		final AccountServiceType accountServiceTypeTax = mock(AccountServiceType.class);
		when(accountServiceTypeTax.getCode()).thenReturn(AccountServiceType.TAX_SERVICE_1099);
		final AccountServiceType accountServiceTypeVOR = mock(AccountServiceType.class);
		when(accountServiceTypeVOR.getCode()).thenReturn(AccountServiceType.VENDOR_OF_RECORD);
		final AccountServiceType accountServiceTypeNone = mock(AccountServiceType.class);
		when(accountServiceTypeNone.getCode()).thenReturn(AccountServiceType.NONE);

		when(paymentConfigurationTest.findAccountServiceTypeForCountry(anyString())).thenAnswer(
			new Answer<AccountServiceType>(){
				@Override
				public AccountServiceType answer(InvocationOnMock invocation){
					if (Country.USA.equals(invocation.getArguments()[0])){
						return accountServiceTypeTax;
					}
					else if(Country.CANADA.equals(invocation.getArguments()[0])){
						return accountServiceTypeVOR;
					}
					else {
						return accountServiceTypeNone;
					}
				}});

		testCompany = mock(Company.class);
		when(testCompany.getPaymentConfiguration()).thenReturn(paymentConfigurationTest);
		when(taxService.getCountryIdFromTaxEntityCountry(anyString())).thenAnswer(
			new Answer<String>(){
				@Override
				public String answer(InvocationOnMock invocation){
					if (AbstractTaxEntity.COUNTRY_USA.equals(invocation.getArguments()[0])){
						return Country.USA;
					}
					else if(AbstractTaxEntity.COUNTRY_CANADA.equals(invocation.getArguments()[0])){
						return Country.CANADA;
					}
					return null;
				}});

	}

	@Test
	public void saveServiceTransactionDeferredRevenueEffectiveDates_withSubscriptionTransaction_savesRevenueTransaction() {
		accountPricingService.saveServiceTransactionDeferredRevenueEffectiveDates(serviceTransaction);
		verify(serviceTransactionRevenueDAO, times(1)).saveOrUpdate(any(ServiceTransactionRevenue.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateCompanyAccountServiceType_withNonExistentCompany() {
		accountPricingService.updateCompanyAccountServiceType(1L, Lists.newArrayList(new AccountServiceTypeDTO()));
	}

	@Test
	public void updateCompanyAccountServiceType_success() {
		accountPricingService.updateCompanyAccountServiceType(1000L, Lists.newArrayList(accountServiceTypeDTO));
		verify(companyDAO, times(1)).findById(anyLong());
		verify(lookupEntityDAO, times(1)).findByCode(AccountServiceType.class, AccountServiceType.VENDOR_OF_RECORD);
	}

	@Test
	public void calculateSubscriptionRevenueEffectiveDates_withFractionalAmountAndSemiannual_success() {
		DateRange dateRange = mock(DateRange.class);
		Calendar from = DateUtilities.newCalendar(2014, Calendar.JANUARY, 1,1, 0,0);
		SubscriptionPaymentPeriod subscriptionPaymentPeriod = mock(SubscriptionPaymentPeriod.class);
		SubscriptionConfiguration subscriptionConfiguration = mock(SubscriptionConfiguration.class);
		when(subscriptionConfiguration.getSubscriptionPeriod()).thenReturn(SubscriptionPeriod.SEMIANNUAL);
		when(subscriptionPaymentPeriod.getSubscriptionConfiguration()).thenReturn(subscriptionConfiguration);
		when(dateRange.getFrom()).thenReturn(from);
		when(subscriptionPaymentPeriod.getPeriodDateRange()).thenReturn(dateRange);

		Map<Calendar, BigDecimal> amounts = accountPricingService.calculateSubscriptionRevenueEffectiveDates(subscriptionPaymentPeriod, BigDecimal.valueOf(2500));
		Assert.isTrue(amounts.entrySet().size() == 6);
		int count = 0;
		for (Map.Entry<Calendar, BigDecimal> calendarBigDecimalEntry : amounts.entrySet()) {
			count++;
			if (count == amounts.entrySet().size()) {
				Assert.equals(calendarBigDecimalEntry.getValue().doubleValue(), 416.7);
			} else {
				Assert.equals(calendarBigDecimalEntry.getValue().doubleValue(), 416.66);
			}

		}
	}

	@Test
	public void calculateSubscriptionRevenueEffectiveDates_withFractionalAmountAndQuarterly_success() {
		DateRange dateRange = mock(DateRange.class);
		Calendar from = DateUtilities.newCalendar(2014, Calendar.JANUARY, 1,1, 0,0);
		SubscriptionPaymentPeriod subscriptionPaymentPeriod = mock(SubscriptionPaymentPeriod.class);
		SubscriptionConfiguration subscriptionConfiguration = mock(SubscriptionConfiguration.class);
		when(subscriptionConfiguration.getSubscriptionPeriod()).thenReturn(SubscriptionPeriod.QUARTERLY);
		when(subscriptionPaymentPeriod.getSubscriptionConfiguration()).thenReturn(subscriptionConfiguration);
		when(dateRange.getFrom()).thenReturn(from);
		when(subscriptionPaymentPeriod.getPeriodDateRange()).thenReturn(dateRange);

		Map<Calendar, BigDecimal> amounts = accountPricingService.calculateSubscriptionRevenueEffectiveDates(subscriptionPaymentPeriod, BigDecimal.valueOf(100));
		Assert.isTrue(amounts.entrySet().size() == 3);
		int count = 0;
		for (Map.Entry<Calendar, BigDecimal> calendarBigDecimalEntry : amounts.entrySet()) {
			count++;
			if (count == amounts.entrySet().size()) {
				Assert.equals(calendarBigDecimalEntry.getValue().doubleValue(), 33.34);
			} else {
				Assert.equals(calendarBigDecimalEntry.getValue().doubleValue(), 33.33);
			}

		}
	}

	@Test
	public void findAllAccountServiceType_success() {
		accountPricingService.findAllAccountServiceType();
		verify(lookupEntityDAO, times(1)).findLookupEntities(eq(AccountServiceType.class));
	}

	@Test
	public void findAccountServiceTypeConfiguration_withNullCompany_returnsNONE() {
		assertEquals(accountPricingService.findAccountServiceTypeConfiguration(null, AbstractTaxEntity.COUNTRY_USA), new AccountServiceType(AccountServiceType.NONE));
	}

	@Test
	public void findAccountServiceTypeConfiguration_withNullCountryAndCountry_returnsNONE() {
		assertEquals(accountPricingService.findAccountServiceTypeConfiguration(null, AbstractTaxEntity.COUNTRY_USA), new AccountServiceType(AccountServiceType.NONE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void adjustSubscriptionPaymentConfigurationByThroughputAmount_withNullArguments_fail() {
		accountPricingService.adjustSubscriptionPaymentConfigurationByThroughputAmount(null);
	}

	@Test(expected = IllegalStateException.class)
	public void adjustSubscriptionPaymentConfigurationByThroughputAmount_withNullWork_fail() {
		when(workResourceTransaction.getWork()).thenReturn(null);
		accountPricingService.adjustSubscriptionPaymentConfigurationByThroughputAmount(workResourceTransaction);
	}

	@Test
	public void adjustSubscriptionPaymentConfigurationByThroughputAmount_withTransactionalAccount_doNoting() {
		when(companyAccountPricingType.isSubscriptionPricing()).thenReturn(false);
		accountPricingService.adjustSubscriptionPaymentConfigurationByThroughputAmount(workResourceTransaction);
		verify(subscriptionConfigurationDAO, never()).findActiveSubscriptionConfigurationByCompanyId(anyLong());
	}

	@Test(expected = InvalidSubscriptionConfigurationException.class)
	public void adjustSubscriptionPaymentConfigurationByThroughputAmount_withMissingActivePaymentConfig_fail() {
		when(subscriptionConfiguration.getActiveSubscriptionFeeConfiguration()).thenReturn(null);
		accountPricingService.adjustSubscriptionPaymentConfigurationByThroughputAmount(workResourceTransaction);
	}

	@Test(expected = InvalidSubscriptionConfigurationException.class)
	public void adjustSubscriptionPaymentConfigurationByThroughputAmount_withNoSubscriptionPaymentTier_fail() {
		when(subscriptionFeeConfiguration.findSubscriptionPaymentTierForThroughputAmount(any(BigDecimal.class))).thenReturn(null);
		accountPricingService.adjustSubscriptionPaymentConfigurationByThroughputAmount(workResourceTransaction);
		verify(subscriptionConfigurationDAO, times(1)).findActiveSubscriptionConfigurationByCompanyId(anyLong());
	}

	@Test
	public void adjustSubscriptionPaymentConfigurationByThroughputAmount_success() {
		accountPricingService.adjustSubscriptionPaymentConfigurationByThroughputAmount(workResourceTransaction);
		verify(subscriptionConfigurationDAO, times(1)).findActiveSubscriptionConfigurationByCompanyId(anyLong());
		verify(subscriptionPaymentTier, times(1)).setSubscriptionPaymentTierSoftwareStatusType(eq(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.PROCESSING)));
		verify(subscriptionPaymentTier, never()).setSubscriptionPaymentTierVorStatusType(eq(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.PROCESSING)));
		verify(subscriptionThroughputIncrementTxDAO, times(1)).saveOrUpdate(any(SubscriptionThroughputIncrementTransaction.class));
	}

	@Test
	public void adjustSubscriptionPaymentConfigurationByThroughputAmount_withVendorOfRecord_success() {
		when(subscriptionConfiguration.isVendorOfRecord()).thenReturn(true);
		accountPricingService.adjustSubscriptionPaymentConfigurationByThroughputAmount(workResourceTransaction);
		verify(subscriptionConfigurationDAO, times(1)).findActiveSubscriptionConfigurationByCompanyId(anyLong());
		verify(subscriptionPaymentTier, times(1)).setSubscriptionPaymentTierSoftwareStatusType(eq(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.PROCESSING)));
		verify(subscriptionPaymentTier, times(1)).setSubscriptionPaymentTierVorStatusType(eq(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.PROCESSING)));
		verify(subscriptionThroughputIncrementTxDAO, times(1)).saveOrUpdate(any(SubscriptionThroughputIncrementTransaction.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void updatePaymentConfigurationAccountServiceType_withNullArguments_fail() {
		accountPricingService.updatePaymentConfigurationAccountServiceType(null, null);
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_US() {

		AccountServiceType accountServiceType = accountPricingService.findAccountServiceTypeConfiguration(testCompany, Country.USA);
		assertEquals(AccountServiceType.TAX_SERVICE_1099, accountServiceType.getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_CANADA() {

		AccountServiceType accountServiceType = accountPricingService.findAccountServiceTypeConfiguration(testCompany, Country.CANADA);
		assertEquals(AccountServiceType.VENDOR_OF_RECORD, accountServiceType.getCode());
	}
}
