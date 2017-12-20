package com.workmarket.api.v3.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ReportResponse")
@JsonDeserialize(builder = ApiReportDTO.Builder.class)
public class ApiReportResponseDTO {
  private final String name;
  private final String description;
  private final Map<String, String> schema;
  private final List<Map<String, Object>> results;

  private ApiReportResponseDTO(final Builder builder) {
    name = builder.name;
    description = builder.description;
    schema = builder.schema;
    results = builder.results;
  }

  @ApiModelProperty(name = "name")
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @ApiModelProperty(name = "description")
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @ApiModelProperty(name = "schema")
  @JsonProperty("schema")
  public Map<String, String> getSchema() {
    return schema;
  }

  @ApiModelProperty(name = "results")
  @JsonProperty("results")
  public List<Map<String, Object>> getResults() {
    return results;
  }

  public static final class Builder {
    private String name;
    private String description;
    private Map<String, String> schema;
    private List<Map<String, Object>> results;

    public Builder() {}

    public Builder(final ApiReportResponseDTO copy) {
      this.name = copy.name;
      this.description = copy.description;
      this.schema = copy.schema;
      this.results = copy.results;
    }

    public Builder withName(final String name) {
      this.name = name;
      return this;
    }

    public Builder withDescription(final String description) {
      this.description = description;
      return this;
    }

    public Builder withSchema(final Map<String, String> schema) {
      this.schema = ImmutableMap.copyOf(schema);
      return this;
    }

    public Builder withResults(final List<Map<String, Object>> results) {
      this.results = ImmutableList.copyOf(results);
      return this;
    }

    public ApiReportResponseDTO build() {
      return new ApiReportResponseDTO(this);
    }
  }
}
