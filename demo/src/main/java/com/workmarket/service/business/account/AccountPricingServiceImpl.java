package com.workmarket.service.business.account;

import com.google.common.collect.Maps;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.account.ServiceTransactionRevenueDAO;
import com.workmarket.dao.account.pricing.CompanyAccountPricingTypeChangeDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionConfigurationDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionThroughputIncrementTransactionDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.ServiceTransaction;
import com.workmarket.domains.model.account.ServiceTransactionRevenue;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.AccountServiceTypeConfiguration;
import com.workmarket.domains.model.account.pricing.CompanyAccountPricingTypeChange;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionFeeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTierStatusType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriodType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionThroughputIncrementTransaction;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.exception.account.InvalidSubscriptionConfigurationException;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Service
public class AccountPricingServiceImpl implements AccountPricingService {

	@Autowired private CompanyAccountPricingTypeChangeDAO companyAccountPricingTypeChangeDAO;
	@Autowired private SubscriptionConfigurationDAO subscriptionConfigurationDAO;
	@Autowired private SubscriptionThroughputIncrementTransactionDAO subscriptionThroughputIncrementTxDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private LookupEntityDAO lookupEntityDAO;
	@Autowired private ServiceTransactionRevenueDAO serviceTransactionRevenueDAO;
	@Autowired private TaxService taxService;
	@Autowired private WorkService workService;
	@Autowired private AddressService addressService;

	private static final Log logger = LogFactory.getLog(AccountPricingServiceImpl.class);

