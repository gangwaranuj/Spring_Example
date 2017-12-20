package com.workmarket.domains.payments.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.pricing.AccountPricingType;

import java.util.Calendar;
import java.util.List;

public interface PaymentConfigurationDAO extends DAOInterface<PaymentConfiguration> {

	List<Company> findAllCompanyAccountsByNextStatementDate(Calendar asOfDate);

	List<Company> findAllCompaniesByAccountPricingType(AccountPricingType accountPricingType);

}
