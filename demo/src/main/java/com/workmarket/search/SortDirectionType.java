package com.workmarket.search;

import com.workmarket.thrift.EnumValue;

public enum SortDirectionType implements EnumValue {
	ASC(0),
	DESC(1);

	private final int value;

	private SortDirectionType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static SortDirectionType findByValue(int value) {
		switch (value) {
			case 0:
				return ASC;
			case 1:
				return DESC;
			default:
				return null;
		}
	}

	public static SortDirectionType findByName(String name) {
		switch (name.toLowerCase()) {
			case "asc":
				return ASC;
			case "desc":
				return DESC;
			default:
				return null;
		}
	}
}
