package com.workmarket.api.v3.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class ApiReportData {
  private final List<Map<String, Object>> results;
  private final Integer totalCount;
  private final List<CustomField> customFields;

  public ApiReportData(
      final List<Map<String, Object>> results,
      final Integer totalCount,
      final List<CustomField> customFields) {
    this.results = ImmutableList.copyOf(results);
    this.totalCount = totalCount;
    this.customFields = ImmutableList.copyOf(customFields);
  }

  public ApiReportData(
      final List<Map<String, Object>> results,
      final Integer totalCount) {
    this.results = ImmutableList.copyOf(results);
    this.totalCount = totalCount;
    this.customFields = ImmutableList.of();
  }

  public static ApiReportData copyOf(final ApiReportData apiReportData) {
    return new ApiReportData(
        apiReportData.getResults(),
        apiReportData.getTotalCount(),
        apiReportData.getCustomFields());
  }

  public List<Map<String, Object>> getResults() {
    return results;
  }

  public Integer getTotalCount() {
    return totalCount;
  }

  public List<CustomField> getCustomFields() {
    return customFields;
  }

  public Map<String, String> getMapCustomFieldSchemaNameToActualName() {
    final Map<String, String> mapCustomFieldSchemaNameToActualName = Maps.newHashMap();
    for (ApiReportData.CustomField customField : getCustomFields()) {
      mapCustomFieldSchemaNameToActualName.put(customField.getSchemaName(), customField.getActualName());
    }
    return mapCustomFieldSchemaNameToActualName;
  }

  public static class CustomField {
    private final Long id;
    private final String schemaName;
    private final String actualName;

    public CustomField(final Long id, final String schemaName, final String actualName) {
      this.id = id;
      this.schemaName = schemaName;
      this.actualName = actualName;
    }

    public Long getId() {
      return id;
    }

    public String getSchemaName() {
      return schemaName;
    }

    public String getActualName() {
      return actualName;
    }
  }
}
