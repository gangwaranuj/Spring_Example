package com.workmarket.thrift.work;

import com.workmarket.thrift.EnumValue;

public enum ResourceNoteActionType implements EnumValue {
	INTERESTED_WILL_LOOK_AT_IT(0),
	NEEDS_TO_CHECK_SCHEDULE(1),
	PHONE_NUMBER_NOT_VALID(2),
	LOOKING_INTO_ADDITIONAL_RESOURCE(3),
	LEFT_MESSAGE(4),
	OTHER(5);

	private final int value;

	private ResourceNoteActionType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static ResourceNoteActionType findByValue(int value) {
		switch (value) {
			case 0:
				return INTERESTED_WILL_LOOK_AT_IT;
			case 1:
				return NEEDS_TO_CHECK_SCHEDULE;
			case 2:
				return PHONE_NUMBER_NOT_VALID;
			case 3:
				return LOOKING_INTO_ADDITIONAL_RESOURCE;
			case 4:
				return LEFT_MESSAGE;
			case 5:
				return OTHER;
			default:
				return null;
		}
	}
}
