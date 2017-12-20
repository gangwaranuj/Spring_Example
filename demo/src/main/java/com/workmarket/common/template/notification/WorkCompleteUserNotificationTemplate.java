package com.workmarket.common.template.notification;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;

public class WorkCompleteUserNotificationTemplate extends UserNotificationTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4662719439517622631L;
	private Work work;

	public void setWork(Work work) {
		this.work = work;
	}

	public Work getWork() {
		return work;
	}
	
	public WorkCompleteUserNotificationTemplate() {
		super();
	}

	public WorkCompleteUserNotificationTemplate(Long toId, NotificationType notificationType, Work work) {
		super(toId, notificationType);
		this.work = work;
	}
	
}
