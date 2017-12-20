package com.workmarket.domains.payments.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.pricing.PaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;

import java.util.Calendar;
import java.util.List;

/**
 * Author: rocio
 */
public interface PaymentPeriodDAO extends DAOInterface<PaymentPeriod> {

	List<SubscriptionPaymentPeriod> findBySubscriptionConfigurationId(long subscriptionConfigurationId);

	SubscriptionPaymentPeriod findBySubscriptionConfigurationIdAndDateInRange(long subscriptionConfigurationId, Calendar dateInRange);

	SubscriptionPaymentPeriod findNextFromDateBySubscriptionConfigurationId(long subscriptionConfigurationId, Calendar fromDate);

	SubscriptionPaymentPeriod findNextNotInvoicedSubscriptionPaymentPeriod(long subscriptionConfigurationId);

	SubscriptionPaymentPeriod findNextInvoiceableSubscriptionPaymentPeriod(long subscriptionConfigurationId, Calendar firstDayOfNextMonth);

	List<SubscriptionPaymentPeriod> findAllInvoicedFutureSubscriptionPaymentPeriods(long subscriptionConfigurationId);
}