package com.workmarket.thrift.work;

import com.workmarket.thrift.EnumValue;

public enum WorkActionResponseCodeType implements EnumValue {
	SUCCESS(0),
	INVALID_REQUEST(1),
	UNKNOWN_HOST_ERROR(2),
	WORK_SERVICE_EXCEPTION(3),
	GENERAL_ERROR(4),
	INVALID_WORK_STATE(5);

	private final int value;

	private WorkActionResponseCodeType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static WorkActionResponseCodeType findByValue(int value) {
		switch (value) {
			case 0:
				return SUCCESS;
			case 1:
				return INVALID_REQUEST;
			case 2:
				return UNKNOWN_HOST_ERROR;
			case 3:
				return WORK_SERVICE_EXCEPTION;
			case 4:
				return GENERAL_ERROR;
			case 5:
				return INVALID_WORK_STATE;
			default:
				return null;
		}
	}
}
