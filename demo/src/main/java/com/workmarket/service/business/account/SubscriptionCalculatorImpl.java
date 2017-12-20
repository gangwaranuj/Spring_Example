package com.workmarket.service.business.account;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.payments.dao.PaymentPeriodDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionPaymentTierDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.domains.payments.dao.ServiceInvoiceDAO;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionFeeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.invoice.item.InvoiceLineItem;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Author: rocio
 */
@Service
public class SubscriptionCalculatorImpl implements SubscriptionCalculator {

	@Autowired private SubscriptionPaymentTierDAO subscriptionPaymentTierDAO;
	@Autowired private PaymentPeriodDAO subscriptionPaymentPeriodDAO;
	@Autowired private ServiceInvoiceDAO serviceInvoiceDAO;
	@Autowired private RegisterTransactionDAO registerTransactionDAO;
	@Autowired private SubscriptionService subscriptionService;

	@Override
	public SubscriptionPaymentDTO calculateSubscriptionPayment(SubscriptionConfiguration subscriptionConfiguration, BigDecimal throughputToDate, Calendar transactionDate) {
		Assert.notNull(subscriptionConfiguration);
		Assert.notNull(throughputToDate);
		Assert.notNull(transactionDate);
		SubscriptionPaymentDTO subscriptionPaymentDTO = new SubscriptionPaymentDTO();

		BigDecimal softwareFeeAmount;
		BigDecimal vorFeeAmount = BigDecimal.ZERO;
		BigDecimal addOnsAmount = BigDecimal.ZERO;
		BigDecimal discount = BigDecimal.ZERO;
		BigDecimal setupFee = BigDecimal.ZERO;

		SubscriptionFeeConfiguration subscriptionFeeConfiguration = subscriptionConfiguration.getActiveSubscriptionFeeConfiguration();
		Assert.notNull(subscriptionFeeConfiguration);

		subscriptionPaymentDTO.setSubscriptionFeeConfigurationId(subscriptionFeeConfiguration.getId());

		SubscriptionPaymentTier activeSoftwarePaymentTier = getActiveSubscriptionPaymentTierForTransactionDate(subscriptionConfiguration, transactionDate, SubscriptionPaymentTier.PaymentTierCategory.SOFTWARE);
		Assert.notNull(activeSoftwarePaymentTier);

		subscriptionPaymentDTO.setSubscriptionPaymentTierSWId(activeSoftwarePaymentTier.getId());

		//Regular payment aka Software fee
		softwareFeeAmount = activeSoftwarePaymentTier.getPaymentAmount();

		//Vor
		if (subscriptionConfiguration.isVendorOfRecord()) {
			SubscriptionPaymentTier activeVORPaymentTier = getActiveSubscriptionPaymentTierForTransactionDate(subscriptionConfiguration, transactionDate, SubscriptionPaymentTier.PaymentTierCategory.VENDOR_OF_RECORD);
			Assert.notNull(activeVORPaymentTier);

			subscriptionPaymentDTO.setSubscriptionPaymentTierVORId(activeVORPaymentTier.getId());

			vorFeeAmount = activeVORPaymentTier.getVendorOfRecordAmount();
		}

		//Add ons
		if (subscriptionConfiguration.hasEffectiveSubscriptionAddOns(transactionDate)) {
			addOnsAmount = subscriptionConfiguration.getTotalActiveAddOnsFeeByEffectiveDate(transactionDate);
		}

		//Discounts
		if (subscriptionConfiguration.isDiscountApplicable()) {
			discount = subscriptionConfiguration.getDiscountedAmountPerPeriod();
		}

		//Set up fee
		if (subscriptionConfiguration.hasSetupFee()) {
			setupFee = subscriptionConfiguration.getSetUpFee();
		}

		return subscriptionPaymentDTO
				.setSoftwareFeeAmount(softwareFeeAmount)
				.setVorFeeAmount(vorFeeAmount)
				.setAddOnsAmount(addOnsAmount)
				.setDiscount(discount)
				.setSetupFee(setupFee);
	}

