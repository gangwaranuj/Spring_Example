package com.workmarket.service.business.scheduler.subscription;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfigurationPagination;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.domains.payments.service.AccountRegisterServicePrefundImpl;
import com.workmarket.service.business.account.PaymentConfigurationService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionMonthlyReconciliationTest {

	@Mock PaymentConfigurationService paymentConfigurationService;
	@Mock SubscriptionService subscriptionService;
	@Mock AuthenticationService authenticationService;
	@Mock CompanyService companyService;
	@Mock AccountRegisterServicePrefundImpl accountRegisterService;
	@Mock UserNotificationService userNotificationService;
	@InjectMocks SubscriptionMonthlyReconciliation subscriptionMonthlyReconciliation;

	private SubscriptionConfiguration subscriptionConfiguration;
	private SubscriptionConfigurationPagination pagination;
	private Company company;
	private PaymentConfiguration paymentConfiguration;

	@Before
	public void setUp() throws Exception {
		subscriptionConfiguration = mock(SubscriptionConfiguration.class);
		pagination = mock(SubscriptionConfigurationPagination.class);
		company = mock(Company.class);
		paymentConfiguration = mock(PaymentConfiguration.class);

		when(pagination.getResults()).thenReturn(Lists.newArrayList(subscriptionConfiguration));
		when(subscriptionService.findAllActiveSubscriptionConfigurations()).thenReturn(pagination);
		when(subscriptionConfiguration.getCompany()).thenReturn(mock(Company.class));
		when(companyService.findCompanyById(anyLong())).thenReturn(company);
		when(company.getPaymentConfiguration()).thenReturn(paymentConfiguration);
		when(paymentConfiguration.isSubscriptionPricing()).thenReturn(false);
		when(paymentConfiguration.getAccountPricingType()).thenReturn(new AccountPricingType(AccountPricingType.TRANSACTIONAL_PRICING_TYPE));
	}


	@Test
	public void verifyPaymentConfiguration_simpleCase_success() {
		subscriptionMonthlyReconciliation.verifyPaymentConfiguration();
		verify(subscriptionService, times(1)).findAllActiveSubscriptionConfigurations();
		verify(paymentConfigurationService, times(1)).findAllCompaniesWithTransactionalAccountPricingType();

	}

	@Test
	public void verifyPaymentConfiguration_subscriptionWithTransactionalPaymentConf_sendsEmail() {
		Calendar effectiveDate = Calendar.getInstance();
		effectiveDate.add(Calendar.MONTH, -1);
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		subscriptionMonthlyReconciliation.verifyPaymentConfiguration();
		verify(companyService, times(1)).findCompanyById(anyLong());
		verify(paymentConfigurationService, times(1)).findAllCompaniesWithTransactionalAccountPricingType();
		verify(userNotificationService, times(1)).onPaymentConfigurationReconciliationDifference(anySet());

	}

	@Test
	public void verifyPaymentConfiguration_subscriptionWithTransactionalPaymentConf2ndVerification_sendsEmail() {
		List<Company> transactionalCompanies = Lists.newArrayList(company);
		when(paymentConfigurationService.findAllCompaniesWithTransactionalAccountPricingType()).thenReturn(transactionalCompanies);

		Calendar effectiveDate = Calendar.getInstance();
		effectiveDate.add(Calendar.MONTH, -1);
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		when(subscriptionService.findActiveSubscriptionConfigurationByCompanyId(anyLong())).thenReturn(subscriptionConfiguration);

		subscriptionMonthlyReconciliation.verifyPaymentConfiguration();

		verify(companyService, times(1)).findCompanyById(anyLong());
		verify(paymentConfigurationService, times(1)).findAllCompaniesWithTransactionalAccountPricingType();
		verify(userNotificationService, times(1)).onPaymentConfigurationReconciliationDifference(anySet());

	}

	@Test
	public void verifyPaymentConfiguration_subscriptionWithSubPaymentConf_dontSendEmail() {
		Calendar effectiveDate = Calendar.getInstance();
		effectiveDate.add(Calendar.MONTH, -1);
		when(paymentConfiguration.isSubscriptionPricing()).thenReturn(true);
		when(paymentConfiguration.getAccountPricingType()).thenReturn(new AccountPricingType(AccountPricingType.SUBSCRIPTION_PRICING_TYPE));

		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		subscriptionMonthlyReconciliation.verifyPaymentConfiguration();
		verify(companyService, times(1)).findCompanyById(anyLong());
		verify(paymentConfigurationService, times(1)).findAllCompaniesWithTransactionalAccountPricingType();
		verify(userNotificationService, never()).onPaymentConfigurationReconciliationDifference(anySet());

	}

	@Test
	public void verifyPaymentConfiguration_transactionalWithTransactionalPaymentConf2ndVerification_dontSendEmail() {
		List<Company> transactionalCompanies = Lists.newArrayList(company);
		when(paymentConfigurationService.findAllCompaniesWithTransactionalAccountPricingType()).thenReturn(transactionalCompanies);

		Calendar effectiveDate = Calendar.getInstance();
		effectiveDate.add(Calendar.MONTH, 1);
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		when(subscriptionService.findActiveSubscriptionConfigurationByCompanyId(anyLong())).thenReturn(subscriptionConfiguration);

		subscriptionMonthlyReconciliation.verifyPaymentConfiguration();

		verify(subscriptionService, times(1)).findActiveSubscriptionConfigurationByCompanyId(anyLong());
		verify(paymentConfigurationService, times(1)).findAllCompaniesWithTransactionalAccountPricingType();
		verify(userNotificationService, never()).onPaymentConfigurationReconciliationDifference(anySet());
	}

	@Test
	public void verifyNextThroughputResetDate_simpleCase_success() {
		subscriptionMonthlyReconciliation.verifyNextThroughputResetDate();
		verify(subscriptionService, times(1)).findAllActiveSubscriptionConfigurations();
	}

	@Test
	public void verifyNextThroughputResetDate_NextThroughputResetDate_sendsEmail() {
		Calendar effectiveDate = Calendar.getInstance();
		effectiveDate.add(Calendar.MONTH, -1);
		when(subscriptionConfiguration.getEffectiveDate()).thenReturn(effectiveDate);
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.YEAR, 2);
		when(subscriptionConfiguration.getEndDate()).thenReturn(endDate);
		when(subscriptionConfiguration.getNextThroughputResetDate()).thenReturn(null);

		subscriptionMonthlyReconciliation.verifyNextThroughputResetDate();
		verify(userNotificationService, times(1)).onNextThroughputResetDateDifference(anySet());
	}
}