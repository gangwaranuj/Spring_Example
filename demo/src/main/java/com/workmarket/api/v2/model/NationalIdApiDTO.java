package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "NationalId")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = NationalIdApiDTO.Builder.class)
public class NationalIdApiDTO {
  public final String country;
  public final String countryId;
  public final String englishName;

  public NationalIdApiDTO(final Builder builder) {
    this.country = builder.country;
    this.countryId = builder.countryId;
    this.englishName = builder.englishName;
  }

  @ApiModelProperty(name = "country")
  @JsonProperty("country")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getCountry() {
    return country;
  }

  @ApiModelProperty(name = "countryId")
  @JsonProperty("countryId")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getCountryId() {
    return countryId;
  }

  @ApiModelProperty(name = "englishName")
  @JsonProperty("englishName")
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  public String getEnglishName() {
    return englishName;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder implements AbstractBuilder<NationalIdApiDTO> {
    public String country;
    public String countryId;
    public String englishName;

    public Builder() { }

    @JsonProperty("country")
    public Builder setCountry(String country) {
      this.country = country;
      return this;
    }

    @JsonProperty("countryId")
    public Builder setCountryId(String countryId) {
      this.countryId = countryId;
      return this;
    }

    @JsonProperty("englishName")
    public Builder setEnglishName(String englishName) {
      this.englishName = englishName;
      return this;
    }

    @Override
    public NationalIdApiDTO build() {
      return new NationalIdApiDTO(this);
    }
  }
}
