package com.workmarket.domains.model.account.payment;

import java.util.EnumSet;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * When configuring bi-weekly statements, users can select a pair of days of the month for their payment cycle.
 * The purpose of this enum is to hold those date pairs, since we don't know if it will always be on 14 days basis (most likely yes, but who knows)
 *
 * @author rocio
 */
public enum BiweeklyPaymentDays {

	FIRST_PAYMENT_EVERY_DAY_1(1, 15),
	FIRST_PAYMENT_EVERY_DAY_2(2, 16),
	FIRST_PAYMENT_EVERY_DAY_3(3, 17),
	FIRST_PAYMENT_EVERY_DAY_4(4, 18),
	FIRST_PAYMENT_EVERY_DAY_5(5, 19),
	FIRST_PAYMENT_EVERY_DAY_6(6, 20),
	FIRST_PAYMENT_EVERY_DAY_7(7, 21),
	FIRST_PAYMENT_EVERY_DAY_8(8, 22),
	FIRST_PAYMENT_EVERY_DAY_9(9, 23),
	FIRST_PAYMENT_EVERY_DAY_10(10, 24),
	FIRST_PAYMENT_EVERY_DAY_11(11, 25),
	FIRST_PAYMENT_EVERY_DAY_12(12, 26),
	FIRST_PAYMENT_EVERY_DAY_13(13, 27),
	FIRST_PAYMENT_EVERY_DAY_14(14, 28),
	FIRST_PAYMENT_EVERY_DAY_15(15, 29),
	FIRST_PAYMENT_EVERY_DAY_16(16, 30);


	private final int firstDay;
	private final int secondDay;
	private static final Map<Integer, BiweeklyPaymentDays> lookup = Maps.newHashMapWithExpectedSize(BiweeklyPaymentDays.values().length);

	static {
		for (BiweeklyPaymentDays biweeklyPaymentDays : EnumSet.allOf(BiweeklyPaymentDays.class))
			lookup.put(biweeklyPaymentDays.getFirstDay(), biweeklyPaymentDays);
	}

	BiweeklyPaymentDays(int firstDay, int secondDay) {
		this.firstDay = firstDay;
		this.secondDay = secondDay;
	}

	public int getFirstDay() {
		return firstDay;
	}

	public int getSecondDay() {
		return secondDay;
	}

	public static BiweeklyPaymentDays getPaymentCycle(int firstDay) {
		return lookup.get(firstDay);
	}
}
