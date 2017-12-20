package com.workmarket.domains.work.model.part;

import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public enum ShippingProvider {
	OTHER("other"),
	UPS("ups"),
	USPS("usps"),
	FEDEX("fedex"),
	DHL("dhl");

	private static final Log LOGGER = LogFactory.getLog(ShippingProvider.class);

	private String code;

	ShippingProvider(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static ShippingProvider getShippingProvider(final String code) {
		try {
			return valueOf(code.toUpperCase());
		} catch (Exception e) {
			LOGGER.error("[trackingNumberAdapter] Unrecognized shipping provider code in response: " + code, e);
			return ShippingProvider.OTHER;
		}
	}

	public static List<ShippingProvider> getValidShippingProviders() {
		return Lists.newArrayList(ShippingProvider.values()).subList(1, ShippingProvider.values().length);
	}
}
