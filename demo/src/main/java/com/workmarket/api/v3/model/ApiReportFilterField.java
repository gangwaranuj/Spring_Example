package com.workmarket.api.v3.model;

public class ApiReportFilterField {
  private final String fieldName;
  private final ApiReportDatatype fieldType;

  public ApiReportFilterField(final String fieldName, final ApiReportDatatype fieldType) {
    this.fieldName = fieldName;
    this.fieldType = fieldType;
  }

  public String getFieldName() {
    return fieldName;
  }

  public ApiReportDatatype getFieldType() {
    return fieldType;
  }

}
