package com.workmarket.service.exception.search;

import com.workmarket.thrift.EnumValue;

public enum ReindexerErrorType implements EnumValue {
	SOLR_ERROR(0),
	INVALID_DATA(1),
	IO_EXCEPTION(2);

	private final int value;

	private ReindexerErrorType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static ReindexerErrorType findByValue(int value) {
		switch (value) {
			case 0:
				return SOLR_ERROR;
			case 1:
				return INVALID_DATA;
			case 2:
				return IO_EXCEPTION;
			default:
				return null;
		}
	}
}
