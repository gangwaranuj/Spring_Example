package com.workmarket.api.v3.response;

public class ApiV3ResponseResultPaginationImpl implements ApiV3ResponseResultPagination {

  private final Integer offset;
  private final Integer limit;
  private final Integer results;

  public ApiV3ResponseResultPaginationImpl(final Integer offset, final Integer limit, final Integer results) {
    this.offset = offset;
    this.limit = limit;
    this.results = results;
  }

  @Override
  public Integer getOffset() {
    return offset;
  }

  @Override
  public Integer getLimit() {
    return limit;
  }

  @Override
  public Integer getResults() {
    return results;
  }
}
