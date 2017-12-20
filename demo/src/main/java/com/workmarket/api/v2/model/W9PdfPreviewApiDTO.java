package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.model.TaxEntityTypeCodeEnum;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "W9PdfPreview")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = W9PdfPreviewApiDTO.Builder.class)
public class W9PdfPreviewApiDTO {
  private String firstName;
  private String middleName;
  private String lastName;
  private String taxNumber;
  private String address;
  private String city;
  private String state;
  private String postalCode;
  private TaxEntityTypeCodeEnum taxEntityTypeCode;
  private boolean business;
  private String companyName;
  private String businessAsName;

  public W9PdfPreviewApiDTO(final Builder builder) {
    this.firstName = builder.firstName;
    this.middleName = builder.middleName;
    this.lastName = builder.lastName;
    this.taxNumber = builder.taxNumber;
    this.address = builder.address;
    this.city = builder.city;
    this.state = builder.state;
    this.postalCode = builder.postalCode;
    this.taxEntityTypeCode = builder.taxEntityTypeCode;
    this.business = builder.business;
    this.companyName = builder.taxName;
    this.businessAsName = builder.businessName;
  }

  public static W9PdfPreviewApiDTO.Builder builder() {
    return new W9PdfPreviewApiDTO.Builder();
  }

  @ApiModelProperty(name = "businessAsName")
  @JsonProperty("businessAsName")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getBusinessAsName() {
    return businessAsName;
  }

  @ApiModelProperty(name = "companyName")
  @JsonProperty("companyName")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getCompanyName() {
    return companyName;
  }

  @ApiModelProperty(name = "business")
  @JsonProperty("business")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public boolean isBusiness() {
    return business;
  }

  @ApiModelProperty(name = "firstName")
  @JsonProperty("firstName")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getFirstName() {
    return firstName;
  }

  @ApiModelProperty(name = "middleName")
  @JsonProperty("middleName")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getMiddleName() {
    return middleName;
  }

  @ApiModelProperty(name = "lastName")
  @JsonProperty("lastName")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getLastName() {
    return lastName;
  }

  @ApiModelProperty(name = "taxNumber")
  @JsonProperty("taxNumber")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getTaxNumber() {
    return taxNumber;
  }

  @ApiModelProperty(name = "address")
  @JsonProperty("address")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getAddress() {
    return address;
  }

  @ApiModelProperty(name = "city")
  @JsonProperty("city")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getCity() {
    return city;
  }

  @ApiModelProperty(name = "state")
  @JsonProperty("state")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getState() {
    return state;
  }

  @ApiModelProperty(name = "postalCode")
  @JsonProperty("postalCode")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getPostalCode() {
    return postalCode;
  }

  @ApiModelProperty(name = "taxEntityTypeCode")
  @JsonProperty("taxEntityTypeCode")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public TaxEntityTypeCodeEnum getTaxEntityTypeCode() {
    return taxEntityTypeCode;
  }

  public String getFullName() {
    return String.format("%s %s %s",
        firstName != null ? firstName : "",
        middleName != null ? middleName : "",
        lastName != null ? lastName : "");
  }

  public static class Builder implements AbstractBuilder<W9PdfPreviewApiDTO> {
    private String firstName;
    private String middleName;
    private String lastName;
    private String taxNumber;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private TaxEntityTypeCodeEnum taxEntityTypeCode = TaxEntityTypeCodeEnum.NONE;
    public boolean business;
    public String taxName;
    public String businessName;

    public Builder() {
    }

    @Override
    public W9PdfPreviewApiDTO build() {
      return new W9PdfPreviewApiDTO(this);
    }

    @JsonProperty("businessAsName")
    public Builder setBusinessName(String businessName) {
      this.businessName = businessName;
      return this;
    }

    @JsonProperty("companyName")
    public Builder setTaxName(String taxName) {
      this.taxName = taxName;
      return this;
    }

    @JsonProperty("business")
    public Builder setBusiness(boolean business) {
      this.business = business;
      return this;
    }

    @JsonProperty("firstName")
    public Builder setFirstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    @JsonProperty("middleName")
    public Builder setMiddleName(String middleName) {
      this.middleName = middleName;
      return this;
    }

    @JsonProperty("lastName")
    public Builder setLastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    @JsonProperty("taxNumberd")
    public Builder setTaxNumber(String taxNumber) {
      this.taxNumber = taxNumber;
      return this;
    }

    @JsonProperty("address")
    public Builder setAddress(String address) {
      this.address = address;
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

    @JsonProperty("id")
    public Builder setPostalCode(String postalCode) {
      this.postalCode = postalCode;
      return this;
    }

    @JsonProperty("taxEntityTypeCode")
    public Builder setTaxEntityTypeCode(TaxEntityTypeCodeEnum taxEntityTypeCode) {
      this.taxEntityTypeCode = taxEntityTypeCode;
      return this;
    }
  }
}
