package com.workmarket.common.template;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkCompletedFundsAddedNotificationTemplate extends AbstractWorkNotificationTemplate {

	/**
	 *
	 */
	private static final long serialVersionUID = 5036747259236414819L;
	private WorkResource resource;
	private boolean hasValidTaxEntity;

	public WorkCompletedFundsAddedNotificationTemplate(Long toId, Work work, WorkResource resource, boolean hasValidTaxEntity) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.RESOURCE_WORK_CLOSED), ReplyToType.TRANSACTIONAL, work);
		this.resource = resource;
		this.hasValidTaxEntity = hasValidTaxEntity;
	}

	public WorkResource getResource() {
		return resource;
	}

	public boolean getHasValidTaxEntity() {
		return hasValidTaxEntity;
	}
}
