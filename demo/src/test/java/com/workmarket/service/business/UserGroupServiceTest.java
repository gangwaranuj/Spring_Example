package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.model.OrgUnitDTO;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipant;
import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.dao.UserAvailabilityDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.certification.CertificationDAO;
import com.workmarket.dao.changelog.user.UserChangeLogDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.dao.insurance.InsuranceDAO;
import com.workmarket.dao.license.LicenseDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.groups.dao.GroupMembershipDAO;
import com.workmarket.domains.groups.dao.ManagedCompanyUserGroupDAO;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.domains.groups.dao.UserUserGroupAssociationDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.changelog.user.UserLeftGroupChangeLog;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.service.business.asset.AssetBundlerQueue;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.search.IndexerEvent;
import com.workmarket.service.business.requirementsets.EligibilityService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.orgstructure.OrgStructureService;
import com.workmarket.service.search.group.GroupSearchService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.service.talentpool.TalentPoolService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserGroupServiceTest {

	@Mock private AuthenticationService authenticationService;
	@Mock private AssetManagementService assetService;
	@Mock private CompanyService companyService;
	@Mock private CRMService crmService;
	@Mock private LaneService laneService;
	@Mock private ProfileService profileService;
	@Mock private TagService tagService;
	@Mock private RequestService requestService;
	@Mock private UserIndexer userIndexer;
	@Mock private GroupSearchService groupSearchService;
	@Mock private EventFactory eventFactory;
	@Mock private EventRouter eventRouter;
	@Mock private UserNotificationService userNotificationService;
	@Mock private UserGroupValidationService userGroupValidationService;
	@Mock private SummaryService summaryService;
	@Mock private ServiceMessageHelper messageHelper;
	@Mock private AssetBundlerQueue assetbundler;
	@Mock private CertificationDAO certificationDAO;
	@Mock private CompanyDAO companyDAO;
	@Mock private LicenseDAO licenseDAO;
	@Mock private IndustryDAO industryDAO;
	@Mock private InsuranceDAO insuranceDAO;
	@Mock private UserDAO userDAO;
	@Mock private UserChangeLogDAO userChangeLogDAO;
	@Mock private UserService userService;
	@Mock private UserGroupDAO userGroupDAO;
	@Mock private UserAvailabilityDAO userAvailabilityDAO;
	@Mock private UserUserGroupAssociationDAO userUserGroupAssociationDAO;
	@Mock private ManagedCompanyUserGroupDAO managedCompanyUserGroupDAO;
	@Mock private GroupMembershipDAO groupMembershipDAO;
	@Mock private EligibilityService eligibilityService;
	@Mock private UserRoleService userRoleService;
	@Mock private OrgStructureService orgStructureService;
	@Mock private TalentPoolService talentPoolService;
	@Mock private FeatureEntitlementService featureEntitlementService;
	@InjectMocks UserGroupServiceImpl userGroupService = spy(new UserGroupServiceImpl());

	private static Long USER_ID = 1L;
	private static Long INVITED_BY_USER_ID = 10L;
	private static Long GROUP_ID = 2L;
	private static Long COMPANY_ID = 3L;
	private static Long GROUP_OWNER_COMPANY_ID = 4L;

	User user;
	UserGroup userGroup;
	UserUserGroupAssociation userUserGroupAssociation;
	Company company, groupOwnerCompany;
	Profile profile;
	Eligibility eligibility;

	@Before
	public void setup() {
		user = mock(User.class);
		userGroup = mock(UserGroup.class);
		company = mock(Company.class);
		groupOwnerCompany = mock(Company.class);
		profile = mock(Profile.class);
		userUserGroupAssociation = mock(UserUserGroupAssociation.class);
		eligibility = mock(Eligibility.class);

		when(user.getId()).thenReturn(USER_ID);
		when(user.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(COMPANY_ID);
		when(userService.getUser(USER_ID)).thenReturn(user);
		when(companyService.findCompanyById(COMPANY_ID)).thenReturn(company);
		when(profileService.findProfile(USER_ID)).thenReturn(profile);

		doReturn(userGroup).when(userGroupService).findGroupById(GROUP_ID);
		when(userGroup.getCompany()).thenReturn(groupOwnerCompany);
		when(userGroup.getId()).thenReturn(GROUP_ID);
		when(userGroup.getOpenMembership()).thenReturn(true);
		when(groupOwnerCompany.getId()).thenReturn(GROUP_OWNER_COMPANY_ID);

		when(userUserGroupAssociation.getUser()).thenReturn(user);
		when(userUserGroupAssociation.getUserGroup()).thenReturn(userGroup);

		when(userGroupService.findAssociationByGroupIdAndUserId(GROUP_ID, USER_ID)).thenReturn(userUserGroupAssociation);
		when(userUserGroupAssociationDAO.findUserUserGroupAssociationByUserGroupIdAndUserId(GROUP_ID, USER_ID)).thenReturn(userUserGroupAssociation);

		when(eligibilityService.getEligibilityFor(USER_ID, userGroup)).thenReturn(eligibility);
	}

	@Test
	public void applyToGroup_workerPassesRequirements_workerHasNoLaneAssociationWithBuyer_AndWorkerHasNoLane3Flag_AndGroupDoesNotRequireApproval_thenDoNotCreateAssociation() {
		when(authenticationService.isLane3Active(user)).thenReturn(false);
		when(userGroup.getRequiresApproval()).thenReturn(false);

		userGroupService.applyToGroup(GROUP_ID, USER_ID);
		verify(laneService, never()).addUserToCompanyLane3(USER_ID, COMPANY_ID);
	}

	@Test
	public void applyToGroup_workerPassesRequirements_workerHasNoLaneAssociationWithBuyer_AndWorkerHasLane3Flag_AndGroupDoesNotRequireApproval_thenDoNotCreateAssociation() {
		when(authenticationService.isLane3Active(user)).thenReturn(false);
		when(userGroup.getRequiresApproval()).thenReturn(true);

		userGroupService.applyToGroup(GROUP_ID, USER_ID);
		verify(laneService, never()).addUserToCompanyLane3(USER_ID, COMPANY_ID);
	}

	@Test
	public void applyToGroup_workerPassesRequirements_workerHasNoLaneAssociationWithBuyer_AndWorkerHasLane3Flag_AndGroupRequiresApproval_thenDoNotCreateAssociation() {
		when(authenticationService.isLane3Active(user)).thenReturn(true);
		when(userGroup.getRequiresApproval()).thenReturn(true);

		userGroupService.applyToGroup(GROUP_ID, USER_ID);
		verify(laneService, never()).addUserToCompanyLane3(USER_ID, COMPANY_ID);
	}

	@Test
	public void applyToGroup_workerPassesRequirements_workerHasNoLaneAssociationWithBuyer_AndWorkerHasLane3Flag_AndGroupDoesNotRequireApproval_thenCreateAssociation() {
		when(eligibility.isEligible()).thenReturn(true);

		when(authenticationService.isLane3Active(user)).thenReturn(true);
		when(userGroup.getRequiresApproval()).thenReturn(false);

		userGroupService.applyToGroup(GROUP_ID, USER_ID);
		verify(laneService).addUserToCompanyLane3(USER_ID, GROUP_OWNER_COMPANY_ID);
	}

	@Test
	public void applyToGroup_whenUserGroupAssociationIsNull_createAssociation() {
		when(userGroupService.findAssociationByGroupIdAndUserId(GROUP_ID, USER_ID)).thenReturn(null);
		userGroupService.applyToGroup(GROUP_ID, USER_ID);
		verify(userGroupService).makeUserGroupAssociation(USER_ID, userGroup);
	}

	@Test
	public void applyToGroup_whenUserGroupAssociationIsPresent_doNotCreateAssociation() {
		userGroupService.applyToGroup(GROUP_ID, USER_ID);
		verify(userGroupService, never()).makeUserGroupAssociation(USER_ID, userGroup);
	}

	@Test
	public void applyToGroup_workerPassesRequirements_andTheirAssociationIsPending_andGroupDoesNotRequireApproval_thenSetStatusToVerifiedAndApproved() {
		when(eligibility.isEligible()).thenReturn(true);
		when(userUserGroupAssociation.isPending()).thenReturn(true);
		when(userGroup.getRequiresApproval()).thenReturn(false);

		userGroupService.applyToGroup(GROUP_ID, USER_ID);
		verify(userUserGroupAssociation).setVerificationStatus(VerificationStatus.VERIFIED);
		verify(userUserGroupAssociation).setApprovalStatus(ApprovalStatus.APPROVED);
	}

	@Test
	public void applyToGroup_workerFailsRequirements_setStatusToFailed() {
		userGroupService.applyToGroup(GROUP_ID, USER_ID);
		verify(userUserGroupAssociation).setVerificationStatus(VerificationStatus.FAILED);
	}

	@Test
	public void addUsersToGroup_workerInactive_canNotApply() {
		when(userGroup.getOpenMembership()).thenReturn(false);
		when(authenticationService.isActive(user)).thenReturn(false);
		userGroupService.addUsersToGroup(ImmutableList.of(USER_ID), GROUP_ID, INVITED_BY_USER_ID);
		verify(userGroupService, never()).applyToGroup(any(Long.class), any(Long.class), any(Boolean.class), any(Boolean.class));
	}

	@Test
	public void addUsersToGroup_workerActiveAndSharedWorkerRole_canApply() {
		when(userGroup.getOpenMembership()).thenReturn(false);
		when(authenticationService.isActive(user)).thenReturn(true);
		when(userRoleService.hasAclRole(user, AclRole.ACL_SHARED_WORKER)).thenReturn(true);
		userGroupService.addUsersToGroup(ImmutableList.of(USER_ID), GROUP_ID, INVITED_BY_USER_ID);
		verify(userGroupService).applyToGroup(any(Long.class), any(Long.class), any(Boolean.class), any(Boolean.class));
	}

	@Test
	public void addUsersToGroup_workerActiveAndWorkerRoleSameCompany_canApply() {
		when(userGroup.getOpenMembership()).thenReturn(false);
		when(authenticationService.isActive(user)).thenReturn(true);
		when(userRoleService.hasAclRole(user, AclRole.ACL_WORKER)).thenReturn(true);
		when(user.getCompany().getId()).thenReturn(COMPANY_ID);
		when(userGroup.getCompany().getId()).thenReturn(COMPANY_ID);
		userGroupService.addUsersToGroup(ImmutableList.of(USER_ID), GROUP_ID, INVITED_BY_USER_ID);
		verify(userGroupService).applyToGroup(any(Long.class), any(Long.class), any(Boolean.class), any(Boolean.class));
	}

	@Test
	public void addUsersToGroup_workerActiveAndWorkerRoleDifferentCompany_canNotApply() {
		when(userGroup.getOpenMembership()).thenReturn(false);
		when(authenticationService.isActive(user)).thenReturn(true);
		when(userRoleService.hasAclRole(user, AclRole.ACL_WORKER)).thenReturn(true);
		when(user.getCompany().getId()).thenReturn(COMPANY_ID);
		when(userGroup.getCompany().getId()).thenReturn(COMPANY_ID + 1);
		userGroupService.addUsersToGroup(ImmutableList.of(USER_ID), GROUP_ID, INVITED_BY_USER_ID);
		verify(userGroupService, never()).applyToGroup(any(Long.class), any(Long.class), any(Boolean.class), any(Boolean.class));
	}

	@Test
	public void addUsersToGroup_nullAssociation_invitesUserToGroup() {
		when(userGroup.getOpenMembership()).thenReturn(true);
		when(userUserGroupAssociationDAO.findUserUserGroupAssociationByUserGroupIdAndUserId(GROUP_ID, USER_ID)).thenReturn(null);

		userGroupService.addUsersToGroup(ImmutableList.of(USER_ID), GROUP_ID, INVITED_BY_USER_ID);
		verify(requestService, times(1)).inviteUserToGroup(INVITED_BY_USER_ID, USER_ID, GROUP_ID);
	}

	@Test
	public void addUsersToGroup_deletedAssociation_invitesUserToGroup() {
		when(userGroup.getOpenMembership()).thenReturn(true);
		when(userUserGroupAssociation.getDeleted()).thenReturn(true);

		userGroupService.addUsersToGroup(ImmutableList.of(USER_ID), GROUP_ID, INVITED_BY_USER_ID);
		verify(requestService, times(1)).inviteUserToGroup(INVITED_BY_USER_ID, USER_ID, GROUP_ID);
	}

	@Test
	public void removeAssociation_withAssociation_deleteAssociation() {
		doNothing().when(userGroupService).deleteAssociation(userUserGroupAssociation, true);

		userGroupService.removeAssociation(GROUP_ID, USER_ID);

		verify(userGroupService).deleteAssociation(userUserGroupAssociation, true);
	}

	@Test
	public void removeAssociation_withNoAssociation_doNotDeleteAssociation() {
		when(userUserGroupAssociationDAO.findUserUserGroupAssociationByUserGroupIdAndUserId(anyLong(), anyLong())).thenReturn(null);

		userGroupService.removeAssociation(GROUP_ID, USER_ID);

		verify(userGroupService, never()).deleteAssociation(userUserGroupAssociation, true);
	}

	@Test
	public void deleteAssociation_withNonNullAssociation_doDeleteAssociation() {
		userGroupService.deleteAssociation(userUserGroupAssociation, true);

		verify(userUserGroupAssociation).setDeleted(true);
		verify(userUserGroupAssociation).setApprovalStatus(ApprovalStatus.PENDING);
		verify(userUserGroupAssociation).setVerificationStatus(VerificationStatus.PENDING);
		verify(userUserGroupAssociation).setOverrideMember(false);
	}

	@Test
	public void deleteAssociation_withNonNullAssociation_saveUserLeftGroupChangeLog() {
		userGroupService.deleteAssociation(userUserGroupAssociation, true);

		verify(userChangeLogDAO).saveOrUpdate(any(UserLeftGroupChangeLog.class));
	}

	@Test
	public void deleteAssociation_withNonNullAssociation_saveUserGroupAssociationHistorySummary() {
		userGroupService.deleteAssociation(userUserGroupAssociation, true);

		verify(summaryService).saveUserGroupAssociationHistorySummary(userUserGroupAssociation);
	}

	@Test
	public void deleteAssociation_withReindexTrue_sendThreeReindexEvents() {
		userGroupService.deleteAssociation(userUserGroupAssociation, true);

		verify(eventRouter, times(2)).sendEvent(any(IndexerEvent.class));
	}

	@Test
	public void deleteAssociation_withReindexFalse_sendNoReindexEvents() {
		userGroupService.deleteAssociation(userUserGroupAssociation, false);

		verify(eventRouter, never()).sendEvent(any(IndexerEvent.class));
	}

	@Test
	public void deleteAssociation_withNullAssociation_doNotDeleteAssociation() {
		userGroupService.deleteAssociation(null, true);

		verify(userUserGroupAssociation, never()).setDeleted(true);
		verify(userUserGroupAssociation, never()).setApprovalStatus(ApprovalStatus.PENDING);
		verify(userUserGroupAssociation, never()).setVerificationStatus(VerificationStatus.PENDING);
	}

	@Test
	public void deleteAssociation_withNullAssociation_doNotSaveUserLeftGroupChangeLog() {
		userGroupService.deleteAssociation(null, true);

		verify(userChangeLogDAO, never()).saveOrUpdate(any(UserLeftGroupChangeLog.class));
	}

	@Test
	public void deleteAssociation_withNullAssociation_doNotSaveUserGroupAssociationHistorySummary() {
		userGroupService.deleteAssociation(null, true);

		verify(summaryService, never()).saveUserGroupAssociationHistorySummary(userUserGroupAssociation);
	}

	@Test
	public void deleteAssociation_withNullAssociation_doNotSendThreeReindexEvents() {
		userGroupService.deleteAssociation(null, true);

		verify(eventRouter, never()).sendEvent(any(IndexerEvent.class));
	}

	@Test
	public void findSharedAndOwnedGroups_makesExpectedCallsToServicesAndDAO() {
		when(featureEntitlementService.hasFeatureToggle(anyLong(), anyString())).thenReturn(true);
		when(orgStructureService.getOrgModeSetting(anyLong())).thenReturn("org-mode");
		when(orgStructureService.getSubtreePaths(anyLong(), anyLong(), anyString()))
				.thenReturn(new ArrayList<OrgUnitDTO>());
		when(talentPoolService.getTalentPoolAndParticipants(anyListOf(String.class)))
				.thenReturn(new HashMap<String, List<TalentPoolParticipant>>());
		final ManagedCompanyUserGroupRowPagination pagination = new ManagedCompanyUserGroupRowPagination();
		when(managedCompanyUserGroupDAO.findSharedAndOwnedGroups(
				anyLong(),
				anyLong(),
				anyBoolean(),
				anyMapOf(String.class, OrgUnitDTO.class),
				any(ManagedCompanyUserGroupRowPagination.class))
		).thenReturn(pagination);

		userGroupService.findSharedAndOwnedGroups(USER_ID, COMPANY_ID, pagination);

		verify(orgStructureService, times(1)).getOrgModeSetting(anyLong());
		verify(orgStructureService, times(1)).getSubtreePaths(anyLong(), anyLong(), anyString())	;
		verify(talentPoolService, times(1)).getTalentPoolAndParticipants(anyListOf(String.class));
		verify(managedCompanyUserGroupDAO, times(1)).findSharedAndOwnedGroups(
				anyLong(),
				anyLong(),
				anyBoolean(),
				anyMapOf(String.class, OrgUnitDTO.class),
				any(ManagedCompanyUserGroupRowPagination.class)
		);
	}

	enum RequirementIs {
		MISSING, PRESENT
	}
}
