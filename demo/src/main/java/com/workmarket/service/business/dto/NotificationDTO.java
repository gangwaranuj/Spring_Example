package com.workmarket.service.business.dto;

import java.io.Serializable;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.web.AbstractWebRequestContextAware;

public class NotificationDTO extends AbstractWebRequestContextAware implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long toUserId;
	private Long fromUserId;
	private NotificationType notificationType;
	private Long workId;

	private String msg;

	public Long getToUserId() {
		return toUserId;
	}

	public void setToUserId(Long toUserId) {
		this.toUserId = toUserId;
	}
	
	public Long getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(Long fromUserId) {
		this.fromUserId = fromUserId;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}
	public void setNotificationType(String notificationType) {
		setNotificationType(new NotificationType(notificationType));
	}

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}