package com.workmarket.thrift.assessment;

import com.workmarket.thrift.EnumValue;

public enum RequestContext implements EnumValue {
	OWNER(0),
	COMPANY_OWNED(1),
	WORKER_POOL(2),
	INVITED(3),
	RESOURCE(4),
	UNRELATED(5);

	private final int value;

	private RequestContext(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static RequestContext findByValue(int value) {
		switch (value) {
			case 0:
				return OWNER;
			case 1:
				return COMPANY_OWNED;
			case 2:
				return WORKER_POOL;
			case 3:
				return INVITED;
			case 4:
				return RESOURCE;
			case 5:
				return UNRELATED;
			default:
				return null;
		}
	}
}
