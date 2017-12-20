package com.workmarket.domains.model.account.payment;

import java.util.EnumSet;
import java.util.Map;

import com.google.common.collect.Maps;

public enum PaymentCycle {

	DAILY(1),
	WEEKLY(7),
	BIWEEKLY(14),
	MONTHLY(30);

	private final int paymentDays;
	private static final Map<Integer, PaymentCycle> lookup = Maps.newHashMapWithExpectedSize(PaymentCycle.values().length);

	static {
		for (PaymentCycle paymentCycle : EnumSet.allOf(PaymentCycle.class))
			lookup.put(paymentCycle.getPaymentDays(), paymentCycle);
	}

	PaymentCycle(int paymentDays) {
		this.paymentDays = paymentDays;
	}

	public int getPaymentDays() {
		return paymentDays;
	}

	public static PaymentCycle getPaymentCycle(int paymentDays) {
		return lookup.get(paymentDays);
	}
}
