package com.workmarket.api.v2;

public class ApiPaginationUserNotifications {
	private final int limit;
	private final int offset; // zero based
	private final Long rowCount; // total records

  public ApiPaginationUserNotifications(final int limit, final int offset, final Long rowCount) {
    this.limit = limit;
    this.offset = offset;
    this.rowCount = rowCount;
  }

	public int getLimit() {
		return limit;
	}

	public int getOffset() {
		return offset;
	}

	public Long getRowCount() {
		return rowCount;
	}
}
