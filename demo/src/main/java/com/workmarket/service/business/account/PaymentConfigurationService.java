package com.workmarket.service.business.account;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;

import java.util.List;

/**
 * Author: rocio
 */
public interface PaymentConfigurationService {

	List<Company> findAllCompaniesWithSubscriptionAccountPricingType();

	List<Company> findAllCompaniesWithTransactionalAccountPricingType();

	void savePaymentConfiguration(PaymentConfiguration paymentConfiguration);
}
