package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "EmailNotConfirmed")
@JsonDeserialize(builder = ApiEmailNotConfirmedDTO.Builder.class)
public class ApiEmailNotConfirmedDTO {
  private final String userNumber;

  public ApiEmailNotConfirmedDTO(final Builder builder) {
    this.userNumber = builder.userNumber;
  }

  @ApiModelProperty(name = "userNumber")
  @JsonProperty("userNumber")
  public String getUserNumber() {
    return userNumber;
  }

  public static class Builder implements AbstractBuilder<ApiEmailNotConfirmedDTO> {
    private String userNumber;

    public Builder() { }

    public Builder(final ApiEmailNotConfirmedDTO dto) {
      this.userNumber = dto.userNumber;
    }

    @JsonProperty("userNumber")
    public Builder setUserNumber(final String userNumber) {
      this.userNumber = userNumber;
      return this;
    }

    @Override
    public ApiEmailNotConfirmedDTO build() {
      return new ApiEmailNotConfirmedDTO(this);
    }
  }
}
