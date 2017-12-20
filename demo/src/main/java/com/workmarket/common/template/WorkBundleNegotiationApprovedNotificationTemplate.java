package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;

import java.util.Map;

public class WorkBundleNegotiationApprovedNotificationTemplate extends AbstractWorkNegotiationNotificationTemplate {

	private static final long serialVersionUID = -5692579862475672263L;
	final Map<String, Object> bundleData;

	public WorkBundleNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation, Map<String, Object> bundleData) {
		super(toId, work, negotiation, new NotificationType(NotificationType.RESOURCE_WORK_NEGOTIATION_DECISION));
		this.bundleData = bundleData;
	}

	public Map<String, Object> getBundleData() {
		return bundleData;
	}
}