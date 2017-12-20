package com.workmarket.api.v2.worker.controllers;

import com.google.common.collect.ImmutableSet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.model.ApiGroupDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.domains.model.requirementset.EligibilityUser;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.utility.StringUtilities;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class GroupsControllerTest extends BaseApiControllerTest {

  private static final TypeReference<ApiV2Response<ApiGroupDTO>> API_GROUP_RESPONSE_TYPE = new TypeReference<ApiV2Response<ApiGroupDTO>>() { };
  private static final TypeReference<ApiV2Response<Object>>      API_NO_RESPONSE_TYPE    = new TypeReference<ApiV2Response<Object>>() { };

  @Mock private UserGroupService userGroupService;
  @Mock private CompanyService companyService;
  @Mock private RequestService requestService;
  @InjectMocks private GroupsController controller = new GroupsController();

  @Before
  public void setup() throws Exception {
    super.setup(controller);
  }

  @Test
  public void detailsForNonMember() throws Exception {
    final User currentUser = new User();

    final Calendar createdOn = Calendar.getInstance();
    final User owner = new User();
    owner.setFirstName(StringUtilities.fullName("Michelle", "Forcier"));
    final Company company = new Company();
    company.setEffectiveName("Herman Integration Services");
    final UserGroup group = new UserGroup();
    group.setName("Residential AV Technicians");
    group.setDescription("Residential AV Technicians Overview");
    group.setCreatedOn(createdOn);
    group.setIndustry(new Industry(1L, "Technology and Communications"));
    group.setOwner(owner);
    group.setCompany(company);
    group.setActiveFlag(true);
    group.setOpenMembership(true);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);

    when(userGroupService.countAllActiveGroupMembers(any(Long.class))).thenReturn(65);

    when(userGroupService.findAssociationByGroupIdAndUserId(any(Long.class), any(Long.class))).thenReturn(null);

    final Criterion criterion = new Criterion(new EligibilityUser(currentUser, null), null);
    criterion.setName("Passed Sterling Drug Test");
    criterion.setTypeName("Drug Test");
    criterion.setUrl("/screening/drug");
    criterion.setMet(true);
    when(userGroupService.validateRequirementSets(any(Long.class), any(Long.class))).thenReturn(new Eligibility(
        ImmutableSet.of(criterion),
        true));

    final UserGroupInvitation invitation = new UserGroupInvitation(owner, currentUser, group, UserGroupInvitationType.RECOMMENDATION);
    final Calendar requestDate = Calendar.getInstance();
    invitation.setRequestDate(requestDate);
    when(requestService.findLatestUserGroupInvitationRequestByInvitedUserAndUserGroup(any(Long.class), any(Long.class))).thenReturn(invitation);

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .get("/worker/v2/groups/1234")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiGroupDTO apiGroupDTO = response.getResults().get(0);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.OK.value(), responseMeta);

    assertEquals("Residential AV Technicians", apiGroupDTO.getName());
    assertEquals("Residential AV Technicians Overview", apiGroupDTO.getDescription());
    assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(createdOn.getTime()), apiGroupDTO.getCreatedOn());
    assertEquals("Technology and Communications", apiGroupDTO.getIndustryName());
    assertEquals(StringUtilities.fullName("Michelle", "Forcier"), apiGroupDTO.getOwnerFullName());
    assertEquals("Herman Integration Services", apiGroupDTO.getCompanyEffectiveName());
    assertEquals(65, apiGroupDTO.getMemberCount());
    assertEquals(false, apiGroupDTO.getMembership().isMember());
    assertEquals(true, apiGroupDTO.getMembership().isEligible());
    assertEquals(1, apiGroupDTO.getMembership().getRequirements().size());
    assertEquals("Passed Sterling Drug Test", apiGroupDTO.getMembership().getRequirements().get(0).getName());
    assertEquals("Drug Test", apiGroupDTO.getMembership().getRequirements().get(0).getTypeName());
    assertEquals("/screening/drug", apiGroupDTO.getMembership().getRequirements().get(0).getUrl());
    assertEquals(true, apiGroupDTO.getMembership().getRequirements().get(0).isMet());
    assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(requestDate.getTime()), apiGroupDTO.getMembership().getInvitation().getRequestDate());
    assertEquals(StringUtilities.fullName("Michelle", "Forcier"), apiGroupDTO.getMembership().getInvitation().getRequesterFullName());
  }


  @Test
  public void detailsForMember() throws Exception {
    final User currentUser = new User();

    final Calendar createdOn = Calendar.getInstance();
    final User owner = new User();
    owner.setFirstName(StringUtilities.fullName("Michelle", "Forcier"));
    final Company company = new Company();
    company.setEffectiveName("Herman Integration Services");
    final UserGroup group = new UserGroup();
    group.setName("Residential AV Technicians");
    group.setDescription("Residential AV Technicians Overview");
    group.setCreatedOn(createdOn);
    group.setIndustry(new Industry(1L, "Technology and Communications"));
    group.setOwner(owner);
    group.setCompany(company);
    group.setActiveFlag(true);
    group.setOpenMembership(true);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);

    when(userGroupService.countAllActiveGroupMembers(any(Long.class))).thenReturn(65);

    final UserUserGroupAssociation association = new UserUserGroupAssociation(currentUser, group);
    association.setApprovalStatus(ApprovalStatus.APPROVED);
    association.setVerificationStatus(VerificationStatus.VERIFIED);
    when(userGroupService.findAssociationByGroupIdAndUserId(any(Long.class), any(Long.class))).thenReturn(
        association);

    final Criterion criterion = new Criterion(new EligibilityUser(currentUser, null), null);
    criterion.setName("Passed Sterling Drug Test");
    criterion.setTypeName("Drug Test");
    criterion.setUrl("/screening/drug");
    criterion.setMet(true);
    when(userGroupService.reValidateRequirementSets(any(Long.class), any(Long.class))).thenReturn(new Eligibility(
        ImmutableSet.of(criterion),
        true));

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .get("/worker/v2/groups/1234")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiGroupDTO apiGroupDTO = response.getResults().get(0);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.OK.value(), responseMeta);

    assertEquals("Residential AV Technicians", apiGroupDTO.getName());
    assertEquals("Residential AV Technicians Overview", apiGroupDTO.getDescription());
    assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(createdOn.getTime()), apiGroupDTO.getCreatedOn());
    assertEquals("Technology and Communications", apiGroupDTO.getIndustryName());
    assertEquals(StringUtilities.fullName("Michelle", "Forcier"), apiGroupDTO.getOwnerFullName());
    assertEquals("Herman Integration Services", apiGroupDTO.getCompanyEffectiveName());
    assertEquals(65, apiGroupDTO.getMemberCount());
    assertEquals(true, apiGroupDTO.getMembership().isMember());
    assertEquals(true, apiGroupDTO.getMembership().isEligible());
    assertEquals(1, apiGroupDTO.getMembership().getRequirements().size());
    assertEquals("Passed Sterling Drug Test", apiGroupDTO.getMembership().getRequirements().get(0).getName());
    assertEquals("Drug Test", apiGroupDTO.getMembership().getRequirements().get(0).getTypeName());
    assertEquals("/screening/drug", apiGroupDTO.getMembership().getRequirements().get(0).getUrl());
    assertEquals(true, apiGroupDTO.getMembership().getRequirements().get(0).isMet());
    assertEquals(null, apiGroupDTO.getMembership().getInvitation());
  }


  @Test
  public void detailsForNonExistingGroup() throws Exception {
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(null);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .get("/worker/v2/groups/1234")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void detailsForNonActiveGroup() throws Exception {
    final UserGroup group = new UserGroup();
    group.setActiveFlag(false);
    group.setOpenMembership(true);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .get("/worker/v2/groups/1234")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void detailsForDeletedGroup() throws Exception {
    final UserGroup group = new UserGroup();
    group.setActiveFlag(true);
    group.setOpenMembership(true);
    group.setDeleted(true);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .get("/worker/v2/groups/1234")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void detailsForNonPublicGroup() throws Exception {
    final ExtendedUserDetails userDetails = mock(ExtendedUserDetails.class);
    when(userDetails.isSeller()).thenReturn(true);

    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(userDetails);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final UserGroup group = new UserGroup();
    group.setActiveFlag(true);
    group.setOpenMembership(false);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .get("/worker/v2/groups/1234")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void detailsForEmployeeWorkerAndDifferentGroupCompany() throws Exception {
    final ExtendedUserDetails userDetails = mock(ExtendedUserDetails.class);
    when(userDetails.isEmployeeWorker()).thenReturn(true);
    when(userDetails.getCompanyId()).thenReturn(1L);

    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(userDetails);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final Company company = new Company();
    company.setId(2L);
    final UserGroup group = new UserGroup();
    group.setCompany(company);
    group.setActiveFlag(true);
    group.setOpenMembership(true);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .get("/worker/v2/groups/1234")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void detailsForEmployeeWorkerAndNonPublicGroup() throws Exception {
    final ExtendedUserDetails userDetails = mock(ExtendedUserDetails.class);
    when(userDetails.isEmployeeWorker()).thenReturn(true);
    when(userDetails.getCompanyId()).thenReturn(1L);

    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(userDetails);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final Company company = new Company();
    company.setId(1L);
    final UserGroup group = new UserGroup();
    group.setCompany(company);
    group.setActiveFlag(true);
    group.setOpenMembership(false);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .get("/worker/v2/groups/1234")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void applyPendingWhenIsNotEligible() throws Exception {
    final UserGroup group = new UserGroup();
    group.setOpenMembership(true);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);
    when(userGroupService.applyToGroup(any(Long.class), any(Long.class))).thenReturn(new UserUserGroupAssociation());
    when(userGroupService.validateRequirementSets(any(Long.class), any(Long.class))).thenReturn(new Eligibility(
        ImmutableSet.<Criterion>of(),
        false));
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_APPLY_PENDING), any(String.class))).thenReturn("success.pending");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/apply")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
    final ApiV2Response response = expectApiV2Response(result, API_NO_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.OK.value(), responseMeta);
    assertEquals("success.pending", responseMeta.get("message"));
  }


  @Test
  public void applyPendingWhenRequiresApproval() throws Exception {
    final UserGroup group = new UserGroup();
    group.setOpenMembership(true);
    group.setRequiresApproval(true);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);
    when(userGroupService.applyToGroup(any(Long.class), any(Long.class))).thenReturn(new UserUserGroupAssociation());
    when(userGroupService.validateRequirementSets(any(Long.class), any(Long.class))).thenReturn(new Eligibility(
        ImmutableSet.<Criterion>of(),
        true));
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_APPLY_PENDING), any(String.class))).thenReturn("success.pending");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/apply")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
    final ApiV2Response response = expectApiV2Response(result, API_NO_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.OK.value(), responseMeta);
    assertEquals("success.pending", responseMeta.get("message"));
  }


  @Test
  public void applyApproved() throws Exception {
    final UserGroup group = new UserGroup();
    group.setOpenMembership(true);
    group.setRequiresApproval(false);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);
    when(userGroupService.applyToGroup(any(Long.class), any(Long.class))).thenReturn(new UserUserGroupAssociation());
    when(userGroupService.validateRequirementSets(any(Long.class), any(Long.class))).thenReturn(new Eligibility(
        ImmutableSet.<Criterion>of(),
        true));
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_APPLY_APPROVED), any(String.class))).thenReturn("success.approved");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/apply")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
    final ApiV2Response response = expectApiV2Response(result, API_NO_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.OK.value(), responseMeta);
    assertEquals("success.approved", responseMeta.get("message"));
  }


  @Test
  public void applyNonExistingGroup() throws Exception {
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(null);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/apply")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();
    final ApiV2Response response = expectApiV2Response(result, API_NO_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void applyError() throws Exception {
    final UserGroup group = new UserGroup();
    group.setOpenMembership(true);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);
    when(userGroupService.applyToGroup(any(Long.class), any(Long.class))).thenReturn(null);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_APPLY_ERROR))).thenReturn("error");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/apply")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
    final ApiV2Response response = expectApiV2Response(result, API_NO_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.BAD_REQUEST.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void applyForNonExistingGroup() throws Exception {
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(null);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/apply")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void applyForNonActiveGroup() throws Exception {
    final UserGroup group = new UserGroup();
    group.setActiveFlag(false);
    group.setOpenMembership(true);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/apply")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void applyForDeletedGroup() throws Exception {
    final UserGroup group = new UserGroup();
    group.setActiveFlag(true);
    group.setOpenMembership(true);
    group.setDeleted(true);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/apply")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void applyForNonPublicGroup() throws Exception {
    final ExtendedUserDetails userDetails = mock(ExtendedUserDetails.class);

    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(userDetails);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final UserGroup group = new UserGroup();
    group.setActiveFlag(true);
    group.setOpenMembership(false);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/apply")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void applyForEmployeeWorkerAndDifferentGroupCompany() throws Exception {
    final ExtendedUserDetails userDetails = mock(ExtendedUserDetails.class);
    when(userDetails.isEmployeeWorker()).thenReturn(true);
    when(userDetails.getCompanyId()).thenReturn(1L);

    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(userDetails);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final Company company = new Company();
    company.setId(2L);
    final UserGroup group = new UserGroup();
    group.setCompany(company);
    group.setActiveFlag(true);
    group.setOpenMembership(true);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/apply")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void applyForEmployeeWorkerAndNonPublicGroup() throws Exception {
    final ExtendedUserDetails userDetails = mock(ExtendedUserDetails.class);
    when(userDetails.isEmployeeWorker()).thenReturn(true);
    when(userDetails.getCompanyId()).thenReturn(1L);

    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(userDetails);
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DETAILS_UNAUTHORIZED_ERROR))).thenReturn("error");

    final Company company = new Company();
    company.setId(1L);
    final UserGroup group = new UserGroup();
    group.setCompany(company);
    group.setActiveFlag(true);
    group.setOpenMembership(false);
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(group);

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/apply")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<ApiGroupDTO> response = expectApiV2Response(result, API_GROUP_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void decline() throws Exception {
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(new UserGroup());
    when(requestService.findLatestUserGroupInvitationRequestByInvitedUserAndUserGroup(any(Long.class), any(Long.class))).thenReturn(new UserGroupInvitation());
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DECLINE_NOTICE))).thenReturn("success");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/decline")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
    final ApiV2Response response = expectApiV2Response(result, API_NO_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.OK.value(), responseMeta);
    assertEquals("success", responseMeta.get("message"));
  }


  @Test
  public void declineNoInvitationError() throws Exception {
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(new UserGroup());
    when(requestService.findLatestUserGroupInvitationRequestByInvitedUserAndUserGroup(any(Long.class), any(Long.class))).thenReturn(null);
    doThrow(new RuntimeException()).when(requestService).declineInvitationToGroup(any(Long.class), any(Long.class));
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DECLINE_NO_INVITATION_ERROR))).thenReturn("error");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/decline")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
    final ApiV2Response response = expectApiV2Response(result, API_NO_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.BAD_REQUEST.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }


  @Test
  public void declineError() throws Exception {
    when(userGroupService.findGroupById(any(Long.class))).thenReturn(new UserGroup());
    when(requestService.findLatestUserGroupInvitationRequestByInvitedUserAndUserGroup(any(Long.class), any(Long.class))).thenReturn(new UserGroupInvitation());
    doThrow(new RuntimeException()).when(requestService).declineInvitationToGroup(any(Long.class), any(Long.class));
    when(messageHelper.getMessage(eq(GroupsController.MESSAGE_GROUPS_DECLINE_ERROR))).thenReturn("error");

    final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
        .post("/worker/v2/groups/1234/decline")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
    final ApiV2Response response = expectApiV2Response(result, API_NO_RESPONSE_TYPE);
    final ApiJSONPayloadMap responseMeta = response.getMeta();

    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(HttpStatus.BAD_REQUEST.value(), responseMeta);
    assertEquals("error", responseMeta.get("message"));
  }
}
