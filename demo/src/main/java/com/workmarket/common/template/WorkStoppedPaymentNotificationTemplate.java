package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkStoppedPaymentNotificationTemplate extends AbstractWorkNotificationTemplate {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8418100516512214371L;
	String reason;

	public WorkStoppedPaymentNotificationTemplate(Long toId, Work work, String reason) {
		super(work.getBuyer().getId(), toId, new NotificationType(NotificationType.RESOURCE_WORK_STOP_PAYMENT), ReplyToType.TRANSACTIONAL_FROM_COMPANY, work);
		this.reason = reason;
	}
	
	public String getReason(){
		return reason;
	}	
}
