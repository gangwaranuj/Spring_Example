package com.workmarket.domains.groups.service;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class PrivateGroupInvitationServiceIT extends UserGroupBaseIT {

	@Autowired PrivateGroupInvitationService privateGroupInvitationService;

	UserGroup userGroup;
	User user1;
	User user2;
	List<User> invitees;
	User inviter;

	@Before
	public void setup() throws Exception{
		inviter = newWMEmployee();
		user1 = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane2(user1.getId(), inviter.getCompany().getId());
		user2 = newContractorIndependentLane4ReadyWithCashBalance();
		laneService.addUserToCompanyLane2(user2.getId(), inviter.getCompany().getId());
		invitees = Lists.newArrayList();
		invitees.add(user1);
		userGroup = newPrivateUserGroup(inviter);
	}

	@Test
	@Transactional
	public void findUsersToInvite_notInvited(){
		List<Long> usersToInvite = privateGroupInvitationService.findUsersToInvite(userGroup.getId(), inviter.getId(), Lists.newArrayList(user1.getId()));
		Assert.assertEquals(1, usersToInvite.size());
	}

	@Test
	public void findUsersToInvite_userAlreadyInvited_notInvitedAgain(){
		userGroupService.applyToGroup(userGroup.getId(), user1.getId());
		List<Long> usersToInvite = privateGroupInvitationService.findUsersToInvite(userGroup.getId(), inviter.getId(), Lists.newArrayList(user1.getId()));
		Assert.assertEquals(0, usersToInvite.size());
	}

	@Test
	@Transactional
	public void findUsersToInvite_userAlreadyInvited_invalidUser_returns(){
		List<Long> usersToInvite = privateGroupInvitationService.findUsersToInvite(userGroup.getId(), inviter.getId(), Lists.newArrayList(Long.MAX_VALUE));
		Assert.assertEquals(0, usersToInvite.size());
	}

	@Test
	public void findUsersToInvite_oneUserAlreadyInvitedOneNotYetInvited_oneUserIdentified(){
		userGroupService.applyToGroup(userGroup.getId(), user1.getId());
		List<Long> usersToInvite = privateGroupInvitationService.findUsersToInvite(userGroup.getId(), inviter.getId(), Lists.newArrayList(user1.getId(), user2.getId()));
		Assert.assertEquals(1, usersToInvite.size());
	}


}
