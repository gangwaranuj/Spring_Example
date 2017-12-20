package com.workmarket.service.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CreateNewWorkerResponse.Builder.class)
public class CreateNewWorkerResponse {
  private final String userNumber;
  private final String email;

  public CreateNewWorkerResponse(
      final String userNumber,
      final String email) {
    this.userNumber = userNumber;
    this.email = email;
  }

  private CreateNewWorkerResponse(Builder builder) {
    this.userNumber = builder.userNumber;
    this.email = builder.email;
  }

  public String getUserNumber() {
    return userNumber;
  }

  public String getEmail() {
    return email;
  }

  public static class Builder {
    private String userNumber;
    private String email;

    @JsonProperty("userNumber")
    public Builder userNumber(String userNumber) {
      this.userNumber = userNumber;
      return this;
    }

    @JsonProperty("email")
    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder fromPrototype(CreateNewWorkerResponse prototype) {
      userNumber = prototype.userNumber;
      email = prototype.email;
      return this;
    }

    public CreateNewWorkerResponse build() {
      return new CreateNewWorkerResponse(this);
    }
  }
}
