package com.workmarket.thrift.work.display;

import com.workmarket.thrift.EnumValue;

public enum WorkDisplayErrorType implements EnumValue {
	INVALID_REQUEST(1),
	INVALID_LOCALE(2),
	VALIDATION_EXCEPTION(3);

	private final int value;

	private WorkDisplayErrorType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static WorkDisplayErrorType findByValue(int value) {
		switch (value) {
			case 1:
				return INVALID_REQUEST;
			case 2:
				return INVALID_LOCALE;
			case 3:
				return VALIDATION_EXCEPTION;
			default:
				return null;
		}
	}
}
