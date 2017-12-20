package com.workmarket.service.business.account;

import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.payments.dao.PaymentConfigurationDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.service.business.CompanyService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author: rocio
 */
@Service
public class PaymentConfigurationServiceImpl implements PaymentConfigurationService {

	private static final Log logger = LogFactory.getLog(PaymentConfigurationServiceImpl.class);
	@Autowired private SubscriptionService subscriptionService;
	@Autowired private CompanyService companyService;
	@Autowired private PaymentConfigurationDAO paymentConfigurationDAO;

	@Override
	public List<Company> findAllCompaniesWithSubscriptionAccountPricingType() {
		return paymentConfigurationDAO.findAllCompaniesByAccountPricingType(new AccountPricingType(AccountPricingType.SUBSCRIPTION_PRICING_TYPE));
	}

	@Override
	public List<Company> findAllCompaniesWithTransactionalAccountPricingType() {
		return paymentConfigurationDAO.findAllCompaniesByAccountPricingType(new AccountPricingType(AccountPricingType.TRANSACTIONAL_PRICING_TYPE));
	}

	@Override
	public void savePaymentConfiguration(PaymentConfiguration paymentConfiguration) {
		paymentConfigurationDAO.saveOrUpdate(paymentConfiguration);
	}
}
