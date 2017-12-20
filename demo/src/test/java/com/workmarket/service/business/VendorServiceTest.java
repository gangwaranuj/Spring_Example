package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.business.talentpool.TalentPoolClient;
import com.workmarket.business.talentpool.gen.Messages.ParticipantType;
import com.workmarket.business.talentpool.gen.Messages.Status;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembership;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipList;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipsRequest;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipsResponse;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipation;
import com.workmarket.common.core.RequestContext;
import com.workmarket.service.business.dto.CompanyIdentityDTO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkVendorInvitation;
import com.workmarket.domains.model.WorkVendorInvitationToGroupAssociation;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.dao.WorkVendorInvitationDAO;
import com.workmarket.domains.work.dao.WorkVendorInvitationToGroupAssociationDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.domains.work.service.route.WorkRoutingValidator;
import com.workmarket.dto.TalentPoolMembershipDTO;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VendorServiceTest {

	private static final Long
		USER_ID = 1000L,
		WORK_ID = 100L,
		WORK_TWO_ID = 101L,
		VENDOR_ONE_ID = 1L,
		VENDOR_TWO_ID = 2L,
		VENDOR_THREE_ID = 3L;
	private static final String
		VENDOR_ONE_NUMBER = "1",
		VENDOR_TWO_NUMBER = "2",
		VENDOR_THREE_NUMBER = "3";
	private static final String
		VENDOR_ONE_UUID = "uuid1",
		VENDOR_TWO_UUID = "uuid2",
		VENDOR_THREE_UUID = "uuid3";
	private static final String
		APPROVED_ON = "2017-03-27 05:22:14.7",
		INVITED_ON = "2017-03-27 05:22:14.7";
	private static final String
		TIMEZONE_ID = "US/Eastern";
	private static final Collection<Long> EMPTY_GROUP_ID_COLLECTION = Collections.<Long>emptySet();
	private static final String TP1_UUID = "TP1_UUID";
	private static final String TP2_UUID = "TP2_UUID";
	private static final String TP3_UUID = "TP3_UUID";
	private static Long TP1_ID = 1L;
	private static Long TP2_ID = 2L;
	private static Long TP3_ID = 3L;
	private static final TalentPoolParticipation APPROVED = TalentPoolParticipation
		.newBuilder()
		.setParticipantType(ParticipantType.VENDOR)
		.setApprovedOn(APPROVED_ON).build();
	private static final TalentPoolParticipation INVITED = TalentPoolParticipation
		.newBuilder()
		.setParticipantType(ParticipantType.VENDOR)
		.setInvitedOn(INVITED_ON).build();
	private static final TalentPoolMembershipList MEMBERSHIP_LIST = TalentPoolMembershipList
		.newBuilder()
		.setParticipantUuid(VENDOR_ONE_UUID)
		.addAllTalentPoolMembership(Lists.newArrayList(
			TalentPoolMembership.newBuilder().setTalentPoolUuid(TP1_UUID).setTalentPoolParticipation(APPROVED).build(),
			TalentPoolMembership.newBuilder().setTalentPoolUuid(TP2_UUID).setTalentPoolParticipation(APPROVED).build(),
			TalentPoolMembership.newBuilder().setTalentPoolUuid(TP3_UUID).setTalentPoolParticipation(INVITED).build()))
		.build();
	private static final TalentPoolMembershipsResponse RESPONSE = TalentPoolMembershipsResponse
		.newBuilder()
		.setStatus(Status.newBuilder().setSuccess(true).setMessage("OK"))
		.addAllTalentPoolMembershipList(Lists.newArrayList(MEMBERSHIP_LIST))
		.build();
	private List<String> ALREADY_INVITED_VENDORS = Lists.newArrayList();
	private List<WorkVendorInvitation> EXISTING_INVITATIONS = Lists.newArrayList();
	private Set<String> VENDOR_NUMBERS_TO_INVITE = Sets.newHashSet();
	private List<CompanyIdentityDTO> VENDORS_TO_INVITE = Lists.newArrayList();
	private List<Long> INVITED_VENDOR_IDS = Lists.newArrayList();
	private WorkVendorInvitation workVendorInvitation;
	private User user;
	private Profile profile;
	private Company company;
	private TimeZone tz;

	private CompanyIdentityDTO
		vendorOneIdentity,
		vendorTwoIdentity,
		vendorThreeIdentity;
	private Set<WorkAuthorizationResponse> workAuthorizationResponses;

	@Mock private WorkVendorInvitationDAO workVendorInvitationDAO;
	@Mock private CompanyService companyService;
	@Mock private WorkService workService;
	@Mock private WorkRoutingValidator workRoutingValidator;
	@Mock private WorkBundleService workBundleService;
	@Mock private UserNotificationService userNotificationService;
	@Mock private AuthenticationService authenticationService;
	@Mock private WorkRoutingService workRoutingService;
	@Mock private WorkResourceDAO workResourceDAO;
	@Mock private TalentPoolClient talentPoolClient;
	@Mock private WorkVendorInvitationToGroupAssociationDAO workVendorInvitationToGroupAssociationDAO;
	@Mock private WebRequestContextProvider webRequestContextProvider;
	@Mock private UserGroupService userGroupService;
	@Mock private UserService userService;
	@InjectMocks private VendorServiceImpl vendorService = spy(new VendorServiceImpl());

	@Before
	public void setUp() {
		user = mock(User.class);
		when(userService.findUserById(USER_ID)).thenReturn(user);

		profile = mock(Profile.class);
		when(user.getProfile()).thenReturn(profile);

		tz = mock(TimeZone.class);
		when(profile.getTimeZone()).thenReturn(tz);

		when(tz.getTimeZoneId()).thenReturn(TIMEZONE_ID);

		company = mock(Company.class);
		when(user.getCompany()).thenReturn(company);
		when(company.getCompanyNumber()).thenReturn(VENDOR_ONE_NUMBER);

		when(userGroupService.findUserGroupIdByUuid(TP1_UUID)).thenReturn(TP1_ID);
		when(userGroupService.findUserGroupIdByUuid(TP2_UUID)).thenReturn(TP2_ID);
		when(userGroupService.findUserGroupIdByUuid(TP3_UUID)).thenReturn(TP3_ID);

		workAuthorizationResponses = mock(HashSet.class);
		vendorOneIdentity = mock(CompanyIdentityDTO.class);
		when(vendorOneIdentity.getCompanyId()).thenReturn(VENDOR_ONE_ID);
		when(vendorOneIdentity.getCompanyNumber()).thenReturn(VENDOR_ONE_NUMBER);
		when(vendorOneIdentity.getUuid()).thenReturn(VENDOR_ONE_UUID);

		vendorTwoIdentity = mock(CompanyIdentityDTO.class);
		when(vendorTwoIdentity.getCompanyId()).thenReturn(VENDOR_TWO_ID);
		when(vendorTwoIdentity.getCompanyNumber()).thenReturn(VENDOR_TWO_NUMBER);
		when(vendorTwoIdentity.getUuid()).thenReturn(VENDOR_TWO_UUID);

		vendorThreeIdentity = mock(CompanyIdentityDTO.class);
		when(vendorThreeIdentity.getCompanyId()).thenReturn(VENDOR_THREE_ID);
		when(vendorThreeIdentity.getCompanyNumber()).thenReturn(VENDOR_THREE_NUMBER);
		when(vendorThreeIdentity.getUuid()).thenReturn(VENDOR_THREE_UUID);

		workVendorInvitation = mock(WorkVendorInvitation.class);
		when(workVendorInvitation.getCompanyId()).thenReturn(VENDOR_TWO_ID);
		EXISTING_INVITATIONS.add(new WorkVendorInvitation(WORK_ID, VENDOR_TWO_ID));

		ALREADY_INVITED_VENDORS.add(VENDOR_TWO_NUMBER);
		VENDOR_NUMBERS_TO_INVITE.add(VENDOR_ONE_NUMBER);
		VENDORS_TO_INVITE.add(vendorOneIdentity);
		INVITED_VENDOR_IDS.add(VENDOR_ONE_ID);
		INVITED_VENDOR_IDS.add(VENDOR_TWO_ID);

		when(workVendorInvitationDAO.getVendorNumbersByWork(WORK_ID)).thenReturn(ALREADY_INVITED_VENDORS);
		when(workVendorInvitationDAO.getNotDeclinedVendorNumbersByWork(WORK_ID)).thenReturn(ALREADY_INVITED_VENDORS);
		when(workVendorInvitationDAO.getVendorInvitationsByWork(WORK_ID)).thenReturn(EXISTING_INVITATIONS);
		when(companyService.findCompanyIdentitiesByCompanyNumbers(anyCollectionOf(String.class))).thenReturn(VENDORS_TO_INVITE);
		when(workRoutingValidator.validateWorkForRouting(any(Work.class))).thenReturn(workAuthorizationResponses);
		when(workAuthorizationResponses.contains(WorkAuthorizationResponse.SUCCEEDED)).thenReturn(true);
		when(vendorService.getNotDeclinedVendorIdsByWork(WORK_ID)).thenReturn(INVITED_VENDOR_IDS);

		when(workVendorInvitationDAO.findBy("workId", WORK_ID, "companyId", VENDOR_ONE_ID)).thenReturn(workVendorInvitation);
		when(workVendorInvitationDAO.findBy("workId", WORK_ID, "companyId", VENDOR_TWO_ID)).thenReturn(null);

		when(webRequestContextProvider.getWebRequestContext()).thenCallRealMethod();
		doCallRealMethod().when(webRequestContextProvider).setWebRequestContext(any(WebRequestContext.class));
		when(talentPoolClient.getMemberships(any(TalentPoolMembershipsRequest.class), any(RequestContext.class)))
			.thenReturn(Observable.just(RESPONSE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void inviteVendorsToWork_nullUserId_exceptionThrown() throws WorkNotFoundException {
		vendorService.inviteVendorsToWork(null, WORK_ID, false, EMPTY_GROUP_ID_COLLECTION);
	}

	@Test(expected = IllegalArgumentException.class)
	public void inviteVendorsToWork_nullWorkId_exceptionThrown() throws WorkNotFoundException {
		vendorService.inviteVendorsToWork(Sets.<String>newHashSet(), null, false, EMPTY_GROUP_ID_COLLECTION);
	}

	@Test
	public void inviteVendorsToWork_emptyVendorSet_successfullyInvitedNoVendors() throws WorkNotFoundException {
		WorkRoutingResponseSummary workRoutingResponseSummary = vendorService.inviteVendorsToWork(Sets.<String>newHashSet(), WORK_ID, false, EMPTY_GROUP_ID_COLLECTION);

		assertTrue(workRoutingResponseSummary.isSuccessful());
		assertTrue(CollectionUtils.isEmpty(workRoutingResponseSummary.getResponse().get(WorkAuthorizationResponse.SUCCEEDED)));
	}

	@Test
	public void inviteVendorsToWork_setOfVendorIds_successfullyInvitedVendors() throws WorkNotFoundException {
		WorkRoutingResponseSummary workRoutingResponseSummary = vendorService.inviteVendorsToWork(VENDOR_NUMBERS_TO_INVITE, WORK_ID, false, EMPTY_GROUP_ID_COLLECTION);

		Map response = workRoutingResponseSummary.getResponse();

		assertTrue(workRoutingResponseSummary.isSuccessful());
		assertTrue(response.get(WorkAuthorizationResponse.SUCCEEDED).equals(VENDOR_NUMBERS_TO_INVITE));
		assertFalse(response.containsKey(WorkAuthorizationResponse.ALREADY_INVITED_TO_WORK));
		assertFalse(response.containsKey(WorkAuthorizationResponse.MAX_VENDORS_EXCEEDED));
	}

	@Test
	public void inviteVendorsToWork_onlyDuplicateInvites_noVendorsInvitedOnlyDuplicates() throws WorkNotFoundException {
		ALREADY_INVITED_VENDORS.add(VENDOR_ONE_NUMBER);
		EXISTING_INVITATIONS.add(new WorkVendorInvitation(WORK_ID, VENDOR_ONE_ID));
		when(workVendorInvitationDAO.getVendorNumbersByWork(WORK_ID)).thenReturn(ImmutableList.copyOf(VENDOR_NUMBERS_TO_INVITE));
		when(workVendorInvitationDAO.getVendorInvitationsByWork(WORK_ID)).thenReturn(EXISTING_INVITATIONS);

		WorkRoutingResponseSummary workRoutingResponseSummary = vendorService.inviteVendorsToWork(VENDOR_NUMBERS_TO_INVITE, WORK_ID, false, EMPTY_GROUP_ID_COLLECTION);

		Map response = workRoutingResponseSummary.getResponse();

		assertTrue(response.containsKey(WorkAuthorizationResponse.ALREADY_INVITED_TO_WORK));
		assertTrue(response.get(WorkAuthorizationResponse.ALREADY_INVITED_TO_WORK).equals(VENDOR_NUMBERS_TO_INVITE));
		assertFalse(workRoutingResponseSummary.isSuccessful());
		assertFalse(response.containsKey(WorkAuthorizationResponse.MAX_VENDORS_EXCEEDED));
	}

	@Test
	public void inviteVendorsToWork_oneDuplicateOneNewVendor_onlyNewVendorInvited() throws WorkNotFoundException {
		VENDOR_NUMBERS_TO_INVITE.add(VENDOR_TWO_NUMBER);
		VENDORS_TO_INVITE.add(vendorTwoIdentity);

		WorkRoutingResponseSummary workRoutingResponseSummary = vendorService.inviteVendorsToWork(VENDOR_NUMBERS_TO_INVITE, WORK_ID, false, EMPTY_GROUP_ID_COLLECTION);

		Map response = workRoutingResponseSummary.getResponse();

		assertTrue(workRoutingResponseSummary.isSuccessful());
		assertTrue(response.containsKey(WorkAuthorizationResponse.ALREADY_INVITED_TO_WORK));
		assertTrue(response.get(WorkAuthorizationResponse.SUCCEEDED).equals(Sets.newHashSet(VENDOR_ONE_NUMBER)));
		assertTrue(response.get(WorkAuthorizationResponse.ALREADY_INVITED_TO_WORK).equals(Sets.newHashSet(VENDOR_TWO_NUMBER)));
		assertFalse(response.containsKey(WorkAuthorizationResponse.MAX_VENDORS_EXCEEDED));
	}

	@Test
	public void inviteVendorsToWork_twoVendorsInvited_OneMoreThanInviteThreshold_onlyOneVendorInvited() throws WorkNotFoundException {
		VENDOR_NUMBERS_TO_INVITE.add(VENDOR_THREE_NUMBER);
		VENDORS_TO_INVITE.add(vendorThreeIdentity);

		List<WorkVendorInvitation> nearMaxInvitations = Lists.newArrayListWithCapacity(vendorService.getMaxVendorsPerAssignment());
		for (int ix = 1; ix <= vendorService.getMaxVendorsPerAssignment() / 10; ix++) {
			long multiplier = ix * 100;
			for (int i = 0; i < 10; i++) {
				nearMaxInvitations.add(new WorkVendorInvitation(WORK_ID, multiplier + i));
			}
		}
		nearMaxInvitations.remove(vendorService.getMaxVendorsPerAssignment() - 1);

		when(workVendorInvitationDAO.getVendorInvitationsByWork(WORK_ID)).thenReturn(nearMaxInvitations);

		WorkRoutingResponseSummary workRoutingResponseSummary = vendorService.inviteVendorsToWork(VENDOR_NUMBERS_TO_INVITE, WORK_ID, false, EMPTY_GROUP_ID_COLLECTION);
		Map<WorkAuthorizationResponse, Set<String>> response = workRoutingResponseSummary.getResponse();

		assertTrue(workRoutingResponseSummary.isSuccessful());
		assertEquals(response.get(WorkAuthorizationResponse.SUCCEEDED).size(), 1);
		assertTrue(response.containsKey(WorkAuthorizationResponse.MAX_VENDORS_EXCEEDED));
		assertEquals(response.get(WorkAuthorizationResponse.MAX_VENDORS_EXCEEDED).size(), 1);
		assertFalse(response.containsKey(WorkAuthorizationResponse.ALREADY_INVITED_TO_WORK));
	}

	@Test(expected = IllegalArgumentException.class)
	public void hasInvitedAtLeastOneVendor_nullWorkId_exceptionThrown() {
		vendorService.hasInvitedAtLeastOneVendor(null);
	}

	@Test
	public void hasInvitedAtLeastOneVendor_workId_DaoCalled() {
		vendorService.hasInvitedAtLeastOneVendor(WORK_ID);

		verify(workVendorInvitationDAO).hasInvitedAtLeastOneVendor(WORK_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getVendorIdsByWork_nullWorkId_exceptionThrown() {
		vendorService.getNotDeclinedVendorIdsByWork(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getVendorNumbersByWork_nullWorkId_exceptionThrown() {
		vendorService.getNotDeclinedVendorNumbersByWork(null);
	}

	@Test
	public void getVendorIdsByWork_workId_DaoCalled() {
		vendorService.getNotDeclinedVendorIdsByWork(WORK_ID);

		verify(workVendorInvitationDAO).getNotDeclinedVendorIdsByWork(WORK_ID);
	}

	@Test
	public void getVendorNumbersByWork_workId_DaoCalled() {
		vendorService.getNotDeclinedVendorNumbersByWork(WORK_ID);

		verify(workVendorInvitationDAO).getNotDeclinedVendorNumbersByWork(WORK_ID);
	}

	@Test
	public void copyVendors_success() {
		vendorService.copyVendorsFromWorkToWork(WORK_ID, WORK_TWO_ID);

		verify(workVendorInvitationDAO).saveAll(anyCollection());
	}

	@Test
	public void vendorDeclines_success() {
		vendorService.declineWork(WORK_ID, VENDOR_ONE_ID, USER_ID);

		verify(workVendorInvitation).setIsDeclined((true));
	}

	@Test
	public void vendorDeclinesWorkerDeclines_success() {
		when(workResourceDAO.getAllWorkersFromCompanyInvitedToWork(VENDOR_ONE_ID, WORK_ID)).thenReturn(ImmutableList.of(USER_ID));
		vendorService.declineWork(WORK_ID, VENDOR_ONE_ID, USER_ID);

		verify(workService).declineWork(USER_ID, WORK_ID);
	}

	@Test
	public void vendorDeclinesCreatesAssociation_success() {
		vendorService.declineWork(WORK_ID, VENDOR_TWO_ID, USER_ID);

		verify(workVendorInvitationDAO).saveOrUpdate(any(WorkVendorInvitation.class));
	}

	@Test
	public void vendorInvitedByGroup_getVendorToGroupMap() {
		List<CompanyIdentityDTO> companyIdentityDTOList =
			Lists.newArrayList(vendorOneIdentity, vendorTwoIdentity);
		UserGroup userGroup1 =  new UserGroup();
		userGroup1.setId(TP1_ID);
		userGroup1.setUuid(TP1_UUID);
		UserGroup userGroup2 = new UserGroup();
		userGroup2.setId(TP2_ID);
		userGroup2.setUuid(TP2_UUID);
		List<UserGroup> userGroups = Lists.newArrayList(userGroup1, userGroup2);

		Map<Long, Set<Long>> vendorGroupMap = vendorService.getVendorUserGroupMemberships(companyIdentityDTOList, userGroups);
		assertTrue(vendorGroupMap.size() == 1);
		assertTrue(vendorGroupMap.containsKey(VENDOR_ONE_ID));
		assertEquals(2, vendorGroupMap.get(VENDOR_ONE_ID).size());
		assertTrue(vendorGroupMap.get(VENDOR_ONE_ID).contains(TP1_ID));
		assertTrue(vendorGroupMap.get(VENDOR_ONE_ID).contains(TP2_ID));
	}

	@Test
	public void vendorIsGroupMember_getAllVendorUserGroupMembershipsReturnsGroup() {
		Map<String, Long> groupIds = Maps.newHashMap();
		groupIds.put(TP1_UUID, TP1_ID);
		groupIds.put(TP2_UUID, TP2_ID);
		groupIds.put(TP3_UUID, TP3_ID);
		when(userGroupService.findUserGroupUuidIdPairsByUuids(anyList())).thenReturn(groupIds);

		TalentPoolMembershipDTO dto = vendorService.getAllVendorUserGroupMemberships(USER_ID);
		assertTrue(dto.getMemberships().containsKey(TP1_ID));
		assertTrue(dto.getMemberships().containsKey(TP2_ID));
		assertTrue(dto.getInvitations().containsKey(TP3_ID));
	}

	@Test
	public void vendorInvitedByGroup_alreadyInvitedByOtherGroup_returnAdditionalGroupAssociation() {
		final Long vendorId =  1234L;
		final Long newGroupId = 3L; // vendor 1234L is invited to work by group_id 3
		final Long invitationId1 = 111L;
		final Long invitationId2 = 112L;
		final Set<Long> groupIdsForVendorInvites = Sets.newHashSet(newGroupId);
		final Set<Long> groupIdOfExistingInvites = Sets.newHashSet(1L, 2L);
		final WorkVendorInvitation invite1 = new WorkVendorInvitation(WORK_ID, vendorId);
		invite1.setId(invitationId1);
		final WorkVendorInvitation invite2 = new WorkVendorInvitation(WORK_ID, 5678L);
		invite2.setId(invitationId2);
		final List<WorkVendorInvitation> existingInvitations = Lists.newArrayList(invite1, invite2);
		final Map<Long, Set<Long>> vendorGroupMap = ImmutableMap.of(vendorId, groupIdsForVendorInvites);
		final Map<Long, Set<Long>> existingVendorGroupAssociations = ImmutableMap.of(vendorId, groupIdOfExistingInvites);

		when(workVendorInvitationDAO.getVendorInvitationGroupAssociationsByWorkId(WORK_ID)).thenReturn(existingVendorGroupAssociations);
		Collection<WorkVendorInvitationToGroupAssociation> additionalAssociations =
			vendorService.createGroupAssociationsForExistingInvitations(WORK_ID, existingInvitations, vendorGroupMap);
		assertTrue(additionalAssociations.size() == 1);
		WorkVendorInvitationToGroupAssociation association = (WorkVendorInvitationToGroupAssociation) additionalAssociations.toArray()[0];
		assertEquals(invitationId1, association.getWorkVendorInvitationUserGroup().getWorkVendorInvitationId());
		assertEquals(newGroupId, association.getWorkVendorInvitationUserGroup().getUserGroupId());
	}

	@Test
	public void addVendorInvitedByGroupAssociations_successWithNewInvitation() {
		final Collection<Long> groupIdsToRoute = Lists.newArrayList(1L);
		final UserGroup userGroup = new UserGroup();
		userGroup.setId(TP1_ID);
		userGroup.setUuid(TP1_UUID);
		final List<CompanyIdentityDTO> vendorIdentityList = Lists.newArrayList(
			new CompanyIdentityDTO(VENDOR_ONE_ID, VENDOR_ONE_NUMBER, VENDOR_ONE_UUID)
		);
		final Long invitationId = 1L;
		final WorkVendorInvitation invitation1 = new WorkVendorInvitation(WORK_ID, VENDOR_ONE_ID);
		invitation1.setId(invitationId);
		final WorkVendorInvitation invitation2 = new WorkVendorInvitation(WORK_ID, VENDOR_THREE_ID);
		invitation2.setId(3L);
		final Set<WorkVendorInvitation> invitations = Sets.newHashSet(invitation1, invitation2);
		final List<WorkVendorInvitation> existingInvitations = Lists.newArrayList();
		when(userGroupService.findGroupById(1L)).thenReturn(userGroup);
		Set<WorkVendorInvitationToGroupAssociation> associations =
			vendorService.addVendorInvitedByGroupAssociations(WORK_ID, groupIdsToRoute, invitations, existingInvitations, vendorIdentityList);
		assertEquals(1, associations.size());
		WorkVendorInvitationToGroupAssociation association = (WorkVendorInvitationToGroupAssociation) associations.toArray()[0];
		assertEquals(TP1_ID, association.getWorkVendorInvitationUserGroup().getUserGroupId());
		assertEquals(invitationId, association.getWorkVendorInvitationUserGroup().getWorkVendorInvitationId());
	}

	@Test
	public void addVendorInvitedByGroupAssociations_successWithExistingInvitation() {
		final Collection<Long> groupIdsToRoute = Lists.newArrayList(1L);
		final UserGroup userGroup = new UserGroup();
		userGroup.setId(TP1_ID);
		userGroup.setUuid(TP1_UUID);
		final List<CompanyIdentityDTO> vendorIdentityList = Lists.newArrayList(
			new CompanyIdentityDTO(VENDOR_ONE_ID, VENDOR_ONE_NUMBER, VENDOR_ONE_UUID)
		);
		final Long invitationId = 1L;
		final WorkVendorInvitation invitation1 = new WorkVendorInvitation(WORK_ID, VENDOR_ONE_ID);
		invitation1.setId(invitationId);
		final WorkVendorInvitation invitation2 = new WorkVendorInvitation(WORK_ID, VENDOR_THREE_ID);
		invitation2.setId(3L);
		final Set<WorkVendorInvitation> invitations = Sets.newHashSet();
		final List<WorkVendorInvitation> existingInvitations = Lists.newArrayList(invitation1, invitation2);
		final Set<Long> existingGroupAssociationsForVendorOne = Sets.newHashSet(TP2_ID);
		final Map<Long, Set<Long>> existingVendorGroupAssociations =
			ImmutableMap.of(VENDOR_ONE_ID, existingGroupAssociationsForVendorOne);
		when(userGroupService.findGroupById(1L)).thenReturn(userGroup);
		when(workVendorInvitationDAO.getVendorInvitationGroupAssociationsByWorkId(WORK_ID)).thenReturn(existingVendorGroupAssociations);
		Set<WorkVendorInvitationToGroupAssociation> associations =
			vendorService.addVendorInvitedByGroupAssociations(WORK_ID, groupIdsToRoute, invitations, existingInvitations, vendorIdentityList);
		assertEquals(1, associations.size());
		WorkVendorInvitationToGroupAssociation association = (WorkVendorInvitationToGroupAssociation) associations.toArray()[0];
		assertEquals(TP1_ID, association.getWorkVendorInvitationUserGroup().getUserGroupId());
		assertEquals(invitationId, association.getWorkVendorInvitationUserGroup().getWorkVendorInvitationId());
	}
}