	@Override
	public void adjustSubscriptionPaymentConfigurationByThroughputAmount(WorkResourceTransaction registerTransaction) throws InvalidSubscriptionConfigurationException {
		Assert.notNull(registerTransaction);
		Assert.notNull(registerTransaction.getAccountRegisterSummaryFields());
		/**
		 * If company has subscription pricing type and the assignment was created within the subscription lifecycle,
		 * we need to check for the payment tiers and see if this particular transaction
		 * reached the next upper level. If that's the case we need to charge the company the pro-rated amount.
		 *
		 */
		Work work = registerTransaction.getWork();
		if (work == null) {
			throw new java.lang.IllegalStateException("[subscription] Invalid RegisterTransaction ");
		}

		if (registerTransaction.getAccountPricingType().isSubscriptionPricing() && work.getCompany().getAccountPricingType().isSubscriptionPricing()) {
			/**
			 * Possible combinations
			 * SW Throughput 		VOR Throughput
			 * 	true					false
			 *  true					true
			 *  false					true
			 */
			SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.findActiveSubscriptionConfigurationByCompanyId(work.getCompany().getId());
			if (subscriptionConfiguration == null) {
				//This is possible if the subscription is cancelled and there were assignments created while the subscription was active.
				return;
			}
			SubscriptionFeeConfiguration feeConfiguration = subscriptionConfiguration.getActiveSubscriptionFeeConfiguration();
			if (feeConfiguration == null) {
				throw new InvalidSubscriptionConfigurationException("Missing active payment configuration for subscription id " + subscriptionConfiguration.getId());
			}
 			BigDecimal softwareThroughput = registerTransaction.getAccountRegisterSummaryFields().getAssignmentSoftwareThroughput();

			/**
			 * Given an amount, find the subscription payment tier where that particular amount is in between the min and max bounds
			 */
			SubscriptionPaymentTier paymentTierSWThroughput = feeConfiguration.findSubscriptionPaymentTierForThroughputAmount(softwareThroughput);
			if (paymentTierSWThroughput == null) {
				throw new InvalidSubscriptionConfigurationException("No payment tier found for subscription id " + subscriptionConfiguration.getId() + " throughput $" + softwareThroughput);
			}

			SubscriptionThroughputIncrementTransaction subscriptionThroughputIncrementTx = null;
			/**
			 * If the payment tier is marked as NOT_REACHED, it means that this is the first time we go up to that limit
			 * and we need to switch to the next level.
			 */
			if (paymentTierSWThroughput.getSubscriptionPaymentTierSoftwareStatusType().isNotReached()) {
				//Mark the payment tier as PROCESSING and trigger and event to create the incremental invoice.
				paymentTierSWThroughput.setSubscriptionPaymentTierSoftwareStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.PROCESSING));
				subscriptionThroughputIncrementTx = new SubscriptionThroughputIncrementTransaction(paymentTierSWThroughput, registerTransaction);
				subscriptionThroughputIncrementTx.setSoftwareIncrement(true);

				//SW and VOR will trigger together if the subscription has VOR service
				if (subscriptionConfiguration.isVendorOfRecord()) {
					subscriptionThroughputIncrementTx.setVorIncrement(true);
					paymentTierSWThroughput.setSubscriptionPaymentTierVorStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.PROCESSING));
				}

			} else if (paymentTierSWThroughput.getSubscriptionPaymentTierSoftwareStatusType().isActive()) {
				/**
				 *  If this is the active payment tier, we need to check for the percentage reached to send and email to the admin/managers
				 *  and/or WM company manager.
				 */
				if (paymentTierSWThroughput.hasReachedSoftwareThroughputNewThreshold(softwareThroughput)) {
					BigDecimal percentReached = paymentTierSWThroughput.calculatePercentReached(softwareThroughput);
					paymentTierSWThroughput.setSoftwareThroughputReached(percentReached);
				}
			}

			if (subscriptionThroughputIncrementTx != null) {
				subscriptionThroughputIncrementTxDAO.saveOrUpdate(subscriptionThroughputIncrementTx);
				subscriptionConfiguration.setLastThroughputUpperBoundReachedOn(Calendar.getInstance());
			}
		}
	}

	@Override
	public void updateCompanyAccountServiceType(long companyId, List<AccountServiceTypeDTO> accountServiceTypeUpdates) {
		Company company = companyDAO.findById(companyId);
		Assert.notNull(company);
		PaymentConfiguration paymentConfiguration = company.getPaymentConfiguration();
		Assert.notNull(paymentConfiguration, "Invalid Payment Configuration");
		Assert.isTrue(paymentConfiguration.isTransactionalPricing(), "Service Type can only be updated for accounts with Transactional pricing");

		for (AccountServiceTypeDTO accountServiceTypeDTO : accountServiceTypeUpdates) {
			AccountServiceType accountServiceType = lookupEntityDAO.findByCode(AccountServiceType.class, accountServiceTypeDTO.getAccountServiceTypeCode());
			Assert.notNull(accountServiceType, "Invalid Account Service Type");

			updatePaymentConfigurationAccountServiceType(paymentConfiguration, accountServiceTypeDTO.getCountryCode(), accountServiceType);
		}
	}

	@Override
	public void updatePaymentConfigurationAccountServiceType(PaymentConfiguration paymentConfiguration, AccountServiceType accountServiceType) {
		Assert.notNull(paymentConfiguration);

		// Only WM_SUPPORTED_COUNTRIES are allowed to change accountServiceType 
		for (String countryId : Country.WM_SUPPORTED_COUNTRIES) {
			updatePaymentConfigurationAccountServiceType(paymentConfiguration, countryId, accountServiceType);
		}
	}

	@Override
	public void updatePaymentConfigurationAccountServiceType(PaymentConfiguration paymentConfiguration, String countryId, AccountServiceType accountServiceType) {
		Assert.notNull(paymentConfiguration);
		Assert.notNull(countryId);

		AccountServiceTypeConfiguration accountServiceTypeConfiguration = paymentConfiguration.findAccountServiceTypeConfigurationForCountry(countryId);
		if (accountServiceTypeConfiguration != null) {
			if ((accountServiceType.isNone() || (!accountServiceType.isNone() && Country.WM_SUPPORTED_COUNTRIES.contains(countryId)))
					&& !accountServiceTypeConfiguration.getAccountServiceType().getCode().equals(accountServiceType.getCode())) {
				accountServiceTypeConfiguration.setAccountServiceType(accountServiceType);
				paymentConfiguration.setAccountServiceTypeModifiedOn(Calendar.getInstance());
			}
		}
	}

	@Override
	public List<AccountServiceType> findAllAccountServiceType() {
		return lookupEntityDAO.findLookupEntities(AccountServiceType.class);
	}

	@Override
	/**
	 * if worker has active tax entity
	 *   use worker tax entity country
	 * else if worker profile has non-foreign (US or Canada) address
	 *   use worker profile country
	 * else if worker profile postal code is a non-foreign (US or Canada) address
	 *   use worker profile postal code address
	 * else if worker company has non-foreign (US or Canada) address
	 *   use worker company address
	 * else if work has address
	 *   use address country
	 * else
	 *   set service type to 'none'
	 */
	public AccountServiceType findAccountServiceTypeConfiguration(AbstractWork work) {

		Assert.notNull(work);

		// init countryIds for worker tax entity, worker profile address, worker postal code, worker company address
		String workerTaxEntityCountryId = null;
		String workerProfileAddressCountryId = null;
		String workerProfilePostalCodeCountryId = null;
		String workerCompanyAddressCountryId = null;
		WorkResource worker = work.isActive() ? workService.findActiveWorkResource(work.getId()) : null;
		if (worker != null && worker.getUser() != null) {
			Profile workerProfile = worker.getUser().getProfile();
			if(workerProfile != null) {
				Address workerProfileAddress = addressService.findById(workerProfile.getAddressId());
				if(workerProfileAddress != null && workerProfileAddress.getCountry() != null) {
					workerProfileAddressCountryId = workerProfileAddress.getCountry().getId();
				}
				if (workerProfile.getProfilePostalCode() != null && workerProfile.getProfilePostalCode().getCountry() != null) {
					workerProfilePostalCodeCountryId = workerProfile.getProfilePostalCode().getCountry().getId();
				}
			}
			Company workerCompany = worker.getUser().getCompany();
			if (workerCompany != null) {
				// If resource's company has an active tax entity, use the country from the tax entity
				AbstractTaxEntity workerTaxEntity = taxService.findActiveTaxEntityByCompany(workerCompany.getId());
				if(workerTaxEntity != null) {
					workerTaxEntityCountryId = taxService.findTaxEntityCountryId(workerTaxEntity);
				}
				Address workerCompanyAddress = workerCompany.getAddress();
				if(workerCompanyAddress != null && workerCompanyAddress.getCountry() != null) {
					workerCompanyAddressCountryId = workerCompanyAddress.getCountry().getId();
				}
			}
		}

		// Determine appropriate account service type
		AccountServiceType accountServiceType;
		if (workerTaxEntityCountryId != null) {
			accountServiceType = findAccountServiceTypeConfiguration(work.getCompany(), workerTaxEntityCountryId);
		} else if(workerProfileAddressCountryId != null && isSupportedTaxCountryId(workerProfileAddressCountryId)) {
			accountServiceType = findAccountServiceTypeConfiguration(work.getCompany(), workerProfileAddressCountryId);
		} else if (workerProfilePostalCodeCountryId != null && isSupportedTaxCountryId(workerProfilePostalCodeCountryId)) {
			accountServiceType = findAccountServiceTypeConfiguration(work.getCompany(), workerProfilePostalCodeCountryId);
		} else if (workerCompanyAddressCountryId != null && isSupportedTaxCountryId(workerCompanyAddressCountryId)) {
			accountServiceType = findAccountServiceTypeConfiguration(work.getCompany(), workerCompanyAddressCountryId);
		} else if (work.getAddress() != null && work.getAddress().getCountry() != null) {
			accountServiceType = findAccountServiceTypeConfiguration(work.getCompany(), work.getAddress().getCountry().getId());
		} else{
			accountServiceType = new AccountServiceType(AccountServiceType.NONE);
		}
		return accountServiceType;
	}

	private boolean isSupportedTaxCountryId(String countryId) {
		String taxCountry = AbstractTaxEntity.getCountryFromCountryId(countryId);
		return !AbstractTaxEntity.COUNTRY_OTHER.equals(taxCountry);
	}

	@Override
	public AccountServiceType findAccountServiceTypeConfiguration(Company company, String countryId) {
		if (company != null && countryId != null) {
			PaymentConfiguration paymentConfiguration = company.getPaymentConfiguration();
			return paymentConfiguration.findAccountServiceTypeForCountry(countryId);
		}
		return new AccountServiceType(AccountServiceType.NONE);
	}

	@Override
	public List<CompanyAccountPricingTypeChange> getCompanyAccountPricingTypeChangeScheduledBeforeDate(Calendar updateDate, Boolean executed) {
		return companyAccountPricingTypeChangeDAO.getCompanyAccountPricingTypeChangeScheduledBeforeDate(updateDate, executed);
	}

	@Override
	public void saveServiceTransactionImmediateRevenueEffectiveDate(ServiceTransaction transaction) {
		/**
		 * Fees like deposit return, withdrawal return, late payment, etc., charged through an ad-hoc invoice will be recognized on the issue date.
		 * Same applies to subscription revenue that is invoiced without a subscription plan
		 */
		Assert.notNull(transaction);
		Calendar revenueEffectiveDate = DateUtilities.cloneCalendar(transaction.getTransactionDate());
		ServiceTransactionRevenue revenue = new ServiceTransactionRevenue(transaction);
		revenue.setRevenueEffectiveDate(revenueEffectiveDate);
		revenue.setRevenueAmount(transaction.getAmount().abs());
		revenue.setDeferredRevenue(false);
		serviceTransactionRevenueDAO.saveOrUpdate(revenue);
	}

	@Override
	public void saveServiceTransactionDeferredRevenueEffectiveDates(ServiceTransaction transaction) {
		Assert.notNull(transaction);
		if (transaction.getPaymentPeriod() instanceof SubscriptionPaymentPeriod && ((SubscriptionPaymentPeriod)transaction.getPaymentPeriod()).getSubscriptionPeriodType().getCode().equals(SubscriptionPeriodType.AUTO)) {
			//The fees came from a subscription invoice
			Map<Calendar, BigDecimal> revenueDates = calculateSubscriptionRevenueEffectiveDates((SubscriptionPaymentPeriod) transaction.getPaymentPeriod(), transaction.getAmount());
			for (Map.Entry<Calendar, BigDecimal> entry : revenueDates.entrySet()) {
				Calendar effectiveDate = entry.getKey();
				ServiceTransactionRevenue revenue = new ServiceTransactionRevenue(transaction);
				revenue.setRevenueEffectiveDate(effectiveDate);
				revenue.setRevenueAmount(entry.getValue().abs());
				//Incremental transactions get recognized at the end of the month and they are not considered deferred
				//unless they are from a different payment period
				if (transaction.isSubscriptionIncrementalTransaction()) {
					int paymentPeriodMonth = transaction.getPaymentPeriod().getPeriodDateRange().getFrom().get(Calendar.MONTH);
					int transactionMonth = transaction.getTransactionDate().get(Calendar.MONTH);
					revenue.setDeferredRevenue(transactionMonth != paymentPeriodMonth);
				}
				serviceTransactionRevenueDAO.saveOrUpdate(revenue);
			}
		} else {
			//The fees came from an Ad-hoc invoice
			saveServiceTransactionImmediateRevenueEffectiveDate(transaction);
		}

	}

	@Override
	public Map<Calendar, BigDecimal> calculateSubscriptionRevenueEffectiveDates(SubscriptionPaymentPeriod paymentPeriod, BigDecimal totalAmountPerPeriod) {
		Assert.notNull(paymentPeriod);
		Map<Calendar, BigDecimal> resultMap = Maps.newLinkedHashMap();

		SubscriptionConfiguration subscriptionConfiguration = paymentPeriod.getSubscriptionConfiguration();
		int numberOfMonths = subscriptionConfiguration.getSubscriptionPeriod().getMonths();
		BigDecimal revenueAmountPerPeriod = totalAmountPerPeriod.divide(BigDecimal.valueOf(numberOfMonths), MathContext.DECIMAL32);
		revenueAmountPerPeriod = revenueAmountPerPeriod.setScale(2, RoundingMode.DOWN);
		Calendar startPaymentPeriod = DateUtilities.cloneCalendar(paymentPeriod.getPeriodDateRange().getFrom());

		BigDecimal cumulativeAmount = BigDecimal.ZERO;
		for (int i = 1; i <= numberOfMonths; i++) {
			if (i > 1) {
				startPaymentPeriod.add(Calendar.MONTH, 1);
			}
			Calendar revenueEffectiveDate = DateUtilities.getCalendarWithLastDayOfTheMonthWithMinimumTime(startPaymentPeriod, TimeZone.getTimeZone("UTC"));
			//In the last iteration we subtract the cumulative from the original total amount.
			if (i == numberOfMonths) {
				revenueAmountPerPeriod = totalAmountPerPeriod.subtract(cumulativeAmount);
			}
			resultMap.put(revenueEffectiveDate, revenueAmountPerPeriod);
			cumulativeAmount = cumulativeAmount.add(revenueAmountPerPeriod);
		}
		return resultMap;
	}

}
