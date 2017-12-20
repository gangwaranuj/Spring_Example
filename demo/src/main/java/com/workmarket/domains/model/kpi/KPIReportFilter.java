package com.workmarket.domains.model.kpi;

import com.workmarket.thrift.EnumValue;

public enum KPIReportFilter implements EnumValue {
	INDUSTRY(0),
	COMPANY(1),
	PAYMENT_TERMS(3),
	LEAD_STATUS(4),
	RATING_STAR_VALUE(5),
	X_NUMBER_DRAFTS(6),
	ACTIVE_RESOURCE_USER_ID(7);

	private final int value;

	private KPIReportFilter(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static KPIReportFilter findByValue(int value) {
		switch (value) {
			case 0:
				return INDUSTRY;
			case 1:
				return COMPANY;
			case 3:
				return PAYMENT_TERMS;
			case 4:
				return LEAD_STATUS;
			case 5:
				return RATING_STAR_VALUE;
			case 6:
				return X_NUMBER_DRAFTS;
			case 7:
				return ACTIVE_RESOURCE_USER_ID;
			default:
				return null;
		}
	}
}
