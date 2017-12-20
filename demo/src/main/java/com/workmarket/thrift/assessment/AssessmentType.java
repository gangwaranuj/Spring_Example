package com.workmarket.thrift.assessment;

import com.workmarket.thrift.EnumValue;

public enum AssessmentType implements EnumValue {
	GRADED(0),
	SURVEY(1);

	private final int value;

	private AssessmentType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static AssessmentType findByValue(int value) {
		switch (value) {
			case 0:
				return GRADED;
			case 1:
				return SURVEY;
			default:
				return null;
		}
	}
}
