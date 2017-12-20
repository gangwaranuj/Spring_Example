package com.workmarket.common.template;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.communication.ReplyToType;

import java.util.Map;

public class WorkBundleAcceptedDetailsNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = -6369589271512936050L;

	private User resource;
	private Map<String, Object> bundleData;


	public WorkBundleAcceptedDetailsNotificationTemplate(Long toId, Work work, User resource, Map<String, Object> bundleData) {
		super(resource.getId(), toId, new NotificationType(NotificationType.RESOURCE_WORK_ACCEPTED_DETAILS), ReplyToType.TRANSACTIONAL, work);
		this.resource = resource;
		this.bundleData = bundleData;
	}

	public User getResource() {
		return resource;
	}

	public Map<String, Object> getBundleData() {
		return bundleData;
	}
}
