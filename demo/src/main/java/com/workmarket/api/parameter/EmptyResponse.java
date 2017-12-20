package com.workmarket.api.parameter;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;

import io.swagger.annotations.ApiModel;

@ApiModel(
    value = "EmptyResponse",
    description = "Signals an empty response. Please see HTTP status codes for success or failure.")
@JsonDeserialize(builder = EmptyResponse.Builder.class)
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
/**
 * An empty DTO with no fields. Use this class as the response object type in cases where nothing is returned
 * This aids in documentation and if we decide to extend the response object later.
 */
public class EmptyResponse {
  public EmptyResponse() {
  }

  public static class Builder implements AbstractBuilder<EmptyResponse> {
    @Override
    public EmptyResponse build() {
      return new EmptyResponse();
    }
  }
}
