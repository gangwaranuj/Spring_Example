package com.workmarket.api.v2.worker.controllers;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.model.ApiGroupDTO;
import com.workmarket.api.v2.worker.model.ApiGroupInvitationDTO;
import com.workmarket.api.v2.worker.model.ApiGroupMembershipDTO;
import com.workmarket.api.v2.worker.model.ApiGroupRequirementDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException403;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"worker", "groups"})
@RequestMapping({"/v2/worker", "/worker/v2"})
@Controller("groupsWorkerController")
public class GroupsController extends ApiBaseController {

  static final String MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR  = "groups.view.unauthorized";
  static final String MESSAGE_GROUPS_APPLY_PENDING               = "groups.apply.pending";
  static final String MESSAGE_GROUPS_APPLY_APPROVED              = "groups.apply.approved";
  static final String MESSAGE_GROUPS_APPLY_ERROR                 = "groups.apply.error";
  static final String MESSAGE_GROUPS_DECLINE_NOTICE              = "groups.decline.notice";
  static final String MESSAGE_GROUPS_DECLINE_NO_INVITATION_ERROR = "groups.decline.error.noInvitation";
  static final String MESSAGE_GROUPS_DECLINE_ERROR               = "groups.decline.error";

  private static final SimpleDateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  @Autowired private UserGroupService userGroupService;
  @Autowired private CompanyService companyService;
  @Autowired private RequestService requestService;

