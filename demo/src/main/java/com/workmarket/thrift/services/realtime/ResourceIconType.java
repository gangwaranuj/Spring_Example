package com.workmarket.thrift.services.realtime;

import com.workmarket.thrift.EnumValue;

public enum ResourceIconType implements EnumValue {
	QUESTION(0),
	OFFER_OPEN(1),
	OFFER_EXPIRED(2),
	OFFER_DECLINED(3),
	ROBODIALER_AVAILABLE(4),
	ROBODIALER_ACTIVE(5),
	NOTE(6),
	VIEWED_ON_MOBILE(7),
	VIEWED_ON_WEB(8),
	IS_EMPLOYEE(9),
	ROBODIALER_NOT_AVAILABLE(10),
	LONG_DISTANCE_RESOURCE(11);

	private final int value;

	private ResourceIconType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static ResourceIconType findByValue(int value) {
		switch (value) {
			case 0:
				return QUESTION;
			case 1:
				return OFFER_OPEN;
			case 2:
				return OFFER_EXPIRED;
			case 3:
				return OFFER_DECLINED;
			case 4:
				return ROBODIALER_AVAILABLE;
			case 5:
				return ROBODIALER_ACTIVE;
			case 6:
				return NOTE;
			case 7:
				return VIEWED_ON_MOBILE;
			case 8:
				return VIEWED_ON_WEB;
			case 9:
				return IS_EMPLOYEE;
			case 10:
				return ROBODIALER_NOT_AVAILABLE;
			case 11:
				return LONG_DISTANCE_RESOURCE;
			default:
				return null;
		}
	}
}
