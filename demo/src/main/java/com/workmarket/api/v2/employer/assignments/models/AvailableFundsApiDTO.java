package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "AvailableFunds")
@JsonDeserialize(builder = AvailableFundsApiDTO.Builder.class)
public class AvailableFundsApiDTO {
    private final BigDecimal spendingLimit;
    private final BigDecimal aplLimit;

    private AvailableFundsApiDTO(Builder builder) {
        this.spendingLimit = builder.spendingLimit;
        this.aplLimit = builder.aplLimit;
    }

    @ApiModelProperty(name = "aplLimit")
    @JsonProperty("aplLimit")
    public BigDecimal getAplLimit() {
        return aplLimit;
    }

    @ApiModelProperty(name = "spendingLimit")
    @JsonProperty("spendingLimit")
    public BigDecimal getSpendingLimit() {
        return spendingLimit;
    }

    public static class Builder implements AbstractBuilder<AvailableFundsApiDTO> {
        private BigDecimal spendingLimit;
        private BigDecimal aplLimit;

        public Builder(AvailableFundsApiDTO AvailableFundsApiDTO) {
            this.spendingLimit = AvailableFundsApiDTO.spendingLimit;
            this.aplLimit = AvailableFundsApiDTO.aplLimit;
        }

        public Builder() {}

        @JsonProperty("spendingLimit") public Builder setSpendingLimit(BigDecimal spendingLimit) {
            this.spendingLimit = spendingLimit;
            return this;
        }

        @JsonProperty("aplLimit") public Builder setAplLimit(BigDecimal aplLimit) {
            this.aplLimit = aplLimit;
            return this;
        }

        @Override
        public AvailableFundsApiDTO build() {
            return new AvailableFundsApiDTO(this);
        }
    }
}
