package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

/**
 * Created by rahul on 4/14/14
 */
public class WorkDeliverableRejectedNotificationTemplate extends AbstractWorkNotificationTemplate {
	private static final long serialVersionUID = -1034214818461483255L;

	private String assetName;
	private String rejectionReason;

	public WorkDeliverableRejectedNotificationTemplate(Long toId, Work work, String assetName, String rejectionReason) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.RESOURCE_WORK_DELIVERABLE_REJECTED), ReplyToType.TRANSACTIONAL, work);
		this.assetName = assetName;
		this.rejectionReason = rejectionReason;
	}

	public String getAssetName() {
		return assetName;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}
}
