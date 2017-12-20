package com.workmarket.thrift.services.realtime;

import com.workmarket.thrift.EnumValue;

public enum SortByType implements EnumValue {
	TIME_TO_APPOINTMENT(0),
	ORDER_AGE(1),
	SCHEDULED_TIME(2),
	DETAILS(3),
	SPEND_LIMIT(4),
	QUESTIONS(5),
	OFFERS(6),
	DECLINES(7),
	MODIFIED_TIME(8);

	private final int value;

	private SortByType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static SortByType findByValue(int value) {
		switch (value) {
			case 0:
				return TIME_TO_APPOINTMENT;
			case 1:
				return ORDER_AGE;
			case 2:
				return SCHEDULED_TIME;
			case 3:
				return DETAILS;
			case 4:
				return SPEND_LIMIT;
			case 5:
				return QUESTIONS;
			case 6:
				return OFFERS;
			case 7:
				return DECLINES;
			case 8:
				return MODIFIED_TIME;
			default:
				return null;
		}
	}
}
