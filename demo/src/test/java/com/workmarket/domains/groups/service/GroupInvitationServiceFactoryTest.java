package com.workmarket.domains.groups.service;

import com.workmarket.domains.groups.model.UserGroup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupInvitationServiceFactoryTest {

	@Mock PublicGroupInvitationService publicGroupInvitationService;
	@Mock PrivateGroupInvitationService privateGroupInvitationService;
	@InjectMocks GroupInvitationServiceFactoryImpl groupInvitationServiceFactory;

	UserGroup userGroup;

	@Before
	public void setup() {
		userGroup = mock(UserGroup.class);
		when(userGroup.getOpenMembership()).thenReturn(true);
	}

	@Test
	public void getGroupInvitationService_openMembership_returnsPublicService() {
		when(userGroup.getOpenMembership()).thenReturn(true);
		GroupInvitationService groupInvitationService = groupInvitationServiceFactory.getGroupInvitationService(userGroup);
		Assert.assertTrue(groupInvitationService instanceof PublicGroupInvitationService);
	}

	@Test
	public void getGroupInvitationService_notOpenMembership_returnsPrivateService() {
		when(userGroup.getOpenMembership()).thenReturn(false);
		GroupInvitationService groupInvitationService = groupInvitationServiceFactory.getGroupInvitationService(userGroup);
		Assert.assertTrue(groupInvitationService instanceof PrivateGroupInvitationService);
	}

}
