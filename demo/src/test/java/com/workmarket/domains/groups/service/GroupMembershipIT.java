package com.workmarket.domains.groups.service;

import com.google.common.collect.Lists;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.RequestService;
import com.workmarket.test.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class GroupMembershipIT extends UserGroupBaseIT {

	@Autowired RequestService requestService;

	@Test
	public void findAllUsersOfGroupById_groupHasRequirements_workerMembershipOverridden_workerIsMemberOfGroup() throws Exception {
		buyer = newEmployeeWithCashBalance();
		worker = newContractorIndependentlane4Ready();
		createGroupWithReqs();

		userGroupService.applyToGroup(groupWithReqs.getId(), worker.getId());
		userGroupService.approveUser(groupWithReqs.getId(), worker.getId());

		List<User> users = userGroupService.findAllUsersOfGroupById(groupWithReqs.getId());
		assertThat(users, hasItem(Matchers.<User>hasProperty("id", equalTo(worker.getId()))));
	}

	@Test
	public void findAllUserIdsOfGroup_OneMemberOneNot() throws Exception {
		buyer = newEmployeeWithCashBalance();
		User worker1 = newContractorIndependentlane4Ready();
		User worker2 = newContractorIndependentlane4Ready();

		UserGroup userGroup = generateUserGroup(
			buyer.getCompany().getId(), REQUIRES_APPROVAL, OPEN_MEMBERSHIP, buyer.getId()
		);

		// both workers apply
		userGroupService.applyToGroup(userGroup.getId(), worker1.getId());
		userGroupService.applyToGroup(userGroup.getId(), worker2.getId());

		// only one gets accepted
		userGroupService.approveUser(userGroup.getId(), worker1.getId());

		List<Long> userIds = userGroupService.findAllUserIdsOfGroup(userGroup.getId());
		assertEquals(1, userIds.size());
		assertEquals(worker1.getId(), userIds.iterator().next());
		assertFalse(userIds.contains(worker2.getId()));
	}

	@Test
	public void removeUserFromAllCompanysGroupAssociationsAndInvitations() throws Exception {
		worker = newContractorIndependentlane4Ready();
		buyer = newFirstEmployee();
		Long companyId = buyer.getCompany().getId();
		Long buyerId = buyer.getId();
		group1 = generateUserGroup(companyId, DOES_NOT_REQUIRE_APPROVAL, OPEN_MEMBERSHIP, buyerId);
		group2 = generateUserGroup(companyId, DOES_NOT_REQUIRE_APPROVAL, OPEN_MEMBERSHIP, buyerId);

		// Add worker to group1
		userGroupService.applyToGroup(group1.getId(), worker.getId());
		userGroupService.approveUser(group1.getId(), worker.getId());

		assertTrue(userGroupService.isUserMemberOfGroup(group1.getId(), worker.getId()));

		// Send invites to group2 for worker
		List<Long> userIds = Lists.newArrayList(worker.getId());
		userGroupService.addUsersToGroup(userIds, group2.getId(), buyerId);
		userGroupService.addUsersToGroup(userIds, group2.getId(), buyerId);

		assertTrue(requestService.userHasInvitationToGroup(worker.getId(), group2.getId()));

		// Block user
		userService.blockUserFromCompany(buyerId, worker.getId(), companyId);

		// Make sure user isn't a member and that there are no active invites
		assertFalse(userGroupService.isUserMemberOfGroup(group1.getId(), worker.getId()));
		assertEquals(
			0,
			requestService.findUserGroupInvitationsByUser(worker.getId()).size()
		);
	}

	@Test
	public void addUserToGroupThenRemoveThenAddAgain_userIsMemberOfGroup() throws Exception {
		worker = newContractorIndependentlane4Ready();
		buyer = newFirstEmployee();
		Long companyId = buyer.getCompany().getId();
		Long buyerId = buyer.getId();
		group1 = generateUserGroup(companyId, DOES_NOT_REQUIRE_APPROVAL, OPEN_MEMBERSHIP, buyerId);

		// Add worker to group
		userGroupService.applyToGroup(group1.getId(), worker.getId());
		userGroupService.approveUser(group1.getId(), worker.getId());

		assertTrue(userGroupService.isUserMemberOfGroup(group1.getId(), worker.getId()));

		// Remove user from group
		userGroupService.removeAssociation(group1.getId(), worker.getId());

		assertFalse(userGroupService.isUserMemberOfGroup(group1.getId(), worker.getId()));

		// Add worker to group
		userGroupService.applyToGroup(group1.getId(), worker.getId());
		userGroupService.approveUser(group1.getId(), worker.getId());

		assertTrue(userGroupService.isUserMemberOfGroup(group1.getId(), worker.getId()));
	}
}
