package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import com.workmarket.domains.onboarding.model.Qualification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Job title DTO.
 */
@ApiModel(value = "JobTitle")
@JsonDeserialize(builder = ApiJobTitleDTO.Builder.class)
public class ApiJobTitleDTO {
  private final String uuid;
  private final String name;
  private final Qualification.Type type;

  public ApiJobTitleDTO(final String uuid, final String name) {
    this.uuid = uuid;
    this.name = name;
    this.type = Qualification.Type.JOBTITLE;
  }

  public ApiJobTitleDTO(final Builder builder) {
    this.uuid = builder.uuid;
    this.name = builder.name;
    this.type = builder.type;
  }

  @ApiModelProperty(name = "Unique UUID", example = "e44cc935-0813-4198-baa3-b3cb70a32e7e")
  @JsonProperty("uuid")
  public String getUuid() {
    return uuid;
  }

  @ApiModelProperty(name = "Job title name", example = "Senior VP")
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @ApiModelProperty(value = "Qualification type", example = "JOBTITLE")
  @JsonProperty("type")
  public Qualification.Type getType() {
    return type;
  }

  public static class Builder implements AbstractBuilder<ApiJobTitleDTO> {
    private String uuid;
    private String name;
    private Qualification.Type type = Qualification.Type.JOBTITLE;

    public Builder() { }

    @JsonProperty("uuid")
    public Builder setUuid(final String uuid) {
      this.uuid = uuid;
      return this;
    }

    @JsonProperty("name")
    public Builder setName(final String name) {
      this.name = name;
      return this;
    }

    @JsonProperty("type")
    public Builder setType(final Qualification.Type type) {
      this.type = type;
      return this;
    }

    @Override
    public ApiJobTitleDTO build() {
      return new ApiJobTitleDTO(this);
    }
  }
}
