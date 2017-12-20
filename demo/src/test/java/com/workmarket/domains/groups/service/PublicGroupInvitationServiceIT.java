package com.workmarket.domains.groups.service;

import com.google.common.collect.Lists;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class PublicGroupInvitationServiceIT extends BaseServiceIT {

	@Autowired PublicGroupInvitationService publicGroupInvitationService;
	@Autowired UserGroupService userGroupService;
	@Autowired LaneService laneService;

	User user1;
	User user2;
	User inviter;
	List<User> invitees;
	UserGroup userGroup;

	@Before
	public void setup() throws Exception{
		inviter = newWMEmployee();
		user1 = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane2(user1.getId(),inviter.getCompany().getId());
		user2 = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane2(user2.getId(),inviter.getCompany().getId());
		userGroup = newPublicUserGroup(inviter);
		invitees = Lists.newArrayList();
		invitees.add(user1);
	}


	@Test
	@Transactional
	public void findUsersToInvite_notInvited(){
		List<Long> usersToInvite = publicGroupInvitationService.findUsersToInvite(userGroup.getId(), inviter.getId(), Lists.newArrayList(user1.getId()));
		Assert.assertEquals(1, usersToInvite.size());
	}

	@Test
	public void findUsersToInvite_userAlreadyInvited_notInvitedAgain(){
		userGroupService.applyToGroup(userGroup.getId(),user1.getId());
		List<Long> usersToInvite = publicGroupInvitationService.findUsersToInvite(userGroup.getId(), inviter.getId(), Lists.newArrayList(user1.getId()));
		Assert.assertEquals(0, usersToInvite.size());
	}

	@Test
	@Transactional
	public void findUsersToInvite_userAlreadyInvited_invalidUser_returns(){
		List<Long> usersToInvite = publicGroupInvitationService.findUsersToInvite(userGroup.getId(), inviter.getId(), Lists.newArrayList(Long.MAX_VALUE));
		Assert.assertEquals(0, usersToInvite.size());
	}

	@Test
	public void findUsersToInvite_oneUserAlreadyInvitedOneNotYetInvited_oneUserIdentified(){
		userGroupService.applyToGroup(userGroup.getId(),user1.getId());
		List<Long> usersToInvite = publicGroupInvitationService.findUsersToInvite(userGroup.getId(), inviter.getId(), Lists.newArrayList(user1.getId(), user2.getId()));
		Assert.assertEquals(1, usersToInvite.size());
	}

	@Test
	@Transactional
	public void saveInvitations_success(){
		// just make sure there are no exceptions
		publicGroupInvitationService.saveInvitations(userGroup.getId(), inviter.getId(), null, Lists.newArrayList(user1.getId()));
	}

	@Test
	@Transactional
	public void sendInvitations_success() {
		// just make sure there are no exceptions
		publicGroupInvitationService.sendInvitations(userGroup.getId(), inviter.getId(), Lists.newArrayList(user1.getId()));
	}
}
