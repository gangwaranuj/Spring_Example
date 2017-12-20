package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "GroupRequirement", description = "A worker group requirement")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = ApiGroupRequirementDTO.Builder.class)
public class ApiGroupRequirementDTO {
  private final String name;
  private final String typeName;
  private final String url;
  private final boolean isMet;

  private ApiGroupRequirementDTO(final Builder builder) {
    this.name = builder.name;
    this.typeName = builder.typeName;
    this.url = builder.url;
    this.isMet = builder.isMet;
  }

  @ApiModelProperty(name = "name", value = "Requirement name")
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @ApiModelProperty(name = "typeName", value = "Requirement type name")
  @JsonProperty("typeName")
  public String getTypeName() {
    return typeName;
  }

  @ApiModelProperty(name = "url", value = "Requirement URL action")
  @JsonProperty("url")
  public String getUrl() {
    return url;
  }

  @ApiModelProperty(name = "isMet", value = "If this requirement is met by the current user")
  @JsonProperty("isMet")
  public boolean isMet() {
    return isMet;
  }

  public static final class Builder {
    private String name;
    private String typeName;
    private String url;
    private boolean isMet;

    @JsonProperty("name")
    public Builder withName(final String name) {
      this.name = name;
      return this;
    }

    @JsonProperty("typeName")
    public Builder withTypeName(final String typeName) {
      this.typeName = typeName;
      return this;
    }

    @JsonProperty("url")
    public Builder withUrl(final String url) {
      this.url = url;
      return this;
    }

    @JsonProperty("isMet")
    public Builder withIsMet(final boolean isMet) {
      this.isMet = isMet;
      return this;
    }

    public ApiGroupRequirementDTO build() {
      return new ApiGroupRequirementDTO(this);
    }
  }
}
