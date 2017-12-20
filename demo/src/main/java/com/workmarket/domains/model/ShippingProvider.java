package com.workmarket.domains.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="shippingProvider")
@Table(name="shipping_provider")
@Deprecated // Use com.workmarket.domains.work.model.part.ShippingProvider.java
public class ShippingProvider extends LookupEntity {

	private static final long serialVersionUID = 1068080455861164799L;

	public static final String
		FEDEX = "fedex",
		UPS = "ups",
		DHL = "dhl",
		USPS = "usps",
		OTHER = "other";

	public ShippingProvider() {}
	
	public ShippingProvider(String code) {
		super(code);
	}
	
	public static ShippingProvider valueOf(String value) {
		switch (value) {
			case FEDEX:
				return new ShippingProvider(FEDEX);
			case UPS:
				return new ShippingProvider(UPS);
			case DHL:
				return new ShippingProvider(DHL);
			case USPS:
				return new ShippingProvider(USPS);
			default:
				return new ShippingProvider(OTHER);
		}
	}
}
