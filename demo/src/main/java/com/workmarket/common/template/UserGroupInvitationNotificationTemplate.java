package com.workmarket.common.template;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class UserGroupInvitationNotificationTemplate extends AbstractUserGroupNotificationTemplate {

	private static final long serialVersionUID = -1278596399383234001L;
	private final String companyNumber;
	private String userGroupNumber;
	private boolean vendorInvitation;

	public UserGroupInvitationNotificationTemplate(Long fromId, Long toId, UserGroup group, Company company, boolean vendorInvitation) {
		super(fromId, toId, new NotificationType(NotificationType.GROUP_INVITED), ReplyToType.TRANSACTIONAL_FROM_COMPANY, group, company);

		this.userGroupNumber = group.getEncryptedId();
		this.companyNumber = company.getEncryptedId();
		this.vendorInvitation = vendorInvitation;
	}

	public String getUserGroupNumber() {
		return userGroupNumber;
	}

	public String getCompanyNumber() {
		return companyNumber;
	}

	public boolean isVendorInvitation() {
		return vendorInvitation;
	}

}
