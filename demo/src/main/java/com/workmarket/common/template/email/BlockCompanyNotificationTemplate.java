package com.workmarket.common.template.email;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.service.infra.communication.ReplyToType;

public class BlockCompanyNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = -3742653885092433718L;
	private User blockingUser;
	private Company blockedCompany;
	
	public BlockCompanyNotificationTemplate(Long toId, User blockingUser,
			Company blockedCompany) {
		super(blockingUser.getId(), toId, new NotificationType(NotificationType.BLOCK_CLIENT), ReplyToType.TRANSACTIONAL_FROM_USER);
		this.setBlockedCompany(blockedCompany);
		this.setBlockingUser(blockingUser);
	}

	public User getBlockingUser() {
		return blockingUser;
	}

	public void setBlockingUser(User blockingUser) {
		this.blockingUser = blockingUser;
	}

	public Company getBlockedCompany() {
		return blockedCompany;
	}

	public void setBlockedCompany(Company blockedCompany) {
		this.blockedCompany = blockedCompany;
	}

}
