package com.workmarket.search.request;

import com.workmarket.thrift.EnumValue;

public enum SearchSortDirection implements EnumValue {
	ASCENDING(1),
	DESCENDING(2);

	private final int value;

	private SearchSortDirection(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static SearchSortDirection findByValue(int value) {
		switch (value) {
			case 1:
				return ASCENDING;
			case 2:
				return DESCENDING;
			default:
				return null;
		}
	}
}
