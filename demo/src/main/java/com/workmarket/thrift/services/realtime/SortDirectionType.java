package com.workmarket.thrift.services.realtime;

import com.workmarket.thrift.EnumValue;

public enum SortDirectionType implements EnumValue {
	ASC(1),
	DESC(2);

	private final int value;

	private SortDirectionType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static SortDirectionType findByValue(int value) {
		switch (value) {
			case 1:
				return ASC;
			case 2:
				return DESC;
			default:
				return null;
		}
	}
}