package com.workmarket.search.request.user;

public enum BackgroundScreeningChoice {
	backgroundCheck(1),
	@Deprecated governmentClearance(2),
	drugTested(3),
	backgroundCheckedWithinLast6Months(4),
	backgroundCheckedWithinLast12Months(5),
	drugTestedWithinLast6Months(6),
	drugTestedWithinLast12Months(7);

	private final int value;

	private BackgroundScreeningChoice(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static BackgroundScreeningChoice findByValue(int value) {
		switch (value) {
			case 1:
				return backgroundCheck;
			case 2:
				return governmentClearance;
			case 3:
				return drugTested;
			case 4:
				return backgroundCheckedWithinLast6Months;
			case 5:
				return backgroundCheckedWithinLast12Months;
			case 6:
				return drugTestedWithinLast6Months;
			case 7:
				return drugTestedWithinLast12Months;
			default:
				return null;
		}
	}
}
