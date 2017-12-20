package com.workmarket.common.template;

import java.util.List;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class ApproveProfileModificationNotificationTemplate extends NotificationTemplate {

	/**
	 *
	 */
	private static final long serialVersionUID = 621914917905968008L;
	private List<String> description;

    public ApproveProfileModificationNotificationTemplate(Long toId, List<String> description) {
    	super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.PROFILE_APPROVED), ReplyToType.TRANSACTIONAL);
    	this.description = description;
    }

	public List<String> getDescription() {
		return description;
	}


}
