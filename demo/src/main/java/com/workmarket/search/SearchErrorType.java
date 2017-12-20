package com.workmarket.search;

import com.workmarket.thrift.EnumValue;

public enum SearchErrorType implements EnumValue {
	INVALID_REQUEST(0),
	SEARCH_BROKE(1),
	NO_COMPANY(2),
	NO_ADDRESS(3),
	UNKNOWN(4),
	DATABASE_DOWN(5),
	SYNC_ERROR(6);

	private final int value;

	private SearchErrorType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static SearchErrorType findByValue(int value) {
		switch (value) {
			case 0:
				return INVALID_REQUEST;
			case 1:
				return SEARCH_BROKE;
			case 2:
				return NO_COMPANY;
			case 3:
				return NO_ADDRESS;
			case 4:
				return UNKNOWN;
			case 5:
				return DATABASE_DOWN;
			case 6:
				return SYNC_ERROR;
			default:
				return null;
		}
	}
}
