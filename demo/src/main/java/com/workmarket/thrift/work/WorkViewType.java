package com.workmarket.thrift.work;

import com.workmarket.thrift.EnumValue;

public enum WorkViewType implements EnumValue {
	WEB(1),
	MOBILE(2),
	OTHER(3);

	private final int value;

	private WorkViewType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static WorkViewType findByValue(int value) {
		switch (value) {
			case 1:
				return WEB;
			case 2:
				return MOBILE;
			case 3:
				return OTHER;
			default:
				return null;
		}
	}
}
