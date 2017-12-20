package com.workmarket.domains.groups.service;

import com.workmarket.domains.groups.model.UserGroup;

public interface GroupInvitationServiceFactory {

	GroupInvitationService getGroupInvitationService(UserGroup userGroup);
}
