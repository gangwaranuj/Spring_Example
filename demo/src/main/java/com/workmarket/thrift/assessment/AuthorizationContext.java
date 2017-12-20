package com.workmarket.thrift.assessment;

import com.workmarket.thrift.EnumValue;

public enum AuthorizationContext implements EnumValue {
	ADMIN(0),
	ATTEMPT(1),
	REATTEMPT(2);

	private final int value;

	private AuthorizationContext(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static AuthorizationContext findByValue(int value) {
		switch (value) {
			case 0:
				return ADMIN;
			case 1:
				return ATTEMPT;
			case 2:
				return REATTEMPT;
			default:
				return null;
		}
	}
}
