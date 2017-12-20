package com.workmarket.domains.groups.service;

import com.google.common.collect.Lists;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.request.FlatRequest;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PublicGroupInvitationServiceTest {

	private static final Long INVITER_ID = 1L;
	private static final Long GROUP_ID = 1L;

	@Mock UserService userService;
	@Mock UserGroupService userGroupService;
	@Mock RequestService requestService;
	@Mock UserIndexer userIndexer;

	@InjectMocks PublicGroupInvitationServiceImpl publicGroupInvitationService;

	@Mock List<Long> invitedUserIds;

	@Before
	public void setup() {
		invitedUserIds = Lists.newArrayList(1L, 2L, 3L);
	}

	@Test(expected = Exception.class)
	public void findUsersToInvite_nullInvitees_Exception() {
		publicGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, null);
	}

	@Test
	public void findUsersToInvite_emptyInvitees_returns_emptyResult() {
		List<Long> usersToInvite = publicGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, new ArrayList<Long>());
		assertNotNull(usersToInvite);
		assertTrue(CollectionUtils.isEmpty(usersToInvite));
	}

	@Test(expected = Exception.class)
	public void findUsersToInvite_nullGroupId_Exception() {
		publicGroupInvitationService.findUsersToInvite(null, INVITER_ID, invitedUserIds);
	}

	@Test(expected = Exception.class)
	public void findUsersToInvite_nullInviterId_Exception() {
		publicGroupInvitationService.findUsersToInvite(GROUP_ID, null, invitedUserIds);
	}

	@Test
	public void findUsersToInvite_allUsersInvited_returns_emptyResult() {
		when(userGroupService.getEligibleUserIdsForInvitationToGroup(invitedUserIds, GROUP_ID)).thenReturn(null);

		List<Long> usersToInvite = publicGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, invitedUserIds);
		assertNotNull(usersToInvite);
		assertTrue(CollectionUtils.isEmpty(usersToInvite));
	}

	@Test
	public void findUsersToInvite_allUsersInvited_returns_emptyResult2() {
		when(userGroupService.getEligibleUserIdsForInvitationToGroup(invitedUserIds, GROUP_ID)).thenReturn(new ArrayList<Long>());

		List<Long> usersToInvite = publicGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, invitedUserIds);
		assertNotNull(usersToInvite);
		assertTrue(CollectionUtils.isEmpty(usersToInvite));
	}

	@Test
	public void findUsersToInvite_noValidUsers_returns_emptyResult() {
		when(userGroupService.getEligibleUserIdsForInvitationToGroup(invitedUserIds, GROUP_ID)).thenReturn(invitedUserIds);
		when(userService.findAllUsersByIds(invitedUserIds)).thenReturn(null);

		List<Long> usersToInvite = publicGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, invitedUserIds);
		assertNotNull(usersToInvite);
		assertTrue(CollectionUtils.isEmpty(usersToInvite));
	}

	@Test
	public void findUsersToInvite_noValidUsers_returns_emptyResult2() {
		when(userGroupService.getEligibleUserIdsForInvitationToGroup(invitedUserIds, GROUP_ID)).thenReturn(invitedUserIds);
		when(userService.findAllUsersByIds(invitedUserIds)).thenReturn(new ArrayList<User>());

		List<Long> usersToInvite = publicGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, invitedUserIds);
		assertNotNull(usersToInvite);
		assertTrue(CollectionUtils.isEmpty(usersToInvite));
	}

	@Test
	public void findUsersToInvite_withValidUsers_success() {
		User u1 = new User();
		User u2 = new User();
		u1.setId(1L);
		u2.setId(2L);

		when(userGroupService.getEligibleUserIdsForInvitationToGroup(invitedUserIds, GROUP_ID)).thenReturn(invitedUserIds);
		when(userService.findAllUsersByIds(invitedUserIds)).thenReturn(Lists.newArrayList(u1, u2));

		List<Long> usersToInvite = publicGroupInvitationService.findUsersToInvite(GROUP_ID, INVITER_ID, invitedUserIds);

		assertNotNull(usersToInvite);
		assertEquals(2, usersToInvite.size());
		assertTrue(usersToInvite.contains(1L));
		assertTrue(usersToInvite.contains(2L));
	}

	@Test(expected = Exception.class)
	public void saveInvitations_nullInvitees_Exception() {
		publicGroupInvitationService.saveInvitations(GROUP_ID, INVITER_ID, null, null);
	}

	@Test
	public void saveInvitations_emptyInvitees_returns_withoutProcessing() {
		// just verifying this works
		publicGroupInvitationService.saveInvitations(GROUP_ID, INVITER_ID, null, new ArrayList<Long>());
		verifyZeroInteractions(requestService);
	}

	@Test(expected = Exception.class)
	public void saveInvitations_nullGroupId_Exception() {
		publicGroupInvitationService.saveInvitations(null, INVITER_ID, null, invitedUserIds);
	}

	@Test(expected = Exception.class)
	public void saveInvitations_nullInviterId_Exception() {
		publicGroupInvitationService.saveInvitations(GROUP_ID, null, null, invitedUserIds);
	}

	@Test
	public void saveInvitations_success() {
		FlatRequest mockRequest1 = mock(FlatRequest.class);
		FlatRequest mockRequest2 = mock(FlatRequest.class);
		when(requestService.createFlatInvitesForInviteUsersToGroup(invitedUserIds, INVITER_ID, GROUP_ID, UserGroupInvitationType.NEW)).thenReturn(Lists.newArrayList(mockRequest1, mockRequest2));

		publicGroupInvitationService.saveInvitations(GROUP_ID, INVITER_ID, null, invitedUserIds);

		verify(requestService, times(1)).createFlatInvitesForInviteUsersToGroup(anyList(), anyLong(), anyLong(), any(UserGroupInvitationType.class));
		verify(requestService, times(1)).saveInvitesForInviteUsersToGroup(anyList());
	}

	@Test
	public void saveInvitations_withRecommendationType_success() {
		FlatRequest mockRequest1 = mock(FlatRequest.class);
		FlatRequest mockRequest2 = mock(FlatRequest.class);
		when(requestService.createFlatInvitesForInviteUsersToGroup(invitedUserIds, INVITER_ID, GROUP_ID, UserGroupInvitationType.RECOMMENDATION)).thenReturn(Lists.newArrayList(mockRequest1, mockRequest2));

		publicGroupInvitationService.saveInvitations(GROUP_ID, INVITER_ID, null, invitedUserIds);

		verify(requestService, times(1)).createFlatInvitesForInviteUsersToGroup(anyList(), anyLong(), anyLong(), any(UserGroupInvitationType.class));
		verify(requestService, times(1)).saveInvitesForInviteUsersToGroup(anyList());
	}

	@Test(expected = Exception.class)
	public void sendInvitations_nullInvitees_Exception() {
		publicGroupInvitationService.sendInvitations(GROUP_ID, INVITER_ID, null);
	}

	@Test
	public void sendInvitations_emptyInvitees_returns_withoutProcessing() {
		// just verifying this works
		publicGroupInvitationService.sendInvitations(GROUP_ID, INVITER_ID, new ArrayList<Long>());
		verifyZeroInteractions(requestService);
	}

	@Test(expected = Exception.class)
	public void sendInvitations_nullGroupId_Exception() {
		publicGroupInvitationService.sendInvitations(null, INVITER_ID, invitedUserIds);
	}

	@Test(expected = Exception.class)
	public void sendInvitations_nullInviterId_Exception() {
		publicGroupInvitationService.sendInvitations(GROUP_ID, null, invitedUserIds);
	}

	@Test
	public void sendInvitations_success() {
		// just verifying this works
		publicGroupInvitationService.sendInvitations(GROUP_ID, INVITER_ID, Lists.newArrayList(1L, 2L));
		verify(requestService, times(1)).sendInvitesForAddUserToGroup(eq(GROUP_ID), eq(INVITER_ID), anyList());
	}

	@Test(expected = Exception.class)
	public void refreshUserGroups_nullInvitees_Exception() {
		publicGroupInvitationService.refreshUserGroups(null);
	}

	@Test
	public void refreshUserGroups_emptyInvitees_returns_withoutProcessing() {
		// just verifying this works
		publicGroupInvitationService.refreshUserGroups(new ArrayList<Long>());
		verifyZeroInteractions(userIndexer);
	}

	@Test
	public void refreshUserGroups_success() {
		// just verifying this works
		publicGroupInvitationService.refreshUserGroups(Lists.newArrayList(1L, 2L));
		verify(userIndexer, times(1)).reindexById(anyList());
	}

}
