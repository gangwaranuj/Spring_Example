package com.workmarket.thrift.services.realtime;

import com.workmarket.thrift.EnumValue;

public enum RealtimeErrorType implements EnumValue {
	INVALID_REQUEST(1),
	RESOURCE_OUT_OF_SYNC(2);

	private final int value;

	private RealtimeErrorType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static RealtimeErrorType findByValue(int value) {
		switch (value) {
			case 1:
				return INVALID_REQUEST;
			case 2:
				return RESOURCE_OUT_OF_SYNC;
			default:
				return null;
		}
	}
}
