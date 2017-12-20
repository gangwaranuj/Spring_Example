package com.workmarket.domains.model.account.pricing.subscription;

import com.google.common.collect.Maps;

import java.util.EnumSet;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public enum SubscriptionPeriod {

	MONTHLY(1),
	QUARTERLY(3),
	SEMIANNUAL(6),
	ANNUAL(12);

	private final int months;
	private static final Map<Integer, SubscriptionPeriod> lookup = Maps.newHashMapWithExpectedSize(SubscriptionPeriod.values().length);

	static {
		for (SubscriptionPeriod subscriptionPeriod : EnumSet.allOf(SubscriptionPeriod.class))
			lookup.put(subscriptionPeriod.getMonths(), subscriptionPeriod);
	}

	SubscriptionPeriod(int months) {
		this.months = months;
	}

	public int getMonths() {
		return months;
	}

	public String getPeriodAsString() {
		switch (this.months) {
			case 1:
				return "Monthly";
			case 3:
				return "Quarterly";
			case 6:
				return "Semiannually";
			case 12:
				return "Annually";
		}

		return StringUtils.EMPTY;
	}

	public static SubscriptionPeriod getSubscriptionPeriod(int months) {
		return lookup.get(months);
	}
}
