package com.workmarket.api.v2.model;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.model.CountryEnum;
import com.workmarket.api.model.TaxEntityTypeCodeEnum;
import com.workmarket.api.model.TaxVerificationStatusCodeEnum;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;

import org.apache.commons.lang.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "TaxInfo")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = TaxInfoApiDTO.Builder.class)
public class TaxInfoApiDTO {
  private final boolean active;
  private final Long activeDate;
  private final Long inactiveDate;
  private final Long effectiveDate;
  private final TaxVerificationStatusCodeEnum taxVerificationStatusCode;
  private final TaxEntityTypeCodeEnum taxEntityTypeCode;
  private final boolean agreeToTerms;
  private final String businessAsName;
  private final String companyName;
  private final String signature;
  private final Long signatureDate;
  private final String countryOfIncorporation;
  private final boolean foreignStatusAccepted;
  private final String address;
  private final String city;
  private final String state;
  private final String postalCode;
  private final CountryEnum country;
  private final boolean business;
  private final String taxNumber;
  private final String firstName;
  private final String middleName;
  private final String lastName;

  @ApiModelProperty(name = "active")
  @JsonProperty("active")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public boolean isActive() {
    return active;
  }

  @ApiModelProperty(name = "activeDate")
  @JsonProperty("activeDate")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public Long getActiveDate() {
    return activeDate;
  }

  @ApiModelProperty(name = "inactiveDate")
  @JsonProperty("inactiveDate")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public Long getInactiveDate() {
    return inactiveDate;
  }

  @ApiModelProperty(name = "effectiveDate")
  @JsonProperty("effectiveDate")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public Long getEffectiveDate() {
    return effectiveDate;
  }

  @ApiModelProperty(name = "taxVerificationStatusCode")
  @JsonProperty("taxVerificationStatusCode")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public TaxVerificationStatusCodeEnum getTaxVerificationStatusCode() {
    return taxVerificationStatusCode;
  }

  @ApiModelProperty(name = "taxEntityTypeCode")
  @JsonProperty("taxEntityTypeCode")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public TaxEntityTypeCodeEnum getTaxEntityTypeCode() {
    return taxEntityTypeCode;
  }

