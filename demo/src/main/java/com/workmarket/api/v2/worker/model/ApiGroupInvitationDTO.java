package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "GroupInvitation", description = "A worker group membership invitation")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = ApiGroupInvitationDTO.Builder.class)
public class ApiGroupInvitationDTO {
  private final String requestDate;
  private final String requesterFullName;

  private ApiGroupInvitationDTO(final Builder builder) {
    this.requestDate = builder.requestDate;
    this.requesterFullName = builder.requesterFullName;
  }

  @ApiModelProperty(name = "requestDate", value = "Request date")
  @JsonProperty("requestDate")
  public String getRequestDate() {
    return requestDate;
  }

  @ApiModelProperty(name = "requesterFullName", value = "Full name of the user who made the request")
  @JsonProperty("requesterFullName")
  public String getRequesterFullName() {
    return requesterFullName;
  }

  public static final class Builder {
    private String requestDate;
    private String requesterFullName;

    @JsonProperty("requestDate")
    public Builder withRequestDate(final String requestDate) {
      this.requestDate = requestDate;
      return this;
    }

    @JsonProperty("requesterFullName")
    public Builder withRequesterFullName(final String requesterFullName) {
      this.requesterFullName = requesterFullName;
      return this;
    }

    public ApiGroupInvitationDTO build() {
      return new ApiGroupInvitationDTO(this);
    }
  }
}