	SubscriptionFeeConfiguration findSubscriptionFeeConfigurationEffectiveOnDate(List<SubscriptionFeeConfiguration> feeConfigurations, Calendar date) {
		Assert.notNull(date);
		SubscriptionFeeConfiguration feeConfiguration = null;

		if (isNotEmpty(feeConfigurations) && feeConfigurations.size() > 1) {
			for (SubscriptionFeeConfiguration configuration : feeConfigurations) {
				if (configuration.getEffectiveDate().before(date) ||
						configuration.getEffectiveDate().equals(date)) {
					feeConfiguration = configuration;
				}
			}
		}
		return feeConfiguration;
	}

	private SubscriptionPaymentTier getActiveSubscriptionPaymentTierForTransactionDate(SubscriptionConfiguration subscriptionConfiguration, Calendar transactionDate, SubscriptionPaymentTier.PaymentTierCategory paymentTierCategory) {
		Assert.notNull(subscriptionConfiguration);
		Assert.notNull(transactionDate);
		SubscriptionPaymentTier activePaymentTier = null;

		//Let's look at approved configurations with effective date in the future.
		List<SubscriptionFeeConfiguration> approvedFeeConfigurations = CollectionUtilities.asSortedList(subscriptionConfiguration.getApprovedSubscriptionFeeConfigurations());
		SubscriptionFeeConfiguration feeConfiguration = findSubscriptionFeeConfigurationEffectiveOnDate(approvedFeeConfigurations, transactionDate);
		if (feeConfiguration != null) {
			activePaymentTier = feeConfiguration.findActiveSubscriptionPaymentTierByPaymentTierCategory(paymentTierCategory);
		}

		if (activePaymentTier != null) {
			return activePaymentTier;
		}

		//Otherwise check for next throughput reset date
		if (subscriptionConfiguration.isSubscriptionResettingOnTransactionDate(transactionDate)) {
			//Get the first tier
			feeConfiguration = subscriptionConfiguration.getActiveSubscriptionFeeConfiguration();
			Assert.notNull(feeConfiguration);
			return feeConfiguration.findSubscriptionPaymentTierForThroughputAmount(BigDecimal.ZERO);
		}

		//Otherwise get the active payment tier
		return subscriptionPaymentTierDAO.findActiveSubscriptionPaymentTier(subscriptionConfiguration.getId(), paymentTierCategory);
	}

	@Override
	public Map<String, Object> getCurrentSubscriptionDetails(Long companyId) {
		SubscriptionConfiguration subscriptionConfiguration = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(companyId);
		if (subscriptionConfiguration == null) {
			return Maps.newHashMap();
		}

		SubscriptionFeeConfiguration activeSubscriptionFeeConfiguration = subscriptionConfiguration.getActiveSubscriptionFeeConfiguration();
		if (activeSubscriptionFeeConfiguration == null) {
			return Maps.newHashMap();
		}

		Calendar effectiveDate = activeSubscriptionFeeConfiguration.getEffectiveDate();
		Calendar today = Calendar.getInstance();

		Calendar date = today;
		if (effectiveDate.compareTo(today) > 0) {
			date = effectiveDate;
		} 

		SubscriptionPaymentTier currentTier = getActiveSubscriptionPaymentTierForTransactionDate(subscriptionConfiguration, date, SubscriptionPaymentTier.PaymentTierCategory.SOFTWARE);

		SubscriptionPaymentTier currentTierVor = null;
		if (subscriptionConfiguration.isVendorOfRecord()) {
			currentTierVor = getActiveSubscriptionPaymentTierForTransactionDate(subscriptionConfiguration, date, SubscriptionPaymentTier.PaymentTierCategory.VENDOR_OF_RECORD);
		}

		Map<String, Object> currentSubscriptionDetails = Maps.newHashMap(ImmutableMap.<String, Object>of(
			"subscriptionConfigurationId", subscriptionConfiguration.getId(),
			"subscriptionFeeConfigurationId", activeSubscriptionFeeConfiguration.getId(),
			"effectiveDate", effectiveDate.getTime(),
			"endDate", subscriptionConfiguration.getEndDate().getTime(),
			"period", subscriptionConfiguration.getSubscriptionPeriod().getPeriodAsString()
		));

		if (currentTier != null) {
			currentSubscriptionDetails.put("currentTierId", currentTier.getId());
			currentSubscriptionDetails.put("currentTierAmount", currentTier.getPaymentAmount());
		}

		if (currentTierVor != null) {
			currentSubscriptionDetails.put("currentTierVORId", currentTierVor.getId());
			currentSubscriptionDetails.put("currentTierVORAmount", currentTierVor.getVendorOfRecordAmount());
		}

		return currentSubscriptionDetails;
	}

