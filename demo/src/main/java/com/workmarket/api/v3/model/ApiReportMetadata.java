package com.workmarket.api.v3.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class ApiReportMetadata {
  private final Long id;
  private final String name;
  private final String description;
  private final ApiReportSource source;
  private final List<ApiReportField> schema;
  private final List<ApiReportJoin> joins;

  private ApiReportMetadata(final Builder builder) {
    id = builder.id;
    name = builder.name;
    description = builder.description;
    source = builder.source;
    schema = builder.schema;
    joins = builder.joins.build();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public ApiReportSource getSource() {
    return source;
  }

  public List<ApiReportField> getSchema() {
    return schema;
  }

  public List<ApiReportJoin> getJoins() {
    return joins;
  }

  public List<String> getFieldNames() {
    final List<String> fieldNames = Lists.newArrayList();
    for (ApiReportField field : getSchema()) {
      fieldNames.add(field.getFieldName());
    }
    return fieldNames;
  }

  public Map<String, ApiReportField> getSchemaMap() {
    final Map<String, ApiReportField> schemaMap = Maps.newHashMap();
    for (final ApiReportField apiReportField : getSchema()) {
      schemaMap.put(apiReportField.getFieldName(), apiReportField);
    }
    return schemaMap;
  }

  public static class ApiReportSource {
    final String service;
    final String callable;
    final Class[] callableParameterTypes;
    final String callableInputConverter;
    final String callableOutputConverter;
    final Class[] callableOutputConverterParameterTypes;
    final List<String> filterFieldWhiteList;
    final List<String> filterFieldRequired;

    private ApiReportSource(Builder builder) {
      service = builder.service;
      callable = builder.callable;
      callableParameterTypes = builder.callableParameterTypes;
      callableInputConverter = builder.callableInputConverter;
      callableOutputConverter = builder.callableOutputConverter;
      callableOutputConverterParameterTypes = builder.callableOutputConverterParameterTypes;
      filterFieldWhiteList = builder.filterFieldWhiteList;
      filterFieldRequired = builder.filterFieldRequired;
    }

    public String getService() {
      return service;
    }

    public String getCallable() {
      return callable;
    }

    public Class[] getCallableParameterTypes() {
      return callableParameterTypes;
    }

    public String getCallableInputConverter() {
      return callableInputConverter;
    }

    public String getCallableOutputConverter() {
      return callableOutputConverter;
    }

    public Class[] getCallableOutputConverterParameterTypes() {
      return callableOutputConverterParameterTypes;
    }

    public List<String> getFilterFieldWhiteList() {
      return filterFieldWhiteList;
    }

    public List<String> getFilterFieldRequired() {
      return filterFieldRequired;
    }

    public static final class Builder {
      private String service;
      private String callable;
      private Class[] callableParameterTypes;
      private String callableInputConverter;
      private String callableOutputConverter;
      private Class[] callableOutputConverterParameterTypes;
      private List<String> filterFieldWhiteList;
      private List<String> filterFieldRequired;

      public Builder() {}

      public Builder(ApiReportSource copy) {
        this.service = copy.getService();
        this.callable = copy.getCallable();
        this.callableParameterTypes = copy.getCallableParameterTypes();
        this.callableInputConverter = copy.getCallableInputConverter();
        this.callableOutputConverter = copy.getCallableOutputConverter();
        this.callableOutputConverterParameterTypes = copy.getCallableOutputConverterParameterTypes();
        this.filterFieldWhiteList = copy.getFilterFieldWhiteList();
        this.filterFieldRequired = copy.getFilterFieldRequired();
      }

      public Builder withService(String service) {
        this.service = service;
        return this;
      }

      public Builder withCallable(String callable) {
        this.callable = callable;
        return this;
      }

      public Builder withCallableParameterTypes(Class[] callableParameterTypes) {
        this.callableParameterTypes = callableParameterTypes;
        return this;
      }

      public Builder withCallableInputConverter(String callableInputConverter) {
        this.callableInputConverter = callableInputConverter;
        return this;
      }

      public Builder withCallableOutputConverter(String callableOutputConverter) {
        this.callableOutputConverter = callableOutputConverter;
        return this;
      }

      public Builder withCallableOutputConverterParameterTypes(Class[] callableOutputConverterParameterTypes) {
        this.callableOutputConverterParameterTypes = callableOutputConverterParameterTypes;
        return this;
      }

      public Builder withFilterFieldWhiteList(List<String> filterFieldWhiteList) {
        this.filterFieldWhiteList = filterFieldWhiteList;
        return this;
      }

      public Builder withFilterFieldRequired(List<String> filterFieldRequired) {
        this.filterFieldRequired = filterFieldRequired;
        return this;
      }

      public ApiReportSource build() {
        return new ApiReportSource(this);
      }
    }
  }

  public static class ApiReportJoin {
    final String name;
    final String foreignKey;
    final String service;
    final String callable;
    final String callableMerger;
    final List<Long> filterIds;

    public ApiReportJoin(
        final String name,
        final String foreignKey,
        final String service,
        final String callable,
        final String callableMerger,
        final List<Long> filterIds) {
      this.name = name;
      this.foreignKey = foreignKey;
      this.service = service;
      this.callable = callable;
      this.callableMerger = callableMerger;
      this.filterIds = filterIds;
    }

    public String getName() {
      return name;
    }

    public String getForeignKey() {
      return foreignKey;
    }

    public String getService() {
      return service;
    }

    public String getCallable() {
      return callable;
    }

    public String getCallableMerger() {
      return callableMerger;
    }

    public List<Long> getFilterIds() {
      return filterIds;
    }
  }

  public static class ApiReportField {
    private final String fieldName;
    private final ApiReportDatatype fieldType;

    public ApiReportField(final String fieldName, final ApiReportDatatype fieldType) {
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

  public static final class Builder {
    private Long id;
    private String name;
    private String description;
    private ApiReportSource source;
    private List<ApiReportField> schema;
    private ImmutableList.Builder<ApiReportJoin> joins = ImmutableList.builder();

    public Builder() {}

    public Builder(final ApiReportMetadata copy) {
      this.id = copy.id;
      this.name = copy.name;
      this.description = copy.description;
      this.source = copy.source;
      this.schema = copy.schema;
      this.joins.addAll(copy.joins);
    }

    public Builder withId(final Long id) {
      this.id = id;
      return this;
    }

    public Builder withName(final String name) {
      this.name = name;
      return this;
    }

    public Builder withDescription(final String description) {
      this.description = description;
      return this;
    }

    public Builder withSource(final ApiReportSource source) {
      this.source = source;
      return this;
    }

    public Builder withSchema(final List<ApiReportField> schema) {
      this.schema = ImmutableList.copyOf(schema);
      return this;
    }

    public Builder addJoin(final ApiReportJoin join) {
      this.joins.add(join);
      return this;
    }

    public ApiReportMetadata build() {
      return new ApiReportMetadata(this);
    }
  }
}
