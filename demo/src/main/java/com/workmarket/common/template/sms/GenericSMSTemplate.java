package com.workmarket.common.template.sms;

import com.workmarket.domains.model.notification.NotificationType;

public class GenericSMSTemplate extends SMSTemplate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1857830267933992753L;
	private String message;

	public GenericSMSTemplate(Long toId, Long providerId, String toNumber, String message) {
		super(providerId, toNumber);
		this.setToId(toId);
		this.message = message;
		setNotificationType(new NotificationType(NotificationType.MISC));
	}

	public String getMessage() {
		return message;
	}
}