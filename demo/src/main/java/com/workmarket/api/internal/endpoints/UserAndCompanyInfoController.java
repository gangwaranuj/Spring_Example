package com.workmarket.api.internal.endpoints;

import com.workmarket.api.internal.user.gen.User.InternalCompanyEmployeesRequest;
import com.workmarket.api.internal.user.gen.User.InternalCompanyEmployeesResponse;
import com.workmarket.api.internal.user.gen.User.InternalUserAtCompanyDetails;
import com.workmarket.api.internal.user.gen.User.InternalUserCreateOrUpdateRequest;
import com.workmarket.api.internal.user.gen.User.InternalUserCreateOrUpdateResponse;
import com.workmarket.api.internal.user.gen.User.InternalUserFindByRequest;
import com.workmarket.api.internal.user.gen.User.InternalUserProfileDetails;
import com.workmarket.api.internal.user.gen.User.QueryBy;
import com.workmarket.common.api.exception.BadRequest;
import com.workmarket.common.api.vo.Response;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.integration.sso.SSOUserDetailsService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller used by the profile-service (translation service).
 */
@Controller
public class UserAndCompanyInfoController extends MicroserviceBaseController {

  private static final Logger logger = LoggerFactory.getLogger(UserAndCompanyInfoController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private CompanyService companyService;

  @Autowired
  private SSOUserDetailsService ssoUserDetailsService;

  @RequestMapping(
      value = "/v2/internal/profile/user/",
      method = POST,
      produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  Response<InternalUserAtCompanyDetails> getUserProfile(@RequestBody final InternalUserFindByRequest request) {
    final Long userId;
    if (request.getQueryBy() == QueryBy.USER_BY_EMAIL) {
      userId = userService.findUserIdByEmail(request.getQueryParameter());
    } else if (request.getQueryBy() == QueryBy.USER_BY_UUID) {
      userId = userService.findUserIdByUuid(request.getQueryParameter());
    } else {
      throw new BadRequest("query_by should be either EMAIL or UUID, not " + request.getQueryBy());
    }
    return Response.valueWithResult(getUserAtCompanyDetails(userId));
  }

  private InternalUserAtCompanyDetails getUserAtCompanyDetails(final Long userId) {
    if (userId == null) {
      InternalUserProfileDetails profile = InternalUserProfileDetails.newBuilder().setFound(false).build();
      return InternalUserAtCompanyDetails.newBuilder()
          .setUserProfileDetails(profile)
          .build();
    }
    final User u = userService.getUser(userId);
    // The following rigamarole brought to you by Hibernate -- helpfully doing stuff to break you in unexpected ways.
    // And showing why mocked tests don't tell you as much as you wish they would.
    final Company company = u.getCompany();
    final Long companyId = company.getId();
    final Company realCompany = companyService.findById(companyId);
    final String coUuid = realCompany.getUuid();
    final UserAssetAssociation assetAssociation = userService.findUserAvatars(userId);
    return buildProfile(u, coUuid, assetAssociation);
  }

  @RequestMapping(
      value = "/v2/internal/company/employees/",
      method = POST,
      produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  Response<InternalCompanyEmployeesResponse> getCompanyEmployees(
          @RequestBody final InternalCompanyEmployeesRequest request) {

    final String companyUuid = request.getCompanyUuid();
    if (StringUtils.isBlank(companyUuid)) {
      throw new BadRequest("Company uuid is required.");
    }

    final Long companyId = companyService.findCompanyIdByUuid(companyUuid);
    final List<UserDTO> userDTOs = userService.findUserDTOsOfAllActiveEmployees(companyId, false);
    final InternalCompanyEmployeesResponse.Builder responseBuilder = InternalCompanyEmployeesResponse.newBuilder()
        .setFound(true)
        .setCompanyUuid(companyUuid);

    if (CollectionUtils.isEmpty(userDTOs)) {
      return Response.valueWithResult(responseBuilder.build());
    }

    for (final UserDTO user : userDTOs) {
        responseBuilder.addUsers(
                InternalUserAtCompanyDetails.newBuilder()
                        .setUuid(user.getUuid())
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .build());
    }

    return Response.valueWithResult(responseBuilder.build());
  }

  private InternalUserAtCompanyDetails buildProfile(
      final User user,
      final String companyUuid,
      final UserAssetAssociation assetAssociation) {
    final Profile profile = user.getProfile();
    final String avatar = assetAssociation != null && assetAssociation.getAsset() != null
        ? assetAssociation.getAsset().getDownloadableUri()
        : "";

    InternalUserProfileDetails userProfileDetails = InternalUserProfileDetails
      .newBuilder()
      .setFound(true)
      .setDeprecatedNumericId(user.getId())
      .setCompanyUuid(companyUuid)
      .setFirstName(user.getFirstName())
      .setLastName(user.getLastName())
      .setUserEmail(user.getEmail())
      .setJobTitle(profile != null && profile.getJobTitle() != null
          ? profile.getJobTitle()
          : "")
      .setAvatar(avatar)
      .build();

    return InternalUserAtCompanyDetails.newBuilder()
        .setUuid(user.getUuid())
        .setCompanyUuid(companyUuid)
        .setFirstName(user.getFirstName())
        .setLastName(user.getLastName())
        .setUserAtCompanyPhoneNumber(profile != null && profile.getWorkPhone() != null
          ? profile.getWorkPhone()
          : "")
        .setUserAtCompanyEmail(user.getEmail())
        .setUserProfileDetails(userProfileDetails)
        .build();
  }

  @RequestMapping(
      value = "/v2/internal/user-at-company/create-sso",
      method = POST,
      produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  Response<InternalUserCreateOrUpdateResponse> createUserProfile(
      @RequestBody final InternalUserCreateOrUpdateRequest createUserRequest,
      final HttpServletRequest servletRequest) {

    logger.debug(
        "createUserProfile(): requested creation of user {}",
        createUserRequest.getUser().getUserAtCompanyEmail());

    final ExtendedUserDetails eud = ssoUserDetailsService.createSSOUser(createUserRequest);
    final Company company = companyService.findCompanyById(eud.getCompanyId());

    final InternalUserAtCompanyDetails user = InternalUserAtCompanyDetails.newBuilder()
        .setUuid(eud.getUuid())
        .setCompanyUuid(company.getUuid())
        .setUserAtCompanyEmail(eud.getEmail())
        .setFirstName(eud.getFirstName())
        .setLastName(eud.getLastName())
        .setUserAtCompanyPhoneNumber(eud.getCompanyNumber())
        .setIdpEntityId(createUserRequest.getUser().getIdpEntityId())
        .build();

    final InternalUserProfileDetails profile = InternalUserProfileDetails.newBuilder()
        .setFound(eud != null)
        .setCompanyUuid(company.getUuid())
        .setUserEmail(eud.getEmail())
        .setFirstName(eud.getFirstName())
        .setLastName(eud.getLastName())
        .setUserPhoneNumber(eud.getUserNumber())
        .build();

    logger.debug(
        "createUserProfile(): user {} created successfully? {}",
        createUserRequest.getUser().getUserAtCompanyEmail(), profile.getFound());

    return Response.valueWithResult(InternalUserCreateOrUpdateResponse.newBuilder()
        .setSuccess(eud != null)
        .setUser(user)
        .setProfile(profile)
        .build());
  }

}
