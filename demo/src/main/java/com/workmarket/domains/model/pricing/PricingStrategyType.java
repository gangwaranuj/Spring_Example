package com.workmarket.domains.model.pricing;

import org.apache.commons.lang.StringUtils;

public enum PricingStrategyType {
	FLAT("Flat Fee"),
	PER_HOUR("Per Hour"),
	PER_UNIT("Per Unit"),
	BLENDED_PER_HOUR("Blended Per Hour"),
	BLENDED_PER_UNIT("Blended Per Unit"),
	NONE("None"),
	INTERNAL("Internal / Lane 1");

	private String description;

	PricingStrategyType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	//TODO This is superfluous. The same is accomplished by PricingStrategyType(description);
	//    Maybe just make it:  try { return PricingStrategyType(description); } catch (Exception e) { return null; }
	//    Requires verification. see http://docs.oracle.com/javase/1.5.0/docs/guide/language/enums.html
	public static PricingStrategyType find(String description) {
		for (PricingStrategyType type : PricingStrategyType.values())
			if (StringUtils.equals(type.getDescription(), description))
				return type;
		return null;
	}

	public static Long getId(Enum<PricingStrategyType> type) {
		return (long) type.ordinal() + 1;
	}

	public boolean isInternal() {
		return this.equals(INTERNAL);
	}
}
