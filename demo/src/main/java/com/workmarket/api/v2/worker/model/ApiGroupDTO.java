package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Group", description = "A worker group")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = ApiGroupDTO.Builder.class)
public class ApiGroupDTO {
  private final String name;
  private final String description;
  private final int memberCount;
  private final String createdOn;
  private final String industryName;
  private final String ownerFullName;
  private final String companyEffectiveName;
  private final String avatarLarge;
  private final ApiGroupMembershipDTO membership;

  private ApiGroupDTO(final Builder builder) {
    this.name = builder.name;
    this.description = builder.description;
    this.memberCount = builder.memberCount;
    this.createdOn = builder.createdOn;
    this.industryName = builder.industryName;
    this.ownerFullName = builder.ownerFullName;
    this.companyEffectiveName = builder.companyEffectiveName;
    this.avatarLarge = builder.avatarLarge;
    this.membership = builder.membership;
  }

  @ApiModelProperty(name = "name", value = "Group name")
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @ApiModelProperty(name = "description", value = "Group description")
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @ApiModelProperty(name = "memberCount", value = "Total number of active members")
  @JsonProperty("memberCount")
  public int getMemberCount() {
    return memberCount;
  }

  @ApiModelProperty(name = "createdOn", value = "Creation date")
  @JsonProperty("createdOn")
  public String getCreatedOn() {
    return createdOn;
  }

  @ApiModelProperty(name = "industryName", value = "Group industry")
  @JsonProperty("industryName")
  public String getIndustryName() {
    return industryName;
  }

  @ApiModelProperty(name = "ownerFullName", value = "Group's owner name")
  @JsonProperty("ownerFullName")
  public String getOwnerFullName() {
    return ownerFullName;
  }

  @ApiModelProperty(name = "companyEffectiveName", value = "Group's company name")
  @JsonProperty("companyEffectiveName")
  public String getCompanyEffectiveName() {
    return companyEffectiveName;
  }

  @ApiModelProperty(name = "avatarLarge", value = "Group's company logo")
  @JsonProperty("avatarLarge")
  public String getAvatarLarge() {
    return avatarLarge;
  }

  @ApiModelProperty(name = "membership", value = "Current user group membership")
  @JsonProperty("membership")
  public ApiGroupMembershipDTO getMembership() {
    return membership;
  }

  public static final class Builder {
    private String name;
    private String description;
    private int memberCount;
    private String createdOn;
    private String industryName;
    private String ownerFullName;
    private String companyEffectiveName;
    private String avatarLarge;
    private ApiGroupMembershipDTO membership;

    @JsonProperty("name")
    public Builder withName(final String name) {
      this.name = name;
      return this;
    }

    @JsonProperty("description")
    public Builder withDescription(final String description) {
      this.description = description;
      return this;
    }

    @JsonProperty("memberCount")
    public Builder withMemberCount(final int memberCount) {
      this.memberCount = memberCount;
      return this;
    }

    @JsonProperty("createdOn")
    public Builder withCreatedOn(final String createdOn) {
      this.createdOn = createdOn;
      return this;
    }

    @JsonProperty("industryName")
    public Builder withIndustryName(final String industryName) {
      this.industryName = industryName;
      return this;
    }

    @JsonProperty("ownerFullName")
    public Builder withOwnerFullName(final String ownerFullName) {
      this.ownerFullName = ownerFullName;
      return this;
    }

    @JsonProperty("companyEffectiveName")
    public Builder withCompanyEffectiveName(final String companyEffectiveName) {
      this.companyEffectiveName = companyEffectiveName;
      return this;
    }

    @JsonProperty("avatarLarge")
    public Builder withAvatarLarge(final String avatarLarge) {
      this.avatarLarge = avatarLarge;
      return this;
    }

    @JsonProperty("membership")
    public Builder withMembership(final ApiGroupMembershipDTO membership) {
      this.membership = membership;
      return this;
    }

    public ApiGroupDTO build() {
      return new ApiGroupDTO(this);
    }
  }
}
