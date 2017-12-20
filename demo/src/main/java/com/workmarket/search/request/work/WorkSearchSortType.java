package com.workmarket.search.request.work;

public enum WorkSearchSortType {
	CREATED_ON(1),
	SCHEDULED_FROM(2),
	SENT_DATE(3),
	COMPLETED_DATE(4),
	APPROVED_DATE(5),
	PAID_DATE(6),
	TIME_FROM(7),
	TIME_THROUGH(8),
	CLIENT(14),
	LAST_MODIFIED_DATE(15),
	TITLE(16),
	KEYWORD(17),
	STATE(18),
	DUE_DATE(19); // payment due date

	private final int value;

	private WorkSearchSortType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public String getDashboardPaginationSortColumn() {
		return this.name();
	}

	public static WorkSearchSortType findByValue(int value) {
		for (WorkSearchSortType dst : values()) {
			if (dst.value == value) return dst;
		}
		return null;
	}
}
