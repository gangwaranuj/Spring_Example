package com.workmarket.thrift.core;

import com.workmarket.thrift.EnumValue;

public enum MonitorStatusType implements EnumValue {
	OK(0),
	WARN(1),
	ERROR(2);

	private final int value;

	private MonitorStatusType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static MonitorStatusType findByValue(int value) {
		switch (value) {
			case 0:
				return OK;
			case 1:
				return WARN;
			case 2:
				return ERROR;
			default:
				return null;
		}
	}
}
