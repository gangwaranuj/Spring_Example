package com.workmarket.thrift.work.uploader;

import com.workmarket.thrift.EnumValue;

public enum WorkUploadErrorType implements EnumValue {
	MULTIPLE_STRATEGIES_INFERRED(0),
	MISSING_DATA(1),
	NO_PRICING_STRATEGY(2),
	INVALID_DATA(3),
	MALFORMED_DATA(4),
	VALIDATION(5),
	MAPPING_COLUMN_NAMING_MISMATCH(6),
	MAPPING_COLUMN_LENGTH_MISMATCH(7),
	MAPPING_COLUMN_NAME_NOT_FOUND(8);

	private final int value;

	private WorkUploadErrorType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static WorkUploadErrorType findByValue(int value) {
		switch (value) {
			case 0:
				return MULTIPLE_STRATEGIES_INFERRED;
			case 1:
				return MISSING_DATA;
			case 2:
				return NO_PRICING_STRATEGY;
			case 3:
				return INVALID_DATA;
			case 4:
				return MALFORMED_DATA;
			case 5:
				return VALIDATION;
			case 6:
				return MAPPING_COLUMN_NAMING_MISMATCH;
			case 7:
				return MAPPING_COLUMN_LENGTH_MISMATCH;
			case 8:
				return MAPPING_COLUMN_NAME_NOT_FOUND;
			default:
				return null;
		}
	}
}
