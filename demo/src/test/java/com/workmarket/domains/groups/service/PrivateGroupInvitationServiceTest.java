package com.workmarket.domains.groups.service;

import com.google.common.collect.Lists;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.changelog.user.UserAppliedToGroupChangeLog;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.UserChangeLogService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.summary.SummaryService;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PrivateGroupInvitationServiceTest {
	private static final Long INVITER_ID = 1L;
	private static final Long MASQ_ID = 2L;
	private static final Long GROUP_ID = 1L;
	private static final Long VERIFIED_INVITEE = 10L;
	private static final Long UNVERIFIED_INVITEE = 20L;


	@Mock UserGroupService userGroupService;
	@Mock UserService userService;
	@Mock LaneService laneService;
	@Mock UserNotificationService userNotificationService;
	@Mock SummaryService summaryService;
	@Mock UserChangeLogService userChangeLogService;
	@Mock UserIndexer userIndexer;
	@Mock PrivateGroupInvitationServiceImpl.FactoryHelper factoryHelper;
	@InjectMocks PrivateGroupInvitationServiceImpl privateGroupInvitationService;

	@Mock List<Long> invitedUserIds;

	@Mock UserGroup userGroup;
	@Mock User verifiedUser;
	@Mock User unverifiedUser;
	@Mock UserUserGroupAssociation verifiedUserUserGroupAssociation;
	@Mock UserUserGroupAssociation unverifiedUserUserGroupAssociation;
	@Mock User masqUser;

	@Before
	public void setup() {
		invitedUserIds = Lists.newArrayList(VERIFIED_INVITEE, UNVERIFIED_INVITEE);

		masqUser = mock(User.class);
		when(userService.getUser(MASQ_ID)).thenReturn(masqUser);

		userGroup = mock(UserGroup.class);
		when(userGroupService.findGroupById(GROUP_ID)).thenReturn(userGroup);

		when(factoryHelper.makeUserAssociations()).thenReturn(new ArrayList<UserUserGroupAssociation>());
		when(factoryHelper.makeInvitations()).thenReturn(new ArrayList<UserGroupInvitation>());

		verifiedUser = mock(User.class);
		when(verifiedUser.getId()).thenReturn(VERIFIED_INVITEE);
		verifiedUserUserGroupAssociation = mock(UserUserGroupAssociation.class);
		when(verifiedUserUserGroupAssociation.getId()).thenReturn(VERIFIED_INVITEE);
		when(verifiedUserUserGroupAssociation.getVerificationStatus()).thenReturn(VerificationStatus.VERIFIED);
		when(verifiedUserUserGroupAssociation.getUser()).thenReturn(verifiedUser);
		when(verifiedUserUserGroupAssociation.getUserGroup()).thenReturn(userGroup);

		unverifiedUser = mock(User.class);
		when(unverifiedUser.getId()).thenReturn(UNVERIFIED_INVITEE);
		unverifiedUserUserGroupAssociation = mock(UserUserGroupAssociation.class);
		when(unverifiedUserUserGroupAssociation.getId()).thenReturn(UNVERIFIED_INVITEE);
		when(unverifiedUserUserGroupAssociation.getVerificationStatus()).thenReturn(VerificationStatus.PENDING);
		when(unverifiedUserUserGroupAssociation.getUser()).thenReturn(unverifiedUser);
		when(unverifiedUserUserGroupAssociation.getUserGroup()).thenReturn(userGroup);

		when(userGroupService.buildUserUserGroupAssociation(eq(VERIFIED_INVITEE), any(UserGroup.class), anyList())).thenReturn(verifiedUserUserGroupAssociation);
		when(userGroupService.buildUserUserGroupAssociation(eq(UNVERIFIED_INVITEE), any(UserGroup.class), anyList())).thenReturn(unverifiedUserUserGroupAssociation);

	}

	@Test(expected = Exception.class)
	public void findUsersToInvite_nullInvitees_Exception() {
		privateGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, null);
	}

	@Test
	public void findUsersToInvite_emptyInvitees_returns_emptyResult() {
		List<Long> usersToInvite = privateGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, new ArrayList<Long>());
		assertNotNull(usersToInvite);
		assertTrue(CollectionUtils.isEmpty(usersToInvite));
	}

	@Test(expected = Exception.class)
	public void findUsersToInvite_nullGroupId_Exception() {
		privateGroupInvitationService.findUsersToInvite(null, INVITER_ID, invitedUserIds);
	}

	@Test(expected = Exception.class)
	public void findUsersToInvite_nullInviterId_Exception() {
		privateGroupInvitationService.findUsersToInvite(GROUP_ID, null, invitedUserIds);
	}

	@Test
	public void findUsersToInvite_allUsersInvited_returns_emptyResult() {
		when(userGroupService.getEligibleUserIdsForInvitationToGroup(invitedUserIds, GROUP_ID)).thenReturn(null);

		List<Long> usersToInvite = privateGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, invitedUserIds);
		assertNotNull(usersToInvite);
		assertTrue(CollectionUtils.isEmpty(usersToInvite));
	}

	@Test
	public void findUsersToInvite_allUsersInvited_returns_emptyResult2() {
		when(userGroupService.getEligibleUserIdsForInvitationToGroup(invitedUserIds, GROUP_ID)).thenReturn(new ArrayList<Long>());

		List<Long> usersToInvite = privateGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, invitedUserIds);
		assertNotNull(usersToInvite);
		assertTrue(CollectionUtils.isEmpty(usersToInvite));
	}

	@Test
	public void findUsersToInvite_noValidUsers_returns_emptyResult() {
		when(userGroupService.getEligibleUserIdsForInvitationToGroup(invitedUserIds, GROUP_ID)).thenReturn(invitedUserIds);
		when(userService.findAllUsersByIds(invitedUserIds)).thenReturn(null);

		List<Long> usersToInvite = privateGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, invitedUserIds);
		assertNotNull(usersToInvite);
		assertTrue(CollectionUtils.isEmpty(usersToInvite));
	}

	@Test
	public void findUsersToInvite_noValidUsers_returns_emptyResult2() {
		when(userGroupService.getEligibleUserIdsForInvitationToGroup(invitedUserIds, GROUP_ID)).thenReturn(invitedUserIds);
		when(userService.findAllUsersByIds(invitedUserIds)).thenReturn(new ArrayList<User>());

		List<Long> usersToInvite = privateGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, invitedUserIds);
		assertNotNull(usersToInvite);
		assertTrue(CollectionUtils.isEmpty(usersToInvite));
	}

	@Test
	public void findUsersToInvite_withValidUsers_success() {
	}

	@Test(expected = Exception.class)
	public void saveInvitations_nullInvitees_Exception() {
		privateGroupInvitationService.saveInvitations(GROUP_ID, INVITER_ID, null, null);
	}

	@Test
	public void saveInvitations_emptyInvitees_returns_withoutProcessing() {
		// just verifying this works
		privateGroupInvitationService.saveInvitations(GROUP_ID, INVITER_ID, null, new ArrayList<Long>());
		verifyZeroInteractions(userGroupService, userNotificationService, userChangeLogService, summaryService, laneService);
	}

	@Test(expected = Exception.class)
	public void saveInvitations_nullGroupId_Exception() {
		privateGroupInvitationService.saveInvitations(null, INVITER_ID, null, invitedUserIds);
	}

	@Test(expected = Exception.class)
	public void saveInvitations_nullInviterId_Exception() {
		privateGroupInvitationService.saveInvitations(GROUP_ID, null, null, invitedUserIds);
	}

	@Test
	public void saveInvitations_success() {
		privateGroupInvitationService.saveInvitations(GROUP_ID, INVITER_ID, null, invitedUserIds);

		verify(factoryHelper, times(1)).makeUserAssociations();
		verify(factoryHelper, times(2)).makeInvitations();

		verify(laneService, times(1)).updateLanesForUserOnGroupApply(verifiedUser, userGroup);
		verify(userNotificationService, times(2)).onUserGroupApplication(any(UserUserGroupAssociation.class));
		verify(userChangeLogService, times(2)).createChangeLog(any(UserAppliedToGroupChangeLog.class));
		verify(summaryService, times(2)).saveUserGroupAssociationHistorySummary(any(UserUserGroupAssociation.class));

		verifyZeroInteractions(userService);
	}

	@Test
	public void saveInvitations_withMasqUser_success() {
		privateGroupInvitationService.saveInvitations(GROUP_ID, INVITER_ID, MASQ_ID, invitedUserIds);

		verify(factoryHelper, times(1)).makeUserAssociations();
		verify(factoryHelper, times(2)).makeInvitations();

		verify(laneService, times(1)).updateLanesForUserOnGroupApply(verifiedUser, userGroup);
		verify(userNotificationService, times(2)).onUserGroupApplication(any(UserUserGroupAssociation.class));
		verify(userChangeLogService, times(2)).createChangeLog(any(UserAppliedToGroupChangeLog.class));
		verify(summaryService, times(2)).saveUserGroupAssociationHistorySummary(any(UserUserGroupAssociation.class));
		verify(userService, times(1)).getUser(MASQ_ID);
	}

	@Test
	public void sendInvitations_nullInvitees_doesNothing() {
		privateGroupInvitationService.sendInvitations(GROUP_ID, INVITER_ID, null);

		// this is not expected to do anything
		verifyZeroInteractions(userGroupService, userService, laneService, userNotificationService, summaryService, userChangeLogService, userIndexer, factoryHelper);
	}

	@Test
	public void sendInvitations_emptyInvitees_doesNothing() {
		privateGroupInvitationService.sendInvitations(GROUP_ID, INVITER_ID, new ArrayList<Long>());

		// this is not expected to do anything
		verifyZeroInteractions(userGroupService, userService, laneService, userNotificationService, summaryService, userChangeLogService, userIndexer, factoryHelper);
	}

	@Test
	public void sendInvitations_nullGroupId_doesNothing() {
		privateGroupInvitationService.sendInvitations(null, INVITER_ID, invitedUserIds);

		// this is not expected to do anything
		verifyZeroInteractions(userGroupService, userService, laneService, userNotificationService, summaryService, userChangeLogService, userIndexer, factoryHelper);
	}

	@Test
	public void sendInvitations_nullInviterId_doesNothing() {
		privateGroupInvitationService.sendInvitations(GROUP_ID, null, invitedUserIds);

		// this is not expected to do anything
		verifyZeroInteractions(userGroupService, userService, laneService, userNotificationService, summaryService, userChangeLogService, userIndexer, factoryHelper);
	}

	@Test
	public void sendInvitations_validParameters_doesNothing() {
		privateGroupInvitationService.sendInvitations(GROUP_ID, INVITER_ID, invitedUserIds);

		// this is not expected to do anything
		verifyZeroInteractions(userGroupService, userService, laneService, userNotificationService, summaryService, userChangeLogService, userIndexer, factoryHelper);
	}

	@Test(expected = Exception.class)
	public void refreshUserGroups_nullInvitees_Exception() {
		privateGroupInvitationService.refreshUserGroups(null);
	}

	@Test
	public void refreshUserGroups_emptyInvitees_returns_withoutProcessing() {
		// just verifying this works
		privateGroupInvitationService.refreshUserGroups(new ArrayList<Long>());
		verifyZeroInteractions(userIndexer);
	}

	@Test
	public void refreshUserGroups_success() {
		// just verifying this works
		privateGroupInvitationService.refreshUserGroups(Lists.newArrayList(1L, 2L));
		verify(userIndexer, times(1)).reindexById(anyList());
	}
}
