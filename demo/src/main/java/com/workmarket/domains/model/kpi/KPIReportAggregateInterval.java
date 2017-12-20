package com.workmarket.domains.model.kpi;

import com.workmarket.thrift.EnumValue;

public enum KPIReportAggregateInterval implements EnumValue {
	DAY_OF_MONTH(0),
	WEEK_OF_YEAR(1),
	MONTH_OF_YEAR(2),
	YEAR(3),
	NONE(4);

	private final int value;

	private KPIReportAggregateInterval(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static KPIReportAggregateInterval findByValue(int value) {
		switch (value) {
			case 0:
				return DAY_OF_MONTH;
			case 1:
				return WEEK_OF_YEAR;
			case 2:
				return MONTH_OF_YEAR;
			case 3:
				return YEAR;
			case 4:
				return NONE;
			default:
				return null;
		}
	}
}