	@Override
	public SubscriptionPaymentDTO calculateIncrementalSubscriptionPayment(SubscriptionConfiguration subscriptionConfiguration, Calendar transactionDate,
																		  SubscriptionPaymentTier newReachedPaymentTier, boolean isSoftwareIncrement, boolean isVORIncrement) {

		Assert.notNull(subscriptionConfiguration);
		Assert.notNull(transactionDate);
		Assert.notNull(newReachedPaymentTier);
		BigDecimal paymentAmountSWIncrement = BigDecimal.ZERO;
		BigDecimal paymentAmountVORIncrement = BigDecimal.ZERO;

		SubscriptionPaymentPeriod currentPaymentPeriod = subscriptionPaymentPeriodDAO.findBySubscriptionConfigurationIdAndDateInRange(subscriptionConfiguration.getId(), transactionDate);
		Assert.notNull(currentPaymentPeriod);
		Calendar nextPaymentDate;
		SubscriptionPaymentPeriod nextPaymentPeriod = subscriptionPaymentPeriodDAO.findNextFromDateBySubscriptionConfigurationId(subscriptionConfiguration.getId(), transactionDate);

		if (nextPaymentPeriod != null) {
			nextPaymentDate = DateUtilities.cloneCalendar(nextPaymentPeriod.getPeriodDateRange().getFrom());
		} else {
			//If the next payment period is null, means that the subscription is going to end at the end of the current period.
			nextPaymentDate = DateUtilities.cloneCalendar(currentPaymentPeriod.getPeriodDateRange().getThrough());
		}
		/**
		 * DateUtilities.getDaysBetween will return full days which is the behavior we want.
		 * If the next period is on the 30th and we are on the 15th, the daysToNextPeriod should be 14 days.
		 */
		int daysToNextPeriod = DateUtilities.getDaysBetween(transactionDate, nextPaymentDate);
		int daysBetweenCurrentPaymentPeriod = currentPaymentPeriod.getPeriodDateRange().getDaysBetweenDateRange();

		//Get the active payment tier, it's the same for SW and VOR
		SubscriptionPaymentTier activeSubscriptionPaymentTier = subscriptionPaymentTierDAO.findActiveSubscriptionPaymentTier(subscriptionConfiguration.getId(), SubscriptionPaymentTier.PaymentTierCategory.SOFTWARE);
		Assert.notNull(activeSubscriptionPaymentTier);
		Assert.isTrue(!newReachedPaymentTier.getId().equals(activeSubscriptionPaymentTier.getId()));

		if (isSoftwareIncrement) {
			BigDecimal subscriptionPaymentPeriodNewTier = NumberUtilities.rate(newReachedPaymentTier.getPaymentAmount(), daysToNextPeriod, daysBetweenCurrentPaymentPeriod);
			BigDecimal subscriptionPaymentPeriodActiveTier = NumberUtilities.rate(activeSubscriptionPaymentTier.getPaymentAmount(), daysToNextPeriod, daysBetweenCurrentPaymentPeriod);
			paymentAmountSWIncrement = subscriptionPaymentPeriodNewTier.subtract(subscriptionPaymentPeriodActiveTier);
		}

		if (isVORIncrement) {
			BigDecimal vorPaymentAmountNewTier = NumberUtilities.rate(newReachedPaymentTier.getVendorOfRecordAmount(), daysToNextPeriod, daysBetweenCurrentPaymentPeriod);
			BigDecimal vorPaymentAmountActiveTier = NumberUtilities.rate(activeSubscriptionPaymentTier.getVendorOfRecordAmount(), daysToNextPeriod, daysBetweenCurrentPaymentPeriod);
			paymentAmountVORIncrement = vorPaymentAmountNewTier.subtract(vorPaymentAmountActiveTier);
		}

		return new SubscriptionPaymentDTO()
				.setSoftwareFeeAmount(paymentAmountSWIncrement)
				.setVorFeeAmount(paymentAmountVORIncrement);

	}

