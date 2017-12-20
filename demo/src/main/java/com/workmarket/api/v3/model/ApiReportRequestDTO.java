package com.workmarket.api.v3.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import java.util.AbstractMap;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "GetReportRequest")
public class ApiReportRequestDTO {

  private String filterField1;
  private String filterField1Value;

  private String filterField2;
  private String filterField2Value;

  @Min(0)
  private Integer offset = 0;
  @Max(100)
  private Integer limit = 100;

  public ApiReportRequestDTO() {

  }

  public String getFilterField1() {
    return filterField1;
  }

  public String getFilterField1Value() {
    return filterField1Value;
  }

  public String getFilterField2() {
    return filterField2;
  }

  public String getFilterField2Value() {
    return filterField2Value;
  }

  public Integer getOffset() {
    return offset;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setFilterField1(String filterField1) {
    this.filterField1 = filterField1;
  }

  public void setFilterField1Value(String filterField1Value) {
    this.filterField1Value = filterField1Value;
  }

  public void setFilterField2(String filterField1) {
    this.filterField2 = filterField1;
  }

  public void setFilterField2Value(String filterField1Value) {
    this.filterField2Value = filterField1Value;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public List<AbstractMap.SimpleEntry<String, String>> getFilterFields() {
    return ImmutableList.of(
        new AbstractMap.SimpleEntry<>(filterField1, filterField1Value),
        new AbstractMap.SimpleEntry<>(filterField2, filterField2Value)
    );
  }

  public Set<String> getFilterFieldKeySet() {
    final Set<String> filterFieldKeySet = Sets.newHashSet();
    filterFieldKeySet.add(filterField1);
    filterFieldKeySet.add(filterField2);
    filterFieldKeySet.remove(null);
    return filterFieldKeySet;
  }

}
