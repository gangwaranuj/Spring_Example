package com.workmarket.domains.groups.service;


import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.search.group.GroupSearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public abstract class GroupInvitationServiceImpl implements GroupInvitationService {
	private static final Log logger = LogFactory.getLog(GroupInvitationServiceImpl.class);

	@Autowired GroupSearchService groupSearchService;
	@Autowired UserGroupService userGroupService;
	@Autowired UserService userService;
	@Autowired UserIndexer userIndexer;

	@Override
	public List<Long> findUsersToInvite(Long groupId, Long inviterId, List<Long> invitedUserIds) {
		Assert.notNull(invitedUserIds);
		Assert.notNull(groupId);
		Assert.notNull(inviterId);
		if (CollectionUtils.isEmpty(invitedUserIds)) {
			logger.warn("No users to invite to group " + groupId + " by user " + inviterId);
			return Collections.emptyList();
		}

		List<Long> notInvitedUserIds = userGroupService.getEligibleUserIdsForInvitationToGroup(invitedUserIds, groupId);
		if (CollectionUtils.isEmpty(notInvitedUserIds)) {
			logger.warn("All " + invitedUserIds.size() + " users have already been invited to (or are in) group " + groupId + " by user " + inviterId);
			return Collections.emptyList();
		}

		// validate the users are valid users
		List<User> validatedNotInvitedUsers = userService.findAllUsersByIds(notInvitedUserIds);
		if (CollectionUtils.isEmpty(validatedNotInvitedUsers)) {
			logger.error("Failed to find any valid users to invite - eligible users was " + notInvitedUserIds.size() + " groupId " + groupId + " by user " + inviterId);
			return Collections.emptyList();
		}

		if (validatedNotInvitedUsers.size() != notInvitedUserIds.size()) {
			logger.warn("Not all users requested are valid users! Requested " + notInvitedUserIds + " but found " + validatedNotInvitedUsers + " validated users for group " + groupId);
		}
		return extract(validatedNotInvitedUsers, on(User.class).getId());
	}


	@Override
	public void refreshUserGroups(List<Long> invitedUserIds) {
		Assert.notNull(invitedUserIds);

		if (CollectionUtils.isEmpty(invitedUserIds)) {
			logger.warn("No invited user ids provided - nothing to refresh");
			return;
		}

		userIndexer.reindexById(invitedUserIds);
	}

	@Override
	public void refreshGroups(Long userGroupId) {
		Assert.notNull(userGroupId);
		groupSearchService.reindexGroup(userGroupId);
	}

}
