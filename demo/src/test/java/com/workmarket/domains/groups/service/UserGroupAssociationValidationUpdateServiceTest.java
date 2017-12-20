package com.workmarket.domains.groups.service;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.groups.service.UserGroupAssociationValidationUpdateServiceImpl.AssociationUpdateType;
import com.workmarket.domains.groups.service.association.UserGroupAssociationService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserGroupService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserGroupAssociationValidationUpdateServiceTest {

	@Mock private RequestService requestService;
	@Mock private UserGroupAssociationService userGroupAssociationService;
	@Mock private UserGroupService userGroupService;

	@InjectMocks private UserGroupAssociationValidationUpdateServiceImpl userGroupAssociationValidationUpdateService;

	User user;

	UserGroup userGroup;
	UserUserGroupAssociation userUserGroupAssociation;

	private static final double FIT_SCORE = 50.0;
	private static final long ASSOCIATION_ID = 5L;
	private static final long GROUP_CREATOR_ID = 4L;
	private static final long GROUP_ID = 1L;
	private static final long USER_ID = 3L;
	private static final UserGroupInvitationType INVITATION_TYPE = UserGroupInvitationType.CRITERIA_MODIFICATION;

	@Before
	public void before() {
		user = mock(User.class);
		userGroup = mock(UserGroup.class);
		userUserGroupAssociation = mock(UserUserGroupAssociation.class);

		when(user.getId()).thenReturn(USER_ID);

		when(userGroup.getId()).thenReturn(GROUP_ID);
		when(userGroup.getCreatorId()).thenReturn(GROUP_CREATOR_ID);

		when(userUserGroupAssociation.getUserGroup()).thenReturn(userGroup);
		when(userUserGroupAssociation.getUser()).thenReturn(user);
		when(userUserGroupAssociation.getId()).thenReturn(ASSOCIATION_ID);
	}

	@Test
	public void getMetAssociationUpdateTypes_validUnverified() {
		when(userUserGroupAssociation.getApprovalStatus()).thenReturn(ApprovalStatus.APPROVED);
		when(userUserGroupAssociation.getVerificationStatus()).thenReturn(VerificationStatus.UNVERIFIED);

		Set<AssociationUpdateType> associationUpdateTypes =
			userGroupAssociationValidationUpdateService.getMetRequirementsAssociationUpdateTypes(userUserGroupAssociation);

		assertTrue(associationUpdateTypes.contains(AssociationUpdateType.MEETS_REQUIREMENTS_SET_VERIFIED));
	}

	@Test
	public void getMetAssociationUpdateTypes_validPending() {
		when(userGroup.getRequiresApproval()).thenReturn(false);
		when(userUserGroupAssociation.getApprovalStatus()).thenReturn(ApprovalStatus.PENDING);
		when(userUserGroupAssociation.getVerificationStatus()).thenReturn(VerificationStatus.VERIFIED);

		Set<AssociationUpdateType> associationUpdateTypes =
			userGroupAssociationValidationUpdateService.getMetRequirementsAssociationUpdateTypes(userUserGroupAssociation);

		assertTrue(associationUpdateTypes.contains(AssociationUpdateType.MEETS_REQUIREMENTS_SET_APPROVED));
	}

	@Test
	public void getFailedAssociationUpdateTypes_invalidNotFailed() {
		when(userUserGroupAssociation.getApprovalStatus()).thenReturn(ApprovalStatus.PENDING);
		when(userUserGroupAssociation.getVerificationStatus()).thenReturn(VerificationStatus.VERIFIED);

		Set<AssociationUpdateType> associationUpdateTypes =
			userGroupAssociationValidationUpdateService.getFailedRequirementsAssociationUpdateTypes(userUserGroupAssociation);

		assertTrue(associationUpdateTypes.contains(AssociationUpdateType.FAILS_REQUIREMENTS_SET_UNVERIFIED));
	}

	@Test
	public void getFailedAssociationUpdateTypes_invalidApproved() {
		when(userUserGroupAssociation.getApprovalStatus()).thenReturn(ApprovalStatus.APPROVED);
		when(userUserGroupAssociation.getVerificationStatus()).thenReturn(VerificationStatus.UNVERIFIED);

		Set<AssociationUpdateType> associationUpdateTypes =
			userGroupAssociationValidationUpdateService.getFailedRequirementsAssociationUpdateTypes(userUserGroupAssociation);

		assertTrue(associationUpdateTypes.contains(AssociationUpdateType.FAILS_REQUIREMENTS_SET_PENDING));
	}

	@Test
	public void updateAssociation_metRequirements_setVerified() {
		userGroupAssociationValidationUpdateService.updateAssociation(
			userUserGroupAssociation,
			EnumSet.of(AssociationUpdateType.MEETS_REQUIREMENTS_SET_VERIFIED),
			INVITATION_TYPE,
			FIT_SCORE
		);

		verify(userGroupAssociationService)
			.updateUserUserGroupAssociation(ASSOCIATION_ID, VerificationStatus.VERIFIED, FIT_SCORE);
	}

	@Test
	public void updateAssociation_failedRequirements_setUnverified() {
		userGroupAssociationValidationUpdateService.updateAssociation(
			userUserGroupAssociation,
			EnumSet.of(AssociationUpdateType.FAILS_REQUIREMENTS_SET_UNVERIFIED),
			INVITATION_TYPE,
			FIT_SCORE
		);

		verify(userGroupAssociationService)
			.updateUserUserGroupAssociation(ASSOCIATION_ID, VerificationStatus.FAILED, FIT_SCORE);
	}

	@Test
	public void updateAssociation_metRequirements_setApproved() {
		userGroupAssociationValidationUpdateService.updateAssociation(
			userUserGroupAssociation,
			EnumSet.of(AssociationUpdateType.MEETS_REQUIREMENTS_SET_APPROVED),
			INVITATION_TYPE,
			FIT_SCORE
		);

		verify(userGroupAssociationService)
			.updateUserUserGroupAssociation(ASSOCIATION_ID, ApprovalStatus.PENDING, FIT_SCORE);
		verify(userGroupService)
			.approveUserAssociations(userGroup.getId(), ImmutableList.of(USER_ID));
	}

	@Test
	public void updateAssociation_failedRequirements_setPending() {
		userGroupAssociationValidationUpdateService.updateAssociation(
			userUserGroupAssociation,
			EnumSet.of(AssociationUpdateType.FAILS_REQUIREMENTS_SET_PENDING),
			INVITATION_TYPE,
			FIT_SCORE
		);

		verify(userGroupAssociationService)
			.updateUserUserGroupAssociation(ASSOCIATION_ID, ApprovalStatus.PENDING, FIT_SCORE);
	}

}
