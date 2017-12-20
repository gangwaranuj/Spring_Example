package com.workmarket.thrift.assessment;

import com.workmarket.thrift.EnumValue;

public enum ItemType implements EnumValue {
	MULTIPLE_CHOICE(0),
	SINGLE_CHOICE_RADIO(1),
	SINGLE_CHOICE_LIST(2),
	SINGLE_LINE_TEXT(3),
	MULTIPLE_LINE_TEXT(4),
	DATE(5),
	PHONE(6),
	EMAIL(7),
	NUMERIC(8),
	DIVIDER(9),
	ASSET(10),
	LINK(11);

	private final int value;

	private ItemType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static ItemType findByValue(int value) {
		switch (value) {
			case 0:
				return MULTIPLE_CHOICE;
			case 1:
				return SINGLE_CHOICE_RADIO;
			case 2:
				return SINGLE_CHOICE_LIST;
			case 3:
				return SINGLE_LINE_TEXT;
			case 4:
				return MULTIPLE_LINE_TEXT;
			case 5:
				return DATE;
			case 6:
				return PHONE;
			case 7:
				return EMAIL;
			case 8:
				return NUMERIC;
			case 9:
				return DIVIDER;
			case 10:
				return ASSET;
			case 11:
				return LINK;
			default:
				return null;
		}
	}
}
