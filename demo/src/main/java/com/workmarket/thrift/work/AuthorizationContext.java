package com.workmarket.thrift.work;

import com.workmarket.thrift.EnumValue;

public enum AuthorizationContext implements EnumValue {
	READ_ONLY(1), // not an admin and either declined resource, cancelled resource, or invited inactive
	BUYER(2), // created the assignment
	ADMIN(3), // created the assignment or someone in the company who can manage assignments
	RESOURCE(4), // invited resource, active resource, or invited inactive
	ACTIVE_RESOURCE(5),
	PAY(6),
	DISPATCHER(7);

	private final int value;

	AuthorizationContext(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static AuthorizationContext findByValue(int value) {
		switch (value) {
			case 1:
				return READ_ONLY;
			case 2:
				return BUYER;
			case 3:
				return ADMIN;
			case 4:
				return RESOURCE;
			case 5:
				return ACTIVE_RESOURCE;
			case 6:
				return PAY;
			case 7:
				return DISPATCHER;
			default:
				return null;
		}
	}
}
