package com.workmarket.domains.model.kpi;

import com.workmarket.thrift.EnumValue;

public enum KPIReportErrorType implements EnumValue {
	INVALID_REQUEST(1),
	DATABASE_UNAVAILABLE(2),
	MISSING_REQUIRED_FILTER(3);

	private final int value;

	private KPIReportErrorType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static KPIReportErrorType findByValue(int value) {
		switch (value) {
			case 1:
				return INVALID_REQUEST;
			case 2:
				return DATABASE_UNAVAILABLE;
			case 3:
				return MISSING_REQUIRED_FILTER;
			default:
				return null;
		}
	}
}
