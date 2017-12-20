package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "GroupMembership", description = "A worker group membership")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = ApiGroupMembershipDTO.Builder.class)
public class ApiGroupMembershipDTO {
  private final boolean isMember;
  private final boolean isEligible;
  private final ApiGroupInvitationDTO invitation;
  private final List<ApiGroupRequirementDTO> requirements;


  private ApiGroupMembershipDTO(final Builder builder) {
    this.isMember = builder.isMember;
    this.isEligible = builder.isEligible;
    this.invitation = builder.invitation;
    this.requirements = builder.requirements;
  }

  @ApiModelProperty(name = "isMember", value = "If current user is member of this group")
  @JsonProperty("isMember")
  public boolean isMember() {
    return isMember;
  }

  @ApiModelProperty(name = "isEligible", value = "If current user is eligible to join this group")
  @JsonProperty("isEligible")
  public boolean isEligible() {
    return isEligible;
  }

  @ApiModelProperty(name = "invitation", value = "Invitation made to join this group")
  @JsonProperty("invitation")
  public ApiGroupInvitationDTO getInvitation() {
    return invitation;
  }

  @ApiModelProperty(name = "requirements", value = "Group membership requirements")
  @JsonProperty("requirements")
  public List<ApiGroupRequirementDTO> getRequirements() {
    return requirements;
  }

  public static final class Builder {
    private boolean isMember;
    private boolean isEligible;
    private ApiGroupInvitationDTO invitation;
    private List<ApiGroupRequirementDTO> requirements;

    @JsonProperty("isMember")
    public Builder withIsMember(final boolean isMember) {
      this.isMember = isMember;
      return this;
    }

    @JsonProperty("isEligible")
    public Builder withIsEligible(final boolean isEligible) {
      this.isEligible = isEligible;
      return this;
    }

    @JsonProperty("invitation")
    public Builder withInvitation(final ApiGroupInvitationDTO invitation) {
      this.invitation = invitation;
      return this;
    }

    @JsonProperty("requirements")
    public Builder withRequirements(final List<ApiGroupRequirementDTO> requirements) {
      this.requirements = requirements;
      return this;
    }

    public ApiGroupMembershipDTO build() {
      return new ApiGroupMembershipDTO(this);
    }
  }
}