  @ResponseBody
  @ApiOperation(value = "Get group details")
  @RequestMapping(value = "/groups/{groupId}", method = GET, produces = APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = MESSAGE_OK),
      @ApiResponse(code = 403, message = MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR),
  })
  public ApiV2Response<ApiGroupDTO> details(
      @ApiParam(value = "Group ID") @PathVariable final long groupId) {
    final UserGroup group = userGroupService.findGroupById(groupId);
    if (group == null) {
      throw new HttpException403(messageHelper.getMessage(MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR));
    }

    final ExtendedUserDetails user = getCurrentUser();
    if (!isAuthorizedToView(group, user)) {
      throw new HttpException403(messageHelper.getMessage(MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR));
    }

    ApiGroupDTO.Builder apiGroupDTOBuilder = new ApiGroupDTO.Builder()
        .withName(group.getName())
        .withDescription(group.getDescription())
        .withMemberCount(userGroupService.countAllActiveGroupMembers(groupId))
        .withCreatedOn(ISO8601_DATE_FORMAT.format(group.getCreatedOn().getTime()))
        .withIndustryName(group.getIndustry() != null ? group.getIndustry().getName() : null)
        .withOwnerFullName(group.getOwner().getFullName())
        .withCompanyEffectiveName(group.getCompany() != null ? group.getCompany().getEffectiveName() : null);

    final CompanyAssetAssociation avatars = group.getCompany() != null ? companyService.findCompanyAvatars(group.getCompany().getId()) : null;
    if (avatars != null && avatars.getTransformedLargeAsset() != null) {
      apiGroupDTOBuilder = apiGroupDTOBuilder.withAvatarLarge(avatars.getTransformedLargeAsset().getUri());
    }

    final UserUserGroupAssociation association = userGroupService.findAssociationByGroupIdAndUserId(groupId, user.getId());
    final Eligibility eligibility = isGroupMember(association) ?
        userGroupService.reValidateRequirementSets(groupId, user.getId()) :
        userGroupService.validateRequirementSets(groupId, user.getId());

    ApiGroupMembershipDTO.Builder apiGroupMembershipDTOBuilder = new ApiGroupMembershipDTO.Builder()
        .withIsMember(isGroupMember(association))
        .withIsEligible(eligibility.isEligible())
        .withRequirements(FluentIterable
            .from(eligibility.getCriteria())
            .transform(new Function<Criterion, ApiGroupRequirementDTO>() {
              @Override
              public ApiGroupRequirementDTO apply(final Criterion criterion) {
                return new ApiGroupRequirementDTO.Builder()
                    .withName(criterion.getName())
                    .withTypeName(criterion.getTypeName())
                    .withUrl(criterion.getUrl())
                    .withIsMet(criterion.isMet())
                    .build();
              }
            })
            .toList());
    if (association == null) {
      final UserGroupInvitation latestInvitation = requestService.findLatestUserGroupInvitationRequestByInvitedUserAndUserGroup(user.getId(), groupId);
      if (latestInvitation != null) {
        apiGroupMembershipDTOBuilder = apiGroupMembershipDTOBuilder
            .withInvitation(new ApiGroupInvitationDTO.Builder()
                .withRequestDate(ISO8601_DATE_FORMAT.format(latestInvitation.getRequestDate().getTime()))
                .withRequesterFullName(latestInvitation.getRequestor().getFullName())
                .build());
      }
    }

    apiGroupDTOBuilder = apiGroupDTOBuilder.withMembership(apiGroupMembershipDTOBuilder.build());

    return ApiV2Response.OK(ImmutableList.of(apiGroupDTOBuilder.build()));
  }


  @ResponseBody
  @ApiOperation(value = "Apply to join a group")
  @RequestMapping(value = "/groups/{groupId}/apply", method = POST, produces = APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = MESSAGE_GROUPS_APPLY_PENDING),
      @ApiResponse(code = 200, message = MESSAGE_GROUPS_APPLY_APPROVED),
      @ApiResponse(code = 403, message = MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR),
      @ApiResponse(code = 400, message = MESSAGE_GROUPS_APPLY_ERROR),
  })
  public ApiV2Response apply(
      @ApiParam(value = "Group ID") @PathVariable final long groupId
  ) {
    final UserGroup group = userGroupService.findGroupById(groupId);
    if (group == null) {
      throw new HttpException403(messageHelper.getMessage(MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR));
    }

    final ExtendedUserDetails user = getCurrentUser();
    if (!isAuthorizedToView(group, user)) {
      throw new HttpException403(messageHelper.getMessage(MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR));
    }

    Eligibility eligibility = userGroupService.validateRequirementSets(groupId, user.getId());
    UserUserGroupAssociation association = userGroupService.applyToGroup(groupId, user.getId());

    if (association == null) {
      throw new HttpException400(messageHelper.getMessage(MESSAGE_GROUPS_APPLY_ERROR));
    }

    if (!eligibility.isEligible() || group.getRequiresApproval()) {
      return ApiV2Response.valueWithMessage(
          messageHelper.getMessage(MESSAGE_GROUPS_APPLY_PENDING, group.getName()),
          HttpStatus.OK);
    }
    else {
      return ApiV2Response.valueWithMessage(
          messageHelper.getMessage(MESSAGE_GROUPS_APPLY_APPROVED, group.getName()),
          HttpStatus.OK);
    }
  }


  @ResponseBody
  @ApiOperation(value = "Decline to join a group")
  @RequestMapping(value = "/groups/{groupId}/decline", method = POST, produces = APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = MESSAGE_GROUPS_DECLINE_NOTICE),
      @ApiResponse(code = 400, message = MESSAGE_GROUPS_DECLINE_NO_INVITATION_ERROR),
      @ApiResponse(code = 400, message = MESSAGE_GROUPS_DECLINE_ERROR),
  })
  public ApiV2Response decline(
      @ApiParam(value = "Group ID") @PathVariable final long groupId
  ) {
    final ExtendedUserDetails user = getCurrentUser();
    final boolean hasActiveInvitations = requestService.findLatestUserGroupInvitationRequestByInvitedUserAndUserGroup(user.getId(), groupId) != null;

    if (!hasActiveInvitations) {
      throw new HttpException400(messageHelper.getMessage(MESSAGE_GROUPS_DECLINE_NO_INVITATION_ERROR));
    }

    try {
      requestService.declineInvitationToGroup(user.getId(), groupId);
    } catch (Exception e) {
      throw new HttpException400(messageHelper.getMessage(MESSAGE_GROUPS_DECLINE_ERROR));
    }

    return ApiV2Response.valueWithMessage(
        messageHelper.getMessage(MESSAGE_GROUPS_DECLINE_NOTICE),
        HttpStatus.OK);
  }


  private static boolean isAuthorizedToView(UserGroup group, ExtendedUserDetails user) {
    return group.getActiveFlag() &&
        !group.getDeleted() &&
        group.getOpenMembership() &&
        (!user.isEmployeeWorker() || isCompanyGroupViewer(group, user));
  }


  private static boolean isGroupMember(UserUserGroupAssociation association) {
    return association != null && association.isApproved() && association.getVerificationStatus().isVerified();
  }


  private static boolean isCompanyGroupViewer(UserGroup group, ExtendedUserDetails user) {
    return (group.getCompany() != null) && user.getCompanyId().equals(group.getCompany().getId());
  }
}

