package com.workmarket.thrift.search.cart;

import com.workmarket.thrift.EnumValue;

public enum SearchCartActionType implements EnumValue {
	ADDED(1),
	ALREADY_ADDED(2),
	REMOVED(3);

	private final int value;

	private SearchCartActionType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static SearchCartActionType findByValue(int value) {
		switch (value) {
			case 1:
				return ADDED;
			case 2:
				return ALREADY_ADDED;
			case 3:
				return REMOVED;
			default:
				return null;
		}
	}
}
