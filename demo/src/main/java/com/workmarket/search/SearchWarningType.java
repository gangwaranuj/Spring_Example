package com.workmarket.search;

import com.workmarket.thrift.EnumValue;

public enum SearchWarningType implements EnumValue {
	LOCATION_ZERO_RESULTS(0),
	LOCATION_OVER_QUERY_LIMIT(1),
	LOCATION_REQUEST_DENIED(2),
	LOCATION_INVALID_REQUEST(3),
	LOCATION_UNKNOWN_ERROR(4);

	private final int value;

	private SearchWarningType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static SearchWarningType findByValue(int value) {
		switch (value) {
			case 0:
				return LOCATION_ZERO_RESULTS;
			case 1:
				return LOCATION_OVER_QUERY_LIMIT;
			case 2:
				return LOCATION_REQUEST_DENIED;
			case 3:
				return LOCATION_INVALID_REQUEST;
			case 4:
				return LOCATION_UNKNOWN_ERROR;
			default:
				return null;
		}
	}
}
