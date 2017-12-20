package com.workmarket.service.business.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

// TODO - API need to move this to a better home, also interesting that its used directly in the service layer
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = CreateNewWorkerRequest.CreateNewWorkerDTOBuilder.class)
public class CreateNewWorkerRequest {
  // required user fields
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String secondaryEmail;
  private final String password;
  private final String locale;

  // optional
  private final String jobTitle;

  private final Long recruitingCampaignId;
  private final String companyName;

  // Address fields
  private final String address1;
  private final String address2;
  private final String country;
  private final String city;
  private final String postalCode;
  private final String state;
  private final BigDecimal longitude;
  private final BigDecimal latitude;

  // industry of worker
  private final String industryId;

  @JsonCreator
  public CreateNewWorkerRequest(
      @JsonProperty("firstName") final String firstName,
      @JsonProperty("lastName") final String lastName,
      @JsonProperty("email") final String email,
      @JsonProperty("secondaryEmail") final String secondaryEmail,
      @JsonProperty("password") final String password,
      @JsonProperty("locale") final String locale,
      @JsonProperty("jobTitle") final String jobTitle,
      @JsonProperty("recruitingCampaignId") final Long recruitingCampaignId,
      @JsonProperty("companyName") final String companyName,
      @JsonProperty("country") final String country,
      @JsonProperty("address1") final String address1,
      @JsonProperty("address2") final String address2,
      @JsonProperty("city") final String city,
      @JsonProperty("postalCode") final String postalCode,
      @JsonProperty("state") final String state,
      @JsonProperty("longitude") final BigDecimal longitude,
      @JsonProperty("latitude") final BigDecimal latitude,
      @JsonProperty("industryId") final String industryId) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.secondaryEmail = secondaryEmail;
    this.password = password;
    this.locale = locale;
    this.jobTitle = jobTitle;
    this.recruitingCampaignId = recruitingCampaignId;
    this.companyName = companyName;
    this.country = country;
    this.address1 = address1;
    this.address2 = address2;
    this.city = city;
    this.postalCode = postalCode;
    this.state = state;
    this.longitude = longitude;
    this.latitude = latitude;
    this.industryId = industryId;
  }

  public CreateNewWorkerRequest(final CreateNewWorkerDTOBuilder builder) {
    this.firstName = builder.getFirstName();
    this.lastName = builder.getLastName();
    this.email = builder.getEmail();
    this.secondaryEmail = builder.getSecondaryEmail();
    this.password = builder.getPassword();
    this.locale = builder.getLocale();
    this.address1 = builder.getAddress1();
    this.address2 = builder.getAddress2();
    this.city = builder.getCity();
    this.state = builder.getState();
    this.country = builder.getCountry();
    this.postalCode = builder.getPostalCode();
    this.recruitingCampaignId = builder.getRecruitingCampaignId();
    this.jobTitle = builder.getJobTitle();
    this.industryId = builder.getIndustryId();
    this.companyName = builder.getCompanyName();
    this.longitude = builder.getLongitude();
    this.latitude = builder.getLatitude();
  }

  public static class CreateNewWorkerDTOBuilder {
    // required user fields
    private String firstName;
    private String lastName;
    private String secondaryEmail;
    private String email;
    private String password;
    private String locale;

    // optional
    private String jobTitle;

    private Long recruitingCampaignId;
    private String companyName;

    // Address fields
    private String address1;
    private String address2;
    private String country;
    private String city;
    private String postalCode;
    private String state;
    private BigDecimal longitude;
    private BigDecimal latitude;

    // industry of worker
    private String industryId;

    public CreateNewWorkerRequest build() {
      return new CreateNewWorkerRequest(this);
    }

    public String getFirstName() {
      return firstName;
    }

    @JsonProperty("firstName")
    public CreateNewWorkerDTOBuilder setFirstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public String getLastName() {
      return lastName;
    }

    @JsonProperty("lastName")
    public CreateNewWorkerDTOBuilder setLastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public String getEmail() {
      return email;
    }

    @JsonProperty("email")
    public CreateNewWorkerDTOBuilder setEmail(String email) {
      this.email = email;
      return this;
    }

    @JsonProperty("secondaryEmail")
    public String getSecondaryEmail() {
      return secondaryEmail;
    }

    public CreateNewWorkerDTOBuilder setSecondaryEmail(String secondaryEmail) {
      this.secondaryEmail = secondaryEmail;
      return this;
    }

    public String getPassword() {
      return password;
    }

    @JsonProperty("password")
    public CreateNewWorkerDTOBuilder setPassword(String password) {
      this.password = password;
      return this;
    }

    public String getLocale() {
      return locale;
    }

    @JsonProperty("locale")
    public CreateNewWorkerDTOBuilder setLocale(final String locale) {
      this.locale = locale;
      return this;
    }

    public String getJobTitle() {
      return jobTitle;
    }

    @JsonProperty("jobTitle")
    public CreateNewWorkerDTOBuilder setJobTitle(String jobTitle) {
      this.jobTitle = jobTitle;
      return this;
    }

    public Long getRecruitingCampaignId() {
      return recruitingCampaignId;
    }

    @JsonProperty("recruitingCampaignId")
    public CreateNewWorkerDTOBuilder setRecruitingCampaignId(Long recruitingCampaignId) {
      this.recruitingCampaignId = recruitingCampaignId;
      return this;
    }

    public String getCompanyName() {
      return companyName;
    }

    @JsonProperty("companyName")
    public CreateNewWorkerDTOBuilder setCompanyName(String companyName) {
      this.companyName = companyName;
      return this;
    }

    public String getCountry() {
      return country;
    }

    @JsonProperty("country")
    public CreateNewWorkerDTOBuilder setCountry(String country) {
      this.country = country;
      return this;
    }

    public String getCity() {
      return city;
    }

    @JsonProperty("city")
    public CreateNewWorkerDTOBuilder setCity(String city) {
      this.city = city;
      return this;
    }

    public String getAddress1() {
      return address1;
    }

    @JsonProperty("address1")
    public CreateNewWorkerDTOBuilder setAddress1(String address1) {
      this.address1 = address1;
      return this;
    }

    public String getAddress2() {
      return address2;
    }

    @JsonProperty("address2")
    public CreateNewWorkerDTOBuilder setAddress2(String address2) {
      this.address2 = address2;
      return this;
    }

    public String getPostalCode() {
      return postalCode;
    }

    @JsonProperty("postalCode")
    public CreateNewWorkerDTOBuilder setPostalCode(String postalCode) {
      this.postalCode = postalCode;
      return this;
    }

    public String getState() {
      return state;
    }

    @JsonProperty("state")
    public CreateNewWorkerDTOBuilder setState(String state) {
      this.state = state;
      return this;
    }

    public BigDecimal getLongitude() {
      return longitude;
    }

    @JsonProperty("longitude")
    public CreateNewWorkerDTOBuilder setLongitude(BigDecimal longitude) {
      this.longitude = longitude;
      return this;
    }

    public BigDecimal getLatitude() {
      return latitude;
    }

    @JsonProperty("latitude")
    public CreateNewWorkerDTOBuilder setLatitude(BigDecimal latitude) {
      this.latitude = latitude;
      return this;
    }

    public String getIndustryId() {
      return industryId;
    }

    @JsonProperty("industryId")
    public CreateNewWorkerDTOBuilder setIndustryId(String industryId) {
      this.industryId = industryId;
      return this;
    }
  }

  public static CreateNewWorkerDTOBuilder builder() {
    return new CreateNewWorkerDTOBuilder();
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getSecondaryEmail() {
    return secondaryEmail;
  }

  public String getPassword() {
    return password;
  }

  public String getLocale() {
    return locale;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public Long getRecruitingCampaignId() {
    return recruitingCampaignId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getCountry() {
    return country;
  }

  public String getAddress1() {
    return address1;
  }

  public String getAddress2() {
    return address2;
  }

  public String getCity() {
    return city;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getState() {
    return state;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }

  public BigDecimal getLatitude() {
    return latitude;
  }

  public String getIndustryId() {
    return industryId;
  }
}
