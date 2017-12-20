package com.workmarket.thrift.work;

import com.workmarket.thrift.EnumValue;

public enum ResourceNoteType implements EnumValue {
	ACCEPT(0),
	DECLINE(1),
	COUNTER(2),
	QUESTION(3),
	NOTE(4),
	REROUTE(5);

	private final int value;

	private ResourceNoteType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static ResourceNoteType findByValue(int value) {
		switch (value) {
			case 0:
				return ACCEPT;
			case 1:
				return DECLINE;
			case 2:
				return COUNTER;
			case 3:
				return QUESTION;
			case 4:
				return NOTE;
			case 5:
				return REROUTE;
			default:
				return null;
		}
	}
}
