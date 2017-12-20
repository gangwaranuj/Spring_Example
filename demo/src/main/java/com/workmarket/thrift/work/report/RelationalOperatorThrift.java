package com.workmarket.thrift.work.report;

import com.workmarket.thrift.EnumValue;

public enum RelationalOperatorThrift implements EnumValue {
	WORK_EQUAL_TO(0),
	WORK_NOT_EQUAL_TO(1),
	WORK_GREATER_THAN(2),
	WORK_GREATER_THAN_EQUAL_TO(3),
	WORK_LESS_THAN(4),
	WORK_LESS_THAN_EQUAL_TO(5),
	WORK_PLEASE_SELECT(6);

	private final int value;

	private RelationalOperatorThrift(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static RelationalOperatorThrift findByValue(int value) {
		switch (value) {
			case 0:
				return WORK_EQUAL_TO;
			case 1:
				return WORK_NOT_EQUAL_TO;
			case 2:
				return WORK_GREATER_THAN;
			case 3:
				return WORK_GREATER_THAN_EQUAL_TO;
			case 4:
				return WORK_LESS_THAN;
			case 5:
				return WORK_LESS_THAN_EQUAL_TO;
			case 6:
				return WORK_PLEASE_SELECT;
			default:
				return null;
		}
	}
}
