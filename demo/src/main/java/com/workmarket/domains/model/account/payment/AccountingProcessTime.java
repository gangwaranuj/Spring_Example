package com.workmarket.domains.model.account.payment;

import java.util.EnumSet;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Represents the number of days it takes to the accounting department of each company to process payments.
 *
 * @author rocio
 */
public enum AccountingProcessTime {

	ZERO_DAYS(0),
	ONE_DAY(1),
	TWO_DAYS(2),
	THREE_DAYS(3),
	FOUR_DAYS(4),
	FIVE_DAYS(5),
	SIX_DAYS(6),
	SEVEN_DAYS(7),
	FIFTEEN_DAYS(15);

	private final int paymentDays;
	private static final Map<Integer, AccountingProcessTime> lookup = Maps.newHashMapWithExpectedSize(AccountingProcessTime.values().length);

	static {
		for (AccountingProcessTime processTime : EnumSet.allOf(AccountingProcessTime.class))
			lookup.put(processTime.getPaymentDays(), processTime);
	}

	AccountingProcessTime(int paymentDays) {
		this.paymentDays = paymentDays;
	}

	public int getPaymentDays() {
		return paymentDays;
	}

	public static AccountingProcessTime getAccountingProcessTime(int paymentDays) {
		return lookup.get(paymentDays);
	}
}
