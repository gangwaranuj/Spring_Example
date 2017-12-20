package com.workmarket.thrift.work;

import com.workmarket.thrift.EnumValue;

public enum RequestContext implements EnumValue {
	OWNER(1),
	ASSIGNED_COMPANY(2),
	INVITED_COMPANY(3),
	INVITED(4),
	ACTIVE_RESOURCE(5),
	INVITED_INACTIVE(6),
	CANCELLED_RESOURCE(7),
	DECLINED_RESOURCE(8),
	UNRELATED(9),
	COMPANY_OWNED(10),
	DISPATCHER(11);

	private final int value;

	RequestContext(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static RequestContext findByValue(int value) {
		switch (value) {
			case 1:
				return OWNER;
			case 2:
				return ASSIGNED_COMPANY;
			case 3:
				return INVITED_COMPANY;
			case 4:
				return INVITED;
			case 5:
				return ACTIVE_RESOURCE;
			case 6:
				return INVITED_INACTIVE;
			case 7:
				return CANCELLED_RESOURCE;
			case 8:
				return DECLINED_RESOURCE;
			case 9:
				return UNRELATED;
			case 10:
				return COMPANY_OWNED;
			case 11:
				return DISPATCHER;
			default:
				return null;
		}
	}
}
