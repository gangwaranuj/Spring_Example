package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.communication.ReplyToType;

import java.util.Map;

public class WorkBundleInvitationNotificationTemplate extends AbstractWorkNotificationTemplate {
	private static final long serialVersionUID = -6269829675339192732L;
	private String companyNumber;
	private Map<String, Object> bundleData;

	public WorkBundleInvitationNotificationTemplate(Long toId, Work work, Map<String, Object> bundleData) {
		// TODO Micah - fix location
		super(work.getBuyer().getId(), toId, new NotificationType(NotificationType.RESOURCE_WORK_INVITED), ReplyToType.TRANSACTIONAL_FROM_USER, work, 0);

		this.bundleData = bundleData;
		this.companyNumber = work.getCompany().getEncryptedId();
	}

	public String getCompanyNumber()
	{
		return companyNumber;
	}

	public Map<String, Object> getBundleData() {
		return bundleData;
	}

	@Override
	public String toString() {
		return "WorkBundleInvitationNotificationTemplate{" +
			"companyNumber='" + companyNumber + '\'' +
			", bundleData=" + bundleData +
			super.toString() +
			'}';
	}
}
