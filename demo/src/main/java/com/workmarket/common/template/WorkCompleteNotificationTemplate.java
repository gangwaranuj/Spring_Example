package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkCompleteNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = -303764537380999497L;
	
	private PaymentSummaryDTO paymentSummary;

	public WorkCompleteNotificationTemplate(Long fromId, Long toId, Work work, PaymentSummaryDTO paymentSummary) {
		super(fromId, toId, new NotificationType(NotificationType.WORK_COMPLETED), ReplyToType.TRANSACTIONAL_FROM_USER, work);
		this.paymentSummary = paymentSummary;
	}

	public PaymentSummaryDTO getPaymentSummary() {
		return paymentSummary;
	}
}
