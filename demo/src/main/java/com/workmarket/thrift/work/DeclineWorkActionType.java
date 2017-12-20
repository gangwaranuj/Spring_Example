package com.workmarket.thrift.work;

import com.workmarket.thrift.EnumValue;

public enum DeclineWorkActionType implements EnumValue {
	SPEND_LIMIT_TOO_LOW(0),
	SCOPE_IS_NOT_CLEAR(1),
	NOT_QUALIFIED(2),
	LACK_NEEDED_TOOLS(3),
	LOCATION_TOO_FAR(4),
	UNAVAILABLE(5),
	SEVERE_WEATHER(6),
	CLIENT_REPUTATION(7),
	OTHER(9);

	private final int value;

	private DeclineWorkActionType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static DeclineWorkActionType findByValue(int value) {
		switch (value) {
			case 0:
				return SPEND_LIMIT_TOO_LOW;
			case 1:
				return SCOPE_IS_NOT_CLEAR;
			case 2:
				return NOT_QUALIFIED;
			case 3:
				return LACK_NEEDED_TOOLS;
			case 4:
				return LOCATION_TOO_FAR;
			case 5:
				return UNAVAILABLE;
			case 6:
				return SEVERE_WEATHER;
			case 7:
				return CLIENT_REPUTATION;
			case 9:
				return OTHER;
			default:
				return null;
		}
	}
}
