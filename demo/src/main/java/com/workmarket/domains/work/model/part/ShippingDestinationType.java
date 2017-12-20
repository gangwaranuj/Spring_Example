package com.workmarket.domains.work.model.part;

public enum ShippingDestinationType {
	NONE, // Worker supplies parts
	WORKER, // Parts shipped to worker
	ONSITE, // Parts shipped to work location
	PICKUP; // Parts shipped to third-party location

	public static ShippingDestinationType convertFromDistributionMethod(PartDistributionMethodType distributionMethodType) {
		switch (distributionMethodType) {
			case SHIPPED:
				return WORKER;
			case ONSITE:
				return ONSITE;
			case PICKUP:
				return PICKUP;
			default:
				return null;
		}
	}

	public PartDistributionMethodType convertToDistributionMethod() {
		switch (this) {
			case WORKER:
				return PartDistributionMethodType.SHIPPED;
			case ONSITE:
				return PartDistributionMethodType.ONSITE;
			case PICKUP:
				return PartDistributionMethodType.PICKUP;
			default:
				return null;
		}
	}
}
