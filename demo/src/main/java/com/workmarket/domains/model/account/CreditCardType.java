package com.workmarket.domains.model.account;

import com.google.common.collect.Maps;

import java.util.EnumSet;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rocio
 * Date: 7/11/12
 * Time: 4:06 PM
 */
public enum CreditCardType {

	MASTER_CARD("mastercard"),
	VISA("visa"),
	AMERICAN_EXPRESS("amex");

	private final String name;
	private static final Map<String, CreditCardType> lookup = Maps.newHashMapWithExpectedSize(CreditCardType.values().length);

	static {
		for (CreditCardType creditCardType : EnumSet.allOf(CreditCardType.class))
			lookup.put(creditCardType.getName(), creditCardType);
	}

	CreditCardType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static CreditCardType getCreditCardType(String name) {
		return lookup.get(name);
	}

}