	@Override
	public SubscriptionPaymentDTO calculateIncrementalSubscriptionPaymentForFutureInvoice(Long companyId, SubscriptionPaymentPeriod subscriptionPaymentPeriod, BigDecimal throughputToDate, SubscriptionFeeConfiguration subscriptionFeeConfiguration, boolean isSoftwareIncrement, boolean isVORIncrement) {
		Assert.notNull(companyId);
		Assert.notNull(subscriptionPaymentPeriod);
		Assert.notNull(subscriptionPaymentPeriod.getSubscriptionInvoice());
		Assert.notNull(throughputToDate);
		Assert.notNull(subscriptionFeeConfiguration);
		Assert.notNull(subscriptionPaymentPeriod.getPeriodDateRange());

		BigDecimal paymentAmountSWIncrement = BigDecimal.ZERO;
		BigDecimal paymentAmountVORIncrement = BigDecimal.ZERO;

		BigDecimal softwareFeeInvoicedAmount = BigDecimal.ZERO;
		BigDecimal vorFeeInvoicedAmount = BigDecimal.ZERO;

		SubscriptionInvoice invoice = (SubscriptionInvoice) serviceInvoiceDAO.get(subscriptionPaymentPeriod.getSubscriptionInvoice().getId());
		for (InvoiceLineItem invoiceLineItem : invoice.getInvoiceLineItems()) {
			Assert.notNull(invoiceLineItem.getAmount());
			if (invoiceLineItem.getType().equals(InvoiceLineItem.SUBSCRIPTION_VOR_INVOICE_LINE_ITEM)) {
				vorFeeInvoicedAmount = vorFeeInvoicedAmount.add(invoiceLineItem.getAmount());
			} else {
				softwareFeeInvoicedAmount = softwareFeeInvoicedAmount.add(invoiceLineItem.getAmount());
			}
		}

		SubscriptionPaymentTier subscriptionPaymentTierForThroughputAmount;

		//Check the next throughputResetDate
		if (subscriptionFeeConfiguration.getSubscriptionConfiguration().isSubscriptionResettingOnTransactionDate(subscriptionPaymentPeriod.getPeriodDateRange().getFrom())) {
			subscriptionPaymentTierForThroughputAmount = subscriptionFeeConfiguration.findSubscriptionPaymentTierForThroughputAmount(BigDecimal.ZERO);
		} else {
			//Get the correct payment tier based on the level of throughput reached
			subscriptionPaymentTierForThroughputAmount = subscriptionFeeConfiguration.findSubscriptionPaymentTierForThroughputAmount(throughputToDate);
		}
		Assert.notNull(subscriptionPaymentTierForThroughputAmount);

		if (isSoftwareIncrement) {
			// find any existing incremental amounts, this will insure we don't double bill when a
			// company busts multiple tiers in a month
			BigDecimal incrementalInvoicedAmount = registerTransactionDAO.calculateIncrementalSubscriptionTransactionPendingInvoiceForPaymentPeriod(companyId, subscriptionPaymentPeriod.getId(), RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT);
			paymentAmountSWIncrement = subscriptionPaymentTierForThroughputAmount.getPaymentAmount().subtract(incrementalInvoicedAmount).subtract(softwareFeeInvoicedAmount);
		}

		if (isVORIncrement) {
			// find any existing incremental amounts, this will insure we don't double bill when a
			// company busts multiple tiers in a month
			BigDecimal incrementalInvoicedAmount = registerTransactionDAO.calculateIncrementalSubscriptionTransactionPendingInvoiceForPaymentPeriod(companyId, subscriptionPaymentPeriod.getId(), RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT);
			paymentAmountVORIncrement = subscriptionPaymentTierForThroughputAmount.getVendorOfRecordAmount().subtract(incrementalInvoicedAmount).subtract(vorFeeInvoicedAmount);
		}

		return new SubscriptionPaymentDTO()
				.setSoftwareFeeAmount(paymentAmountSWIncrement)
				.setVorFeeAmount(paymentAmountVORIncrement);

	}
}