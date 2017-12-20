package com.workmarket.domains.groups.service;

import com.google.common.collect.Lists;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.changelog.user.UserAppliedToGroupChangeLog;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.UserChangeLogService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.summary.SummaryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class PrivateGroupInvitationServiceImpl extends GroupInvitationServiceImpl implements PrivateGroupInvitationService {

	@Component
	static class FactoryHelper {
		List<UserUserGroupAssociation> makeUserAssociations() {
			return Lists.newArrayList();
		}

		List<UserGroupInvitation> makeInvitations() {
			return Lists.newArrayList();
		}
	}

	private static final Log logger = LogFactory.getLog(PrivateGroupInvitationServiceImpl.class);

	@Autowired LaneService laneService;
	@Autowired UserNotificationService userNotificationService;
	@Autowired SummaryService summaryService;
	@Autowired UserChangeLogService userChangeLogService;
	@Autowired PrivateGroupInvitationServiceImpl.FactoryHelper factoryHelper;


	@Override
	public void saveInvitations(Long userGroupId, Long inviterId, Long masqUserId, List<Long> invitedUserIds) {
		Assert.notNull(invitedUserIds);
		Assert.notNull(userGroupId);
		Assert.notNull(inviterId);

		if (CollectionUtils.isEmpty(invitedUserIds)) {
			logger.warn("No users to invite to group " + userGroupId + " by user " + inviterId);
			return;
		}

		// find our group
		UserGroup userGroup = userGroupService.findGroupById(userGroupId);
		Assert.notNull(userGroup);

		// find our inviter
		User masqUser = null;
		if (masqUserId != null) {
			masqUser = userService.getUser(masqUserId);
		}

		// now create our user->user-group association
		List<UserUserGroupAssociation> associations = factoryHelper.makeUserAssociations();
		for (Long invitedUserId : invitedUserIds) {
			List<UserGroupInvitation> invites = factoryHelper.makeInvitations();
			UserUserGroupAssociation association = userGroupService.buildUserUserGroupAssociation(invitedUserId, userGroup, invites);
			associations.add(association);
		}

		// now send the invitations
		for (UserUserGroupAssociation userUserGroupAssociation : associations) {
			User user = userUserGroupAssociation.getUser();
			if (VerificationStatus.VERIFIED.equals(userUserGroupAssociation.getVerificationStatus())) {
				laneService.updateLanesForUserOnGroupApply(user, userGroup);
			}

			Long masqId = masqUser != null ? masqUser.getId() : null;
			userNotificationService.onUserGroupApplication(userUserGroupAssociation);
			userChangeLogService.createChangeLog(new UserAppliedToGroupChangeLog(user.getId(), user.getId(), masqId, userGroup));
			summaryService.saveUserGroupAssociationHistorySummary(userUserGroupAssociation);
		}

	}

	@Override
	public void saveInvitations(
		final Long userGroupId,
		final Long inviterId,
		final Long masqUserId,
		final List<Long> invitedUserIds,
		final UserGroupInvitationType invitationType
	) {
		logger.info(invitationType + " -- invitation type is not used for private group invite");
		saveInvitations(userGroupId, inviterId, masqUserId, invitedUserIds);
	}


	@Override
	public void sendInvitations(Long userGroupId, Long inviterId, List<Long> invitedUserIds) {
		// there is no send for private groups since they are not public
		;
	}

}
