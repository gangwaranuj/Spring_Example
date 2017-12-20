package com.workmarket.api.v2.worker.model;

public class PaginationInfo {
  // API TODO - normalize into API v2 response?

  private final int limit;
  private final int offset; // zero based
  private final Long rowCount; // total records

  public PaginationInfo(final int limit, final int offset, final Long rowCount) {
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
