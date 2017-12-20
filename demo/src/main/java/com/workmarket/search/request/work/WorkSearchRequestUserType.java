package com.workmarket.search.request.work;

public enum WorkSearchRequestUserType {
	CLIENT(0),
	RESOURCE(1);

	private final int value;

	private WorkSearchRequestUserType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static WorkSearchRequestUserType findByValue(int value) {
		switch (value) {
			case 0:
				return CLIENT;
			case 1:
				return RESOURCE;
			default:
				return null;
		}
	}
}
