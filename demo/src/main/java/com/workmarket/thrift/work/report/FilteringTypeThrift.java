package com.workmarket.thrift.work.report;

import com.workmarket.thrift.EnumValue;

public enum FilteringTypeThrift implements EnumValue {
	WORK_TEXT(0),
	WORK_DATE_TIME(1),
	WORK_NUMERIC(2),
	WORK_BIG_DECIMAL(3),
	WORK_DATE_RANGE(4),
	WORK_DATE_BEFORE(5),
	WORK_DATE_AFTER(6),
	WORK_NEXT_1_DAY(7),
	WORK_NEXT_7_DAYS(8),
	WORK_LAST_1_DAY(9),
	WORK_LAST_7_DAYS(10),
	WORK_LAST_30_DAYS(11),
	WORK_LAST_60_DAYS(12),
	WORK_LAST_90_DAYS(13),
	WORK_THIS_YEAR_TO_DATE(14),
	WORK_FIELD_VALUE(15),
	WORK_CONTAINS(16),
	WORK_PLEASE_SELECT(17),
	WORK_NEXT_30_DAYS(18),
	WORK_LAST_365_DAYS(19),
	WORK_NEXT_60_DAYS(20),
	WORK_NEXT_90_DAYS(21);

	private final int value;

	private FilteringTypeThrift(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static FilteringTypeThrift findByValue(int value) {
		switch (value) {
			case 0:
				return WORK_TEXT;
			case 1:
				return WORK_DATE_TIME;
			case 2:
				return WORK_NUMERIC;
			case 3:
				return WORK_BIG_DECIMAL;
			case 4:
				return WORK_DATE_RANGE;
			case 5:
				return WORK_DATE_BEFORE;
			case 6:
				return WORK_DATE_AFTER;
			case 7:
				return WORK_NEXT_1_DAY;
			case 8:
				return WORK_NEXT_7_DAYS;
			case 9:
				return WORK_LAST_1_DAY;
			case 10:
				return WORK_LAST_7_DAYS;
			case 11:
				return WORK_LAST_30_DAYS;
			case 12:
				return WORK_LAST_60_DAYS;
			case 13:
				return WORK_LAST_90_DAYS;
			case 14:
				return WORK_THIS_YEAR_TO_DATE;
			case 15:
				return WORK_FIELD_VALUE;
			case 16:
				return WORK_CONTAINS;
			case 17:
				return WORK_PLEASE_SELECT;
			case 18:
				return WORK_NEXT_30_DAYS;
			case 19:
				return WORK_LAST_365_DAYS;
			case 20:
				return WORK_NEXT_60_DAYS;
			case 21:
				return WORK_NEXT_90_DAYS;
			default:
				return null;
		}
	}
}
