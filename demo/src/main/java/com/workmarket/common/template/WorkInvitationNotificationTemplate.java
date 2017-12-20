package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkInvitationNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = -6269829675339192732L;
	private String companyNumber;
	private int mandatoryRequirementCount;

	public WorkInvitationNotificationTemplate(Long toId, Work work, double distanceInMilesToWork, int mandatoryRequirementCount) {
		super(work.getBuyer().getId(), toId, new NotificationType(NotificationType.RESOURCE_WORK_INVITED), ReplyToType.TRANSACTIONAL_FROM_USER, work, distanceInMilesToWork);

		this.companyNumber = work.getCompany().getEncryptedId();
		this.mandatoryRequirementCount = mandatoryRequirementCount;
	}

	@Override
	public String toString() {
		return "WorkInvitationNotificationTemplate [getDistanceInMilesToWork()=" + getDistanceInMilesToWork() + "]";
	}

	public String getCompanyNumber() {
		return companyNumber;
	}

	public int getMandatoryRequirementCount() {
		return mandatoryRequirementCount;
	}
}
