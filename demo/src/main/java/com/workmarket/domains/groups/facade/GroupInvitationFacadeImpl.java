package com.workmarket.domains.groups.facade;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.service.GroupInvitationService;
import com.workmarket.domains.groups.service.GroupInvitationServiceFactory;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.util.List;

@Service
public class GroupInvitationFacadeImpl implements GroupInvitationFacade {
	private static final Log logger = LogFactory.getLog(GroupInvitationFacadeImpl.class);

	@Autowired private GroupInvitationServiceFactory groupInvitationServiceFactory;
	@Autowired private UserService userService;
	@Autowired private UserGroupService userGroupService;

	@Override
	public void inviteUsersToGroups(Long userGroupId, Long inviterId, Long masqUserId, List<Long> invitedUserIds, UserGroupInvitationType invitationType) {
		Assert.notNull(userGroupId);
		Assert.notNull(inviterId);
		Assert.notNull(invitedUserIds);

		logger.info(String.format("[addToGroup] - BEGIN onEvent - %d users, groupId:%d, inviterId:%s", invitedUserIds.size(), userGroupId, inviterId));

		StopWatch timer = new StopWatch();

		// find our user
		timer.start("1. find requester");
		User inviter = userService.getUser(inviterId);
		Assert.notNull(inviter);
		timer.stop();

		timer.start("2. find group");
		UserGroup userGroup = userGroupService.findGroupById(userGroupId);
		Assert.notNull(userGroup);
		timer.stop();

		timer.start("3. find invitees");
		GroupInvitationService groupInvitationService = groupInvitationServiceFactory.getGroupInvitationService(userGroup);
		List<Long> invitees = groupInvitationService.findUsersToInvite(userGroup.getId(), inviter.getId(), invitedUserIds);
		timer.stop();

		timer.start("4. save invites");
		groupInvitationService.saveInvitations(userGroup.getId(), inviter.getId(), masqUserId, invitees, invitationType);
		timer.stop();

		timer.start("5. send invitations");
		groupInvitationService.sendInvitations(userGroup.getId(), inviter.getId(), invitees);
		timer.stop();

		timer.start("6. refresh solr");
		groupInvitationService.refreshUserGroups(invitees);
		timer.stop();

		timer.start("7. Reindex the group");
		groupInvitationService.refreshGroups(userGroup.getId());
		timer.stop();

		logger.info("[addToGroup] - END onEvent: " + timer.prettyPrint());
	}

	@Override
	public void inviteUsersToGroups(Long userGroupId, Long inviterId, Long masqUserId, List<Long> invitedUserIds) {
		inviteUsersToGroups(userGroupId, inviterId, masqUserId, invitedUserIds, UserGroupInvitationType.NEW);
	}
}
