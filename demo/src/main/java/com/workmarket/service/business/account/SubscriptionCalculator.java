package com.workmarket.service.business.account;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionFeeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentDTO;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;

/**
 * Author: rocio
 */
public interface SubscriptionCalculator {

	/**
	 * Calculates the payment the user would have to make for a given subscription, throughput and a date.
	 *
	 * @param subscriptionConfiguration
	 * @param throughputToDate
	 * @param transactionDate
	 * @return {@link com.workmarket.service.business.dto.account.pricing.subscription SubscriptionPaymentDTO}
	 */
	SubscriptionPaymentDTO calculateSubscriptionPayment(SubscriptionConfiguration subscriptionConfiguration, BigDecimal throughputToDate, Calendar transactionDate);

	SubscriptionPaymentDTO calculateIncrementalSubscriptionPayment(SubscriptionConfiguration subscriptionConfiguration, Calendar transactionDate,
			SubscriptionPaymentTier newReachedPaymentTier, boolean isSoftwareIncrement, boolean isVORIncrement);

	SubscriptionPaymentDTO calculateIncrementalSubscriptionPaymentForFutureInvoice(Long companyId, SubscriptionPaymentPeriod subscriptionPaymentPeriod, BigDecimal throughputToDate,
			SubscriptionFeeConfiguration subscriptionFeeConfiguration, boolean isSoftwareIncrement, boolean isVORIncrement);

	Map<String, Object> getCurrentSubscriptionDetails(Long companyId);

}
