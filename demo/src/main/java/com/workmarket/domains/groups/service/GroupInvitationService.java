package com.workmarket.domains.groups.service;

import com.workmarket.domains.groups.model.UserGroupInvitationType;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface GroupInvitationService {

	@NotNull List<Long> findUsersToInvite(@NotNull Long groupId, @NotNull Long inviterId, @NotNull List<Long> invitedUserIds);

	void saveInvitations(@NotNull Long userGroupId, @NotNull Long inviterId, Long masqUser, @NotNull List<Long> invitedUserIds);

	void saveInvitations(@NotNull Long userGroupId, @NotNull Long inviterId, Long masqUser, @NotNull List<Long> invitedUserIds, UserGroupInvitationType invitationType);

	void sendInvitations(@NotNull Long userGroupId, @NotNull Long inviterId, @NotNull List<Long> invitedUserIds);

	void refreshUserGroups(@NotNull List<Long> invitedUserIds);

	void refreshGroups(@NotNull Long userGroupId);
}
