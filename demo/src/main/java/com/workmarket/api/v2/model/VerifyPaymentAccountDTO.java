package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Verify Payment Account DTO.
 */
@ApiModel(value = "VerifyPaymentAccount")
@JsonDeserialize(builder = VerifyPaymentAccountDTO.Builder.class)
public class VerifyPaymentAccountDTO {
  private final String amount1;
  private final String amount2;


  public VerifyPaymentAccountDTO(final String amount1, final String amount2) {
    this.amount1 = amount1;
    this.amount2 = amount2;
  }

  public VerifyPaymentAccountDTO(final Builder builder) {
    this.amount1 = builder.amount1;
    this.amount2 = builder.amount2;
  }

  @ApiModelProperty(name = "Deposit Amount 1", example = ".02")
  @JsonProperty("amount1")
  public String getAmount1() {
    return amount1;
  }

  @ApiModelProperty(name = "Deposit Amount 2", example = ".05")
  @JsonProperty("amount2")
  public String getAmount2() {
    return amount2;
  }

  public static class Builder implements AbstractBuilder<VerifyPaymentAccountDTO> {
    private String amount1;
    private String amount2;

    public Builder() { }

    @JsonProperty("amount1")
    public Builder setAmount1(final String amount1) {
      this.amount1 = amount1;
      return this;
    }

    @JsonProperty("amount2")
    public Builder setAmount2(final String amount2) {
      this.amount2 = amount2;
      return this;
    }

    @Override
    public VerifyPaymentAccountDTO build() {
      return new VerifyPaymentAccountDTO(this);
    }
  }
}
