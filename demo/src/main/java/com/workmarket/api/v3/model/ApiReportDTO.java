package com.workmarket.api.v3.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Report")
@JsonDeserialize(builder = ApiReportDTO.Builder.class)
public class ApiReportDTO {

  private final String title;
  private final String description;
  private final String createdAt;
  private final String updatedAt;
  private final String url;
  private final int id;
  private final List<List<String>> data;

  private ApiReportDTO(Builder builder) {
    title = builder.title;
    description = builder.description;
    createdAt = builder.createdAt;
    updatedAt = builder.updatedAt;
    url = builder.url;
    id = builder.id;
    data = builder.data;
  }


  @ApiModelProperty(name = "title")
	@JsonProperty("title")
  public String getTitle() {
    return title;
  }

  @ApiModelProperty(name = "description")
	@JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @ApiModelProperty(name = "createdAt")
  @JsonProperty("createdAt")
  public String getCreatedAt() {
    return createdAt;
  }

  @ApiModelProperty(name = "updatedAt")
  @JsonProperty("updatedAt")
  public String getUpdatedAt() {
    return updatedAt;
  }

  @ApiModelProperty(name = "url")
  @JsonProperty("url")
  public String getUrl() {
    return url;
  }

  @ApiModelProperty(name = "id")
  @JsonProperty("id")
  public int getId() {
    return id;
  }

  @ApiModelProperty(name = "data")
  @JsonProperty("data")
  public List<List<String>> getData() {
    return data;
  }

  public static final class Builder {
    private String title;
    private String description;
    private String createdAt;
    private String updatedAt;
    private String url;
    private int id;
    private List<List<String>> data;

    public Builder() {
    }

    @JsonProperty("title")
    public Builder withTitle(final String title) {
      this.title = title;
      return this;
    }

    @JsonProperty("description")
    public Builder withDescription(final String description) {
      this.description = description;
      return this;
    }

    @JsonProperty("createdAt")
    public Builder withCreatedAt(final String createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    @JsonProperty("updatedAt")
    public Builder withUpdatedAt(final String updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }
    
    @JsonProperty("url")
    public Builder withUrl(final String url) {
      this.url = url;
      return this;
    }

    @JsonProperty("id")
    public Builder withId(final int id) {
      this.id = id;
      return this;
    }

    @JsonProperty("data")
    public Builder withData(final List<List<String>> data) {
      this.data = data;
      return this;
    }

    public ApiReportDTO build() {
      return new ApiReportDTO(this);
    }
  }
}