  @ApiModelProperty(name = "agreeToTerms")
  @JsonProperty("agreeToTerms")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public boolean isAgreeToTerms() {
    return agreeToTerms;
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

  @ApiModelProperty(name = "signature")
  @JsonProperty("signature")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getSignature() {
    return signature;
  }

  @ApiModelProperty(name = "signatureDate")
  @JsonProperty("signatureDate")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public Long getSignatureDate() {
    return signatureDate;
  }

  @ApiModelProperty(name = "countryOfIncorporation")
  @JsonProperty("countryOfIncorporation")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getCountryOfIncorporation() {
    return countryOfIncorporation;
  }

  @ApiModelProperty(name = "foreignStatusAccepted")
  @JsonProperty("foreignStatusAccepted")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public boolean isForeignStatusAccepted() {
    return foreignStatusAccepted;
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

  @ApiModelProperty(name = "country")
  @JsonProperty("country")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public CountryEnum getCountry() {
    return country;
  }

  @ApiModelProperty(name = "business")
  @JsonProperty("business")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public boolean isBusiness() {
    return business;
  }

  @ApiModelProperty(name = "taxNumber")
  @JsonProperty("taxNumber")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getTaxNumber() {
    return taxNumber;
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

  public String getFullName() {
    final ImmutableList.Builder builder = ImmutableList.builder();

    if (StringUtils.isNotBlank(firstName)) {
      builder.add(firstName);
    }
    if (StringUtils.isNotBlank(middleName)) {
      builder.add(middleName);
    }
    if (StringUtils.isNotBlank(lastName)) {
      builder.add(lastName);
    }

    return Joiner.on(" ").join(builder.build());
  }

  public TaxInfoApiDTO(final Builder builder) {
    this.active = builder.active;
    this.activeDate = builder.activeDate;
    this.address = builder.address;
    this.agreeToTerms = builder.agreeToTerms;
    this.business = builder.business;
    this.businessAsName = builder.businessAsName;
    this.city = builder.city;
    this.companyName = builder.companyName;
    this.country = builder.country;
    this.countryOfIncorporation = builder.countryOfIncorporation;
    this.effectiveDate = builder.effectiveDate;
    this.firstName = builder.firstName;
    this.foreignStatusAccepted = builder.foreignStatusAccepted;
    this.middleName = builder.middleName;
    this.lastName = builder.lastName;
    this.state = builder.state;
    this.postalCode = builder.postalCode;
    this.taxEntityTypeCode = builder.taxEntityTypeCode;
    this.taxVerificationStatusCode = builder.taxVerificationStatusCode;
    this.taxNumber = builder.taxNumber;
    this.signature = builder.signature;
    this.signatureDate = builder.signatureDate;
    this.inactiveDate = builder.inactiveDate;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder implements AbstractBuilder<TaxInfoApiDTO> {
    private boolean active;
    private Long activeDate;
    private Long inactiveDate;
    private Long effectiveDate;
    private TaxVerificationStatusCodeEnum taxVerificationStatusCode;
    private TaxEntityTypeCodeEnum taxEntityTypeCode;
    private boolean agreeToTerms;
    private String businessAsName;
    private String companyName;
    private String signature;
    private Long signatureDate;
    private String countryOfIncorporation;
    private boolean foreignStatusAccepted;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private CountryEnum country;
    private boolean business;
    private String taxNumber;
    private String firstName;
    private String middleName;
    private String lastName;

    public Builder() {
    }

    @JsonProperty("active")
    public Builder setActive(boolean active) {
      this.active = active;
      return this;
    }

    @JsonProperty("activeDate")
    public Builder setActiveDate(Long activeDate) {
      this.activeDate = activeDate;
      return this;
    }

    @JsonProperty("inactiveDate")
    public Builder setInactiveDate(Long inactiveDate) {
      this.inactiveDate = inactiveDate;
      return this;
    }

    @JsonProperty("effectiveDate")
    public Builder setEffectiveDate(Long effectiveDate) {
      this.effectiveDate = effectiveDate;
      return this;
    }

    @JsonProperty("taxVerificationStatusCode")
    public Builder setTaxVerificationStatusCode(TaxVerificationStatusCodeEnum taxVerificationStatusCode) {
      this.taxVerificationStatusCode = taxVerificationStatusCode;
      return this;
    }

    @JsonProperty("taxEntityTypeCode")
    public Builder setTaxEntityTypeCode(TaxEntityTypeCodeEnum taxEntityTypeCode) {
      this.taxEntityTypeCode = taxEntityTypeCode;
      return this;
    }

    @JsonProperty("agreeToTerms")
    public Builder setAgreeToTerms(boolean agreeToTerms) {
      this.agreeToTerms = agreeToTerms;
      return this;
    }

    @JsonProperty("businessAsName")
    public Builder setBusinessAsName(String businessAsName) {
      this.businessAsName = businessAsName;
      return this;
    }

    @JsonProperty("companyName")
    public Builder setCompanyName(String companyName) {
      this.companyName = companyName;
      return this;
    }

    @JsonProperty("signature")
    public Builder setSignature(String signature) {
      this.signature = signature;
      return this;
    }

    @JsonProperty("signatureDate")
    public Builder setSignatureDate(Long signatureDate) {
      this.signatureDate = signatureDate;
      return this;
    }

    @JsonProperty("countryOfIncorporation")
    public Builder setCountryOfIncorporation(String countryOfIncorporation) {
      this.countryOfIncorporation = countryOfIncorporation;
      return this;
    }

    @JsonProperty("foreignStatusAccepted")
    public Builder setForeignStatusAccepted(boolean foreignStatusAccepted) {
      this.foreignStatusAccepted = foreignStatusAccepted;
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

    @JsonProperty("postalCode")
    public Builder setPostalCode(String postalCode) {
      this.postalCode = postalCode;
      return this;
    }

    @JsonProperty("country")
    public Builder setCountry(CountryEnum country) {
      this.country = country;
      return this;
    }

    @JsonProperty("business")
    public Builder setBusiness(boolean business) {
      this.business = business;
      return this;
    }

    @JsonProperty("taxNumber")
    public Builder setTaxNumber(String taxNumber) {
      this.taxNumber = taxNumber;
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

    @Override
    public TaxInfoApiDTO build() {
      return new TaxInfoApiDTO(this);
    }
  }
}