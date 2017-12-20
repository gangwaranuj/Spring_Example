package com.workmarket.domains.model.account.pricing.subscription;

import com.workmarket.utility.CollectionUtilities;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class SubscriptionUtilities {
	public static enum APPROVAL_TYPE {
		NEW("New"),
		RENEWAL("Renewal"),
		CANCELLATION("Cancellation"),
		PAYMENT_EDIT("Payment Edit"),
		ADDON_EDIT("Add-on Edit");

		String type;

		APPROVAL_TYPE(String type) {
			this.type = type;
		}

		public String toString() {
			return type;
		}

		public static APPROVAL_TYPE lookupByCode(Integer statusCode) {
			switch (statusCode) {
				case 0:
					return NEW;
				case 1:
					return RENEWAL;
				case 2:
					return CANCELLATION;
				case 3:
					return PAYMENT_EDIT;
				case 4:
					return ADDON_EDIT;
				default:
					return null;
			}
		}
	};


	private static BigDecimal calculateRRPerPeriod(SubscriptionConfiguration subscription) {
		Assert.notNull(subscription);

		SubscriptionPaymentTier lowerTier = CollectionUtilities.first(subscription.getSubscriptionPaymentTiers());

		BigDecimal paymentPerPeriodAmount = lowerTier.getPaymentAmount(),
				vendorOfRecordAmount = lowerTier.getVendorOfRecordAmount();

		return paymentPerPeriodAmount.add(vendorOfRecordAmount);
	}

	/**
	 * Monthly Recurring Revenue based on the lowest tier
	 * <p/>
	 * MRR = ((Payment period amount) + (VOR period amount)) / (Period in months)
	 *
	 * @param subscription
	 * @return
	 */
	public static BigDecimal calculateMonthlyRecurringRevenue(SubscriptionConfiguration subscription) {
		Assert.notNull(subscription);
		return calculateRRPerPeriod(subscription).divide(BigDecimal.valueOf(subscription.getSubscriptionPeriod().getMonths()), MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * Annual Recurring Revenue based on the lowest tier
	 * <p/>
	 * ARR = 12 * ((Payment period amount) / (Period in months)) = 12 * MRR
	 *
	 * @param subscription
	 * @return
	 */
	public static BigDecimal calculateAnnualRecurringRevenue(SubscriptionConfiguration subscription) {
		Assert.notNull(subscription);

		BigDecimal mrr = calculateRRPerPeriod(subscription);
		BigDecimal multiplier = BigDecimal.valueOf(12);
		Integer termsInMonths = subscription.getTermsInMonths();
		if (termsInMonths.intValue() <= 12) {
			multiplier = BigDecimal.valueOf(termsInMonths);
		}
		return mrr.divide(BigDecimal.valueOf(subscription.getSubscriptionPeriod().getMonths()), MathContext.DECIMAL32).multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * Returns the subscription's number of renewals formatted for on-screen display
	 *
	 * @param subscription
	 * @return
	 */
	public static String subscriptionAutoRenewalAsString(SubscriptionConfiguration subscription) {
		Integer numberOfRenewals = subscription.getNumberOfRenewals();

		return (numberOfRenewals > 0)
				? String.format("%d Renewal", numberOfRenewals)
				: "None";
	}
}
