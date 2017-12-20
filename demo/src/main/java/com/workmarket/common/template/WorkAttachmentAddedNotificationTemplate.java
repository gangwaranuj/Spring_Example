package com.workmarket.common.template;

import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkAttachmentAddedNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = -6595481416615079023L;
	private Asset asset;

	public WorkAttachmentAddedNotificationTemplate(Long toId, Work work, Asset asset, Long activeWorkerId) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORK_ATTACHMENT_ADDED), ReplyToType.TRANSACTIONAL, work, activeWorkerId);

		if (isToActiveWorker()) {
			setNotificationType(new NotificationType(NotificationType.RESOURCE_WORK_ATTACHMENT_ADDED));
		}

		this.asset = asset;
	}

	public Asset getAsset() {
		return asset;
	}
}
