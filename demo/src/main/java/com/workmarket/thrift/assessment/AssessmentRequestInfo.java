package com.workmarket.thrift.assessment;

import com.workmarket.thrift.EnumValue;

public enum AssessmentRequestInfo implements EnumValue {
	CONTEXT_INFO(0),
	ITEM_INFO(1),
	CORRECT_CHOICES_INFO(2),
	STATISTICS_INFO(3),
	LATEST_ATTEMPT_INFO(4);

	private final int value;

	private AssessmentRequestInfo(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static AssessmentRequestInfo findByValue(int value) {
		switch (value) {
			case 0:
				return CONTEXT_INFO;
			case 1:
				return ITEM_INFO;
			case 2:
				return CORRECT_CHOICES_INFO;
			case 3:
				return STATISTICS_INFO;
			case 4:
				return LATEST_ATTEMPT_INFO;
			default:
				return null;
		}
	}
}
