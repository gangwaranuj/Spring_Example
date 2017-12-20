package com.workmarket.search.request.user;


public enum VerificationBoost {
	FAILED_SCREENING(0),
	NO_SCREENING(1),
	PASSED_SCREENING(2);

	private final int value;

	private VerificationBoost(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static VerificationBoost findByValue(int value) {
		switch (value) {
			case 0:
				return FAILED_SCREENING;
			case 1:
				return NO_SCREENING;
			case 2:
				return PASSED_SCREENING;
			default:
				return null;
		}
	}
}
