package com.workmarket.service.business.account;

import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.ServiceTransaction;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.CompanyAccountPricingTypeChange;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.service.exception.account.InvalidSubscriptionConfigurationException;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface AccountPricingService {

	/**
	 * Updates the AccountServiceType per country for a given company, only if the company has a transactional Pricing Type
	 * @param companyId
	 * @param accountServiceTypeUpdates
	 */
	void updateCompanyAccountServiceType(long companyId, List<AccountServiceTypeDTO> accountServiceTypeUpdates);

	/**
	 * Sets the correct payment tier for a subscription configuration, given a register transaction that reached a new level of throughput band.
	 * @param registerTransaction
	 * @throws InvalidSubscriptionConfigurationException
	 */
	void adjustSubscriptionPaymentConfigurationByThroughputAmount(WorkResourceTransaction registerTransaction) throws InvalidSubscriptionConfigurationException;

	/**
	 * Finds all the Account Service Types available.
	 * @return
	 */
	List<AccountServiceType> findAllAccountServiceType();

	/**
	 * Returns the account service type configuration for a given assignment
	 * @param work
	 * @return {@link com.workmarket.domains.model.account.pricing AccountServiceType}
	 */
	AccountServiceType findAccountServiceTypeConfiguration(AbstractWork work);

	/**
	 * Returns the account service type configuration for a given company and a country.
	 * @param company
	 * @param countryId
	 * @return {@link com.workmarket.domains.model.account.pricing AccountServiceType}
	 */
	AccountServiceType findAccountServiceTypeConfiguration(Company company, String countryId);

	List<CompanyAccountPricingTypeChange> getCompanyAccountPricingTypeChangeScheduledBeforeDate(Calendar updateDate, Boolean executed);

	void saveServiceTransactionImmediateRevenueEffectiveDate(ServiceTransaction transaction);

	void saveServiceTransactionDeferredRevenueEffectiveDates(ServiceTransaction transaction);

	Map<Calendar, BigDecimal> calculateSubscriptionRevenueEffectiveDates(SubscriptionPaymentPeriod paymentPeriod, BigDecimal totalAmountPerPeriod);

	void updatePaymentConfigurationAccountServiceType(PaymentConfiguration paymentConfiguration, AccountServiceType accountServiceType);

	void updatePaymentConfigurationAccountServiceType(PaymentConfiguration paymentConfiguration, String countryId, AccountServiceType accountServiceType);
	
}
