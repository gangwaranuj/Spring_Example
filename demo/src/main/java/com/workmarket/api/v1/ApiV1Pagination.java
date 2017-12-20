package com.workmarket.api.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.workmarket.api.ApiSwaggerModelConverter;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pagination")
public class ApiV1Pagination<T> {

  @ApiModelProperty(value = "total_results")
  @JsonProperty("total_results")
  private final Integer totalResults;

  @ApiModelProperty(value = "count")
  @JsonProperty("count")
  private final Integer count;

  @ApiModelProperty(value = "start")
  @JsonProperty("start")
  private final Integer start;

  @ApiModelProperty(value = "limit")
  @JsonProperty("limit")
  private final Integer limit;

  @ApiModelProperty(value = "data", reference = ApiSwaggerModelConverter.REFERENCE_GENERIC_LIST)
  @JsonProperty("data")
  private final List<T> data;

  public ApiV1Pagination(Integer totalResults, Integer count, Integer start, Integer limit, List<T> data) {
    this.totalResults = totalResults;
    this.count = count;
    this.start = start;
    this.limit = limit;
    this.data = data;
  }

  public Integer getTotalResults() {
    return totalResults;
  }
	public Integer getCount() {
    return count;
  }
  public Integer getStart() {
    return start;
  }
  public Integer getLimit() {
    return limit;
  }
  public List<T> getData() {
    return data;
  }
}
