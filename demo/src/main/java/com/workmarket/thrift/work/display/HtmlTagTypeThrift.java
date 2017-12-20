package com.workmarket.thrift.work.display;

import com.workmarket.thrift.EnumValue;

public enum HtmlTagTypeThrift implements EnumValue {
	INPUT_TEXT(0),
	SELECT_OPTION(1),
    MULTI_SELECT_OPTION(2),
	DATE(3),
	DATE_TIME(4),
	TO_FROM_DATES(5),
	NUMERIC(6),
	NUMERIC_RANGE(7),
	DISPLAY(8);

	private final int value;

	private HtmlTagTypeThrift(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static HtmlTagTypeThrift findByValue(int value) {
		switch (value) {
			case 0:
				return INPUT_TEXT;
			case 1:
				return SELECT_OPTION;
            case 2:
                return MULTI_SELECT_OPTION;
			case 3:
				return DATE;
			case 4:
				return DATE_TIME;
			case 5:
				return TO_FROM_DATES;
			case 6:
				return NUMERIC;
			case 7:
				return NUMERIC_RANGE;
			case 8:
				return DISPLAY;
			default:
				return null;
		}
	}
}
