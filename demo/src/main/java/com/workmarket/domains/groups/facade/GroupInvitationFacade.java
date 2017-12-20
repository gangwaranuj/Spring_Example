package com.workmarket.domains.groups.facade;

import com.workmarket.domains.groups.model.UserGroupInvitationType;

import java.util.List;

public interface GroupInvitationFacade {

	void inviteUsersToGroups(Long userGroupId, Long inviterId, Long masqUserId, List<Long> invitedUserIds);

	void inviteUsersToGroups(Long userGroupId, Long inviterId, Long masqUserId, List<Long> invitedUserIds, UserGroupInvitationType invitationType);

}
