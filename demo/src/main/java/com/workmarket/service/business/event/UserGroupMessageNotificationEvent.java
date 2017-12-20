package com.workmarket.service.business.event;

import com.workmarket.domains.model.Message;

public class UserGroupMessageNotificationEvent extends Event {

	private static final long serialVersionUID = 8783285408795106132L;

	// TODO: Alex - replace reference to entity with id or code
	@Deprecated
	private Message message;
	
	public UserGroupMessageNotificationEvent() {}

	@Deprecated
	public UserGroupMessageNotificationEvent(Message message) {
		this.message = message;
	}

	@Deprecated
	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
}
