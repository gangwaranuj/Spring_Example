package com.workmarket.domains.groups.service;

import com.google.common.collect.Lists;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.request.FlatRequest;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserGroupService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class PublicGroupInvitationServiceImpl extends GroupInvitationServiceImpl implements PublicGroupInvitationService {

	@Component
	static class FactoryHelper {
		List<User> makeInvitedUsers() {
			return Lists.newArrayList();
		}
	}

	private static final Log logger = LogFactory.getLog(PublicGroupInvitationServiceImpl.class);

	@Autowired UserGroupService userGroupService;
	@Autowired RequestService requestService;

	@Override
	public void saveInvitations(Long userGroupId, Long inviterId, Long masqUserId, List<Long> invitedUserIds) {
		saveInvitations(userGroupId, inviterId, masqUserId, invitedUserIds, UserGroupInvitationType.NEW);
	}

	public void saveInvitations(
		final Long userGroupId,
		final Long inviterId,
		final Long masqUserId,
		final List<Long> invitedUserIds,
		final UserGroupInvitationType invitationType) {

		Assert.notNull(invitedUserIds);
		Assert.notNull(userGroupId);
		Assert.notNull(inviterId);

		if (CollectionUtils.isEmpty(invitedUserIds)) {
			logger.warn("No invited user ids provided - nothing to save: groupId=" + userGroupId + " inviterId=" + inviterId);
			return;
		}

		List<FlatRequest> requests = requestService.createFlatInvitesForInviteUsersToGroup(invitedUserIds, inviterId, userGroupId, invitationType);
		requestService.saveInvitesForInviteUsersToGroup(requests);
	}

	@Override
	public void sendInvitations(Long userGroupId, Long inviterId, List<Long> invitedUserIds) {
		Assert.notNull(invitedUserIds);
		Assert.notNull(userGroupId);
		Assert.notNull(inviterId);

		if (CollectionUtils.isEmpty(invitedUserIds)) {
			logger.warn("No invited user ids provided - nothing to send: groupId=" + userGroupId + " inviterId=" + inviterId);
			return;
		}

		requestService.sendInvitesForAddUserToGroup(userGroupId, inviterId, invitedUserIds);
	}

}
