package com.workmarket.domains.groups.service;

import com.workmarket.domains.groups.model.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupInvitationServiceFactoryImpl implements GroupInvitationServiceFactory{

	@Autowired PublicGroupInvitationService publicGroupInvitationService;
	@Autowired PrivateGroupInvitationService privateGroupInvitationService;

	public GroupInvitationService getGroupInvitationService(UserGroup userGroup){
		if(userGroup.getOpenMembership()){
			return publicGroupInvitationService;
		}
		return privateGroupInvitationService;
	}
}
