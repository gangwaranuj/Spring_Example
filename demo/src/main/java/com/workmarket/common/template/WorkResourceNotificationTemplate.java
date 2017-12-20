package com.workmarket.common.template;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkResourceNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = -7567408200800610013L;
	private WorkResource workResource;

	protected WorkResourceNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, WorkResource workResource) {
		super(fromId, toId, notificationType, replyToType, workResource.getWork());
		this.workResource = workResource;
	}

	protected WorkResourceNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, Work work) {
		super(fromId, toId, notificationType, replyToType, work);
	}

	protected WorkResourceNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, Work work, WorkResource workResource) {
		super(fromId, toId, notificationType, replyToType, work);
		this.workResource = workResource;
	}

	public WorkResource getWorkResource() {
		return workResource;
	}

	public void setWorkResource(WorkResource workResource) {
		this.workResource = workResource;
	}
}
