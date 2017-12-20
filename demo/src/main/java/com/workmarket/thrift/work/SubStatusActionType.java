package com.workmarket.thrift.work;

import com.workmarket.thrift.EnumValue;

public enum SubStatusActionType implements EnumValue {
	ADDED(1),
	RESOLVED(2);

	private final int value;

	private SubStatusActionType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static SubStatusActionType findByValue(int value) {
		switch (value) {
			case 1:
				return ADDED;
			case 2:
				return RESOLVED;
			default:
				return null;
		}
	}
}
