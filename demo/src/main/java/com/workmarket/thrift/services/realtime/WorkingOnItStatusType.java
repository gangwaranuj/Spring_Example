package com.workmarket.thrift.services.realtime;

import com.workmarket.thrift.EnumValue;

public enum WorkingOnItStatusType implements EnumValue {
	ON(0),
	OFF(1);

	private final int value;

	private WorkingOnItStatusType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static WorkingOnItStatusType findByValue(int value) {
		switch (value) {
			case 0:
				return ON;
			case 1:
				return OFF;
			default:
				return null;
		}
	}
}
