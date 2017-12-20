package com.workmarket.thrift.assessment;

import com.workmarket.thrift.EnumValue;

public enum NotificationType implements EnumValue {
	NEW_ATTEMPT(0),
	NEW_ATTEMPT_BY_INVITEE(1),
	ATTEMPT_UNGRADED(2),
	ASSESSMENT_INACTIVE(3);

	private final int value;

	private NotificationType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static NotificationType findByValue(int value) {
		switch (value) {
			case 0:
				return NEW_ATTEMPT;
			case 1:
				return NEW_ATTEMPT_BY_INVITEE;
			case 2:
				return ATTEMPT_UNGRADED;
			case 3:
				return ASSESSMENT_INACTIVE;
			default:
				return null;
		}
	}
}
