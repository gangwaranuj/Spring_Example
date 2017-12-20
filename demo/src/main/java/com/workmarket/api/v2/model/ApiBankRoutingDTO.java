package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import com.workmarket.domains.model.banking.BankRouting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Api Bank Routing DTO.
 */
@ApiModel(value = "BankRouting")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ApiBankRoutingDTO.Builder.class)
public class ApiBankRoutingDTO {

    private final Long id;
    private final String city;
    private final String state;
    private final String country;
    private final String address;
    private final String bankName;
    private final String postalCode;
    private final String routingNumber;

    private ApiBankRoutingDTO(final Builder builder) {
        this.id = builder.id;
        this.city = builder.city;
        this.state = builder.state;
        this.country = builder.country;
        this.address = builder.address;
        this.bankName = builder.bankName;
        this.postalCode = builder.postalCode;
        this.routingNumber = builder.routingNumber;
    }

    @JsonProperty("id")
    @ApiModelProperty(name = "id")
    public Long getId() {
        return id;
    }

    @JsonProperty("city")
    @ApiModelProperty(name = "city")
    public String getCity() {
        return city;
    }

    @JsonProperty("state")
    @ApiModelProperty(name = "state")
    public String getState() {
        return state;
    }

    @JsonProperty("country")
    @ApiModelProperty(name = "country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("address")
    @ApiModelProperty(name = "address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("bankName")
    @ApiModelProperty(name = "bankName")
    public String getBankName() {
        return bankName;
    }

    @JsonProperty("postalCode")
    @ApiModelProperty(name = "postalCode")
    public String getPostalCode() {
        return postalCode;
    }

    @JsonProperty("routingNumber")
    @ApiModelProperty(name = "routingNumber")
    public String getRoutingNumber() {
        return routingNumber;
    }

    public static class Builder implements AbstractBuilder<ApiBankRoutingDTO> {
        private Long id;
        private String city;
        private String state;
        private String country;
        private String address;
        private String bankName;
        private String postalCode;
        private String routingNumber;

        public Builder() { }

        public Builder(final ApiBankRoutingDTO dto) {
            this.id = dto.id;
            this.city = dto.city;
            this.state = dto.state;
            this.country = dto.country;
            this.address = dto.address;
            this.bankName = dto.bankName;
            this.postalCode = dto.postalCode;
            this.routingNumber = dto.routingNumber;
        }

        public Builder(final BankRouting entity) {
            this.id = entity.getId();
            this.city = entity.getCity();
            this.state = entity.getState();
            this.address = entity.getAddress();
            this.bankName = entity.getBankName();
            this.postalCode = entity.getPostalCode();
            this.routingNumber = entity.getRoutingNumber();

            if (entity.getCountry() != null) {
                this.country = entity.getCountry().getId();
            }
        }

        @JsonProperty("id")
        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        @JsonProperty("city")
        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        @JsonProperty("state")
        public Builder setState(String state) {
            this.state = state;
            return this;
        }

        @JsonProperty("country")
        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        @JsonProperty("address")
        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        @JsonProperty("bankName")
        public Builder setBankName(String bankName) {
            this.bankName = bankName;
            return this;
        }

        @JsonProperty("postalCode")
        public Builder setPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        @JsonProperty("routingNumber")
        public Builder setRoutingNumber(String routingNumber) {
            this.routingNumber = routingNumber;
            return this;
        }

        @Override
        public ApiBankRoutingDTO build() {
            return new ApiBankRoutingDTO(this);
        }
    }
}